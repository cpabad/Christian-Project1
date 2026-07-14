# WALKTHROUGH - one request, end to end

The prose twin of a FLOW trace: the employee login (`POST /app/employee/login`,
`employee2` / `employeePassword`) narrated through every class it touches, in the order it
touches them. The hop numbers `[3f0a|1..20]` refer to the real captured trace below - read
this document with that trace beside it, or better, with your own live one
(`STARTUP.md` -> "Watch the app think").

All paths are under `ReimbursementManagement/src/main/`.

## The captured trace

```
FLOW [3f0a|1]  SessionFilter → POST /ReimbursementManagement/app/employee/login received; session: none
FLOW [3f0a|2]  SessionFilter → auth check: PASS - continuing down the filter chain
FLOW [3f0a|3]  EmployeeFilter → role check: EXEMPT (login URL, no session required yet) - continuing down the filter chain
FLOW [3f0a|4]  FrontController → doPost received ... - write-auth check: PASS (session exists or login URL)
FLOW [3f0a|5]  RequestHelper → routing POST /employee/login through the switch
FLOW [3f0a|6]  RequestHelper → matched /employee/login - calling UserService.authenticate for 'employee2'
FLOW [3f0a|7]  UserService → authenticate: identifier 'employee2' has no '@' - treating it as a username
FLOW [3f0a|8]  UserRepositoryImpl → findByUsername: opening a Hibernate session + transaction
FLOW [3f0a|9]  HibernateSessionFactory → first use - building the singleton SessionFactory ... (expensive, happens once)
FLOW [3f0a|10] Request → no-arg constructor fired (Hibernate hydration builds entities this way)
FLOW [3f0a|11] AmazonS3Object → no-arg constructor fired ...
FLOW [3f0a|12] SupervisorApproval → no-arg constructor fired ...
FLOW [3f0a|13] Reimbursement → no-arg constructor fired ...
FLOW [3f0a|14] HibernateSessionFactory → handing the current thread-bound Hibernate Session to the caller
FLOW [3f0a|15] User → no-arg constructor fired ...
FLOW [3f0a|16] Role → no-arg constructor fired ...
FLOW [3f0a|17] UserService → authenticate outcome: BCrypt verified the password for 'employee2' - returning the user
FLOW [3f0a|18] RequestHelper → login decision: SUCCESS for 'employee2' (role Employee) - creating the session
FLOW [3f0a|19] FrontController → result serialized to JSON; information is being sent to the client (status 200)
FLOW [3f0a|20] SessionFilter → response on its way to the client - request frame ends
```

## Before hop 1 - the browser

The Employee Login button's handler lives in the inline `<script>` of `webapp/index.html`.
It reads the two input fields and fires
`POST /ReimbursementManagement/app/employee/login?username=...&password=...` with
`XMLHttpRequest`. Note the credentials travel as query parameters - servlet-era idiom; the
server reads them with `request.getParameter(...)` either way.

Tomcat receives the request on a thread from its **thread pool** - a recycled thread that has
served other requests before and will serve unrelated ones after. That single fact drives the
cleanup discipline in the next paragraph.

## Hops 1-3 - the filter chain

**`SessionFilter`** (`java/com/revature/filter/SessionFilter.java`, mapped to `/*`) is the
front door; every request enters here first. It does two jobs:

1. **Opens the FLOW frame** - `FlowTrace.begin()` binds a 4-hex request id (`3f0a`) to the
   current thread via Log4j2's `ThreadContext`, and the matching `end()` sits in a `finally`.
   Without that `finally`, the pooled thread would carry `3f0a` into whatever request it
   serves next - the classic ThreadLocal leak.
2. **The auth check.** A GET to a protected page with no session would be forwarded to the
   deny view here. Our request is a login POST - the exempt list lets it pass (hop 2), and
   authentication of writes is FrontController's job anyway (hop 4).

**`EmployeeFilter`** (mapped to `/app/employee/*`) guards role, not authentication: only a
session whose role is `Employee` may use employee endpoints. But we don't *have* a session
yet - which is exactly why login URLs are role-exempt (hop 3). Chicken-and-egg resolved by
exemption. (`ManagerFilter` mirrors this for `/app/manager/*`.)

Each filter ends with `chain.doFilter(...)` - "continue down the chain" - and each one
`return`s immediately after any `forward`, never both. (That double-dispatch bug is one of
the things the 2026-06 hardening fixed; see CHANGELOG.)

## Hop 4 - FrontController

**`FrontController`** (`java/com/revature/controller/FrontController.java`) is the single
servlet behind `/app/*`. Its `doPost` applies the **write-auth rule**: POST/PUT/DELETE
require a session, unless the URL is a login. We are a login, so: PASS. It then delegates
everything to `RequestHelper.processPost(...)` - the FrontController pattern in its
textbook form: one entry point, routing decided in one place, no per-endpoint servlets.

## Hops 5-6 - RequestHelper, the router

**`RequestHelper`** (`controller/RequestHelper.java`) strips the context prefix and switches
on what remains: `case "/employee/login": case "/manager/login":` - one fall-through case,
because the only difference between the two logins is a role gate applied *after*
authentication. It pulls `username` and `password` from the request and calls
**`UserService.authenticate`**. The controller's whole job is: parse HTTP, delegate, map the
outcome back to HTTP. The business rule lives one layer down.

## Hop 7 - UserService, the first business decision

**`UserService.authenticate(usernameOrEmail, rawPassword)`**
(`java/com/revature/service/UserService.java`): the identifier `employee2` contains no `@`,
so it is treated as a username -> `findByUsername`. (This rule replaced a five-clause TLD
heuristic whose operator-precedence bug broke logins for years - CHANGELOG, "login and
profile-update logic".) An `@` would have routed to `findByEmail` instead - the first fork
you should be able to predict cold.

## Hops 8-16 - the persistence dive

**`UserRepositoryImpl.findByUsername`** (`java/com/revature/repository/`) opens a Hibernate
session and transaction (hop 8) - and because this is the *first query since Tomcat started*,
hop 9 interrupts: **`HibernateSessionFactory`** (`util/`) builds the singleton
`SessionFactory` from `hibernate.cfg.xml` plus the `dburl`/`dbuser`/`dbpassword` env vars.
Expensive, once per JVM.

Hops 10-13 are the bootstrap's signature: `Request`, `AmazonS3Object`, `SupervisorApproval`,
`Reimbursement` no-arg constructors fire for entities *nobody queried* - the SessionFactory
probing its mapped classes (metadata/proxy setup). **Bootstrap noise: recognize it, never
grade yourself on it.** On every later query you'll see only the hydration that row actually
needs.

Hops 15-16 are real hydration: the `users` row comes back, and Hibernate builds the object
the reflective way - no-arg constructor first (`User`, then its `Role` from the join), then
populate fields. This is why every Hibernate entity must keep a no-arg constructor. (How
Hibernate knows which class maps to which table and column is the `@Entity`/`@Column`
annotations on `User` - read at SessionFactory build, hop 9. [ANNOTATIONS.md](ANNOTATIONS.md)
walks all of them, including what breaks when one is deleted.)

## Hop 17 - BCrypt

Back in `UserService`: `BCryptPasswordEncoder.matches(rawPassword, storedHash)` verifies the
password against the bcrypt hash seeded in the database (plaintext storage died in the
2026-06 pass). Success -> return the `User`. Had it failed, the service would return `null` -
deliberately indistinguishable from "unknown user", so a caller can't probe which usernames
exist.

## Hops 18-20 - decision, session, response

`RequestHelper` (hop 18) runs the login decision tree in order:

1. `user == null`? -> 400 "Invalid Credentials" (wrong password / unknown user, same answer).
2. Manager login but role isn't Supervisor? -> same 400 (role gate, `/manager/login` only).
3. Session already exists? -> 400 "You already have a current session." (checked only
   *after* credentials prove who's asking - the session hint isn't owed to strangers).
4. Otherwise: `request.getSession()` **creates** the session (Tomcat issues the JSESSIONID
   cookie), and userId/username/email/role go in as attributes - the state every later
   request's filters read at *their* hop 1.

`FrontController` (hop 19) serializes the return value to JSON with Jackson (the `User`'s
password never leaves - `@JsonIgnore`; [ANNOTATIONS.md](ANNOTATIONS.md) shows the live capture
of the hash leaking when that one line is deleted) and answers 200. `SessionFilter`'s `finally` (hop 20)
closes the frame and clears the ThreadContext: the pooled thread is clean for its next,
unrelated request.

The browser's `onreadystatechange` sees 200 and navigates to `employeehomepage.html` - which
is a *new* request, with a *new* FLOW id, whose SessionFilter line now reads
`session: active (role=Employee)`. That one-line difference is the whole point of login.

## The forks you should be able to predict cold

| Input | Departs the happy path at | Outcome |
|---|---|---|
| Wrong password | hop 17 (BCrypt fails) | 400 "Invalid Credentials" |
| Unknown username | hops 15-16 never fire (no row) | same 400 - indistinguishable by design |
| `employee2` on Manager Login | hop 18, rule 2 (role gate) | same 400 |
| Valid login, session already open | hop 18, rule 3 | 400 "You already have a current session." |
| Identifier contains `@` | hop 7 | same path via `findByEmail` |
| Second login after Tomcat restart | hops 9-13 absent | 14 hops, not 20 - bootstrap is gone |

## The cast, one line each

| Class | Layer | Job in this request |
|---|---|---|
| `SessionFilter` | filter | frame owner; auth gate for GETs |
| `EmployeeFilter` | filter | role gate; login-exempt |
| `FrontController` | controller | single `/app/*` servlet; write-auth; JSON out |
| `RequestHelper` | controller | URL switch; login decision tree; session creation |
| `UserService` | service | email-vs-username rule; BCrypt verdict |
| `UserRepositoryImpl` | repository | the actual query, session + transaction |
| `HibernateSessionFactory` | util | singleton SessionFactory; thread-bound sessions |
| `User`, `Role` | model | hydrated entities (no-arg ctor + populate) |

## The same walkthrough, run by CI — Build Now

Everything above was one request, traced by hand. Jenkins runs the *whole* verification —
fresh clone, containerized JDK-8 build, a throwaway seeded database, all 125 tests (this login
path among them), plus three security scans — every time you push, and on demand when you
press one button. Setup lives in [JENKINS.md](JENKINS.md) and [STARTUP.md](STARTUP.md)
("Jenkins — the teammate who never goes home"); the pipeline itself is the commented
`Jenkinsfile` at this repo's root.

The routine, once the job exists:

1. Open http://localhost:8090 and select the `ers-monolith` job.
2. Press **Build Now**. The stage view fills in left to right: *Build → Tests →
   SCA → SAST → Secrets*. Click the running build → **Console Output** to watch it narrate —
   the CI cousin of the FLOW trace you just read.
3. Read the result. **SUCCESS** means the app still builds, passes every test, and scans
   clean, from scratch, on a clean environment — compressed into one verdict. **UNSTABLE**
   means a security scan reported findings and wants triage (the warn-then-ratchet policy —
   see the Jenkinsfile header). **FAILURE** means the build or a test broke, or a secret was
   detected; the Discord notification names the commit author.

One honest footnote on the Build button: repeated clicks just queue identical builds — one
click is one verdict, and SCM polling already builds every push without being asked.
