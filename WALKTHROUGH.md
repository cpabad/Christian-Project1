# WALKTHROUGH - one request, end to end

The prose twin of a FLOW trace: the login (`POST /app/login`, `employee2` /
`employeePassword`) narrated through every class it touches, in the order it touches them.
The hop numbers `[2283|1..19]` refer to the real captured trace below - read this document
with that trace beside it, or better, with your own live one
(`STARTUP.md` -> "Watch the app think").

All paths are under `ReimbursementManagement/src/main/`.

## The captured trace

```
FLOW [2283|1]  SessionFilter → POST /ReimbursementManagement/app/login received; session: none
FLOW [2283|2]  SessionFilter → auth check: PASS - continuing down the filter chain
FLOW [2283|3]  FrontController → doPost received POST /ReimbursementManagement/app/login - write-auth check: PASS (session exists or login URL)
FLOW [2283|4]  RequestHelper → routing POST /login through the switch
FLOW [2283|5]  RequestHelper → matched /login - calling UserService.authenticate for 'employee2'
FLOW [2283|6]  UserService → authenticate: identifier 'employee2' has no '@' - treating it as a username
FLOW [2283|7]  UserRepositoryImpl → findByUsername: opening a Hibernate session + transaction
FLOW [2283|8]  HibernateSessionFactory → first use - building the singleton SessionFactory ... (expensive, happens once)
FLOW [2283|9]  Request → no-arg constructor fired (Hibernate hydration builds entities this way)
FLOW [2283|10] AmazonS3Object → no-arg constructor fired ...
FLOW [2283|11] SupervisorApproval → no-arg constructor fired ...
FLOW [2283|12] Reimbursement → no-arg constructor fired ...
FLOW [2283|13] HibernateSessionFactory → handing the current thread-bound Hibernate Session to the caller
FLOW [2283|14] User → no-arg constructor fired ...
FLOW [2283|15] Role → no-arg constructor fired ...
FLOW [2283|16] UserService → authenticate outcome: BCrypt verified the password for 'employee2' - returning the user
FLOW [2283|17] RequestHelper → login decision: SUCCESS for 'employee2' (role Employee) - creating the session
FLOW [2283|18] FrontController → result serialized to JSON; information is being sent to the client (status 200)
FLOW [2283|19] SessionFilter → response on its way to the client - request frame ends
```

## Before hop 1 - the browser

The Login button's handler lives in the inline `<script>` of `webapp/index.html`. One
button for both roles: the client does not know - and is not trusted to decide - who you
are. It reads the two input fields and fires `POST /ReimbursementManagement/app/login` with
`XMLHttpRequest`, sending `username` and `password` **form-encoded in the POST body**. The
body, not the query string, on purpose: URLs (query string included) are written to Tomcat
access logs and any proxy in between; POST bodies are not. Server-side the choice is
invisible - `request.getParameter(...)` reads both sources the same way.

Tomcat receives the request on a thread from its **thread pool** - a recycled thread that has
served other requests before and will serve unrelated ones after. That single fact drives the
cleanup discipline in the next paragraph.

## Hops 1-2 - the filter chain

**`SessionFilter`** (`java/com/revature/filter/SessionFilter.java`, mapped to `/*`) is the
front door; every request enters here first. It does two jobs:

1. **Opens the FLOW frame** - `FlowTrace.begin()` binds a 4-hex request id (`2283`) to the
   current thread via Log4j2's `ThreadContext`, and the matching `end()` sits in a `finally`.
   Without that `finally`, the pooled thread would carry `2283` into whatever request it
   serves next - the classic ThreadLocal leak.
2. **The auth check.** A GET to a protected page with no session would be forwarded to the
   deny view here. Our request is a login POST - the exempt list lets it pass (hop 2), and
   authentication of writes is FrontController's job anyway (hop 3).

Notice who *doesn't* speak: **`EmployeeFilter`** (mapped to `/app/employee/*`) and
**`ManagerFilter`** (`/app/manager/*`) guard role, and `/app/login` sits outside both of
their mappings, so neither filter runs at all. Before the 2026-07 consolidation, login lived
*inside* the guarded territory (`/app/employee/login`, `/app/manager/login`), which forced a
chicken-and-egg exemption into each role filter - "a role check needs a session, but login
is how you get one, so login URLs pass". Moving login to a neutral URL dissolved that knot
on this path: the role filters now fire only where a role can actually exist.

Each filter ends with `chain.doFilter(...)` - "continue down the chain" - and each one
`return`s immediately after any `forward`, never both. (That double-dispatch bug is one of
the things the 2026-06 hardening fixed; see CHANGELOG.)

## Hop 3 - FrontController

**`FrontController`** (`java/com/revature/controller/FrontController.java`) is the single
servlet behind `/app/*`. Its `doPost` applies the **write-auth rule**: POST/PUT/DELETE
require a session, unless the URL is a login. We are a login, so: PASS. It then delegates
everything to `RequestHelper.processPost(...)` - the FrontController pattern in its
textbook form: one entry point, routing decided in one place, no per-endpoint servlets.

## Hops 4-5 - RequestHelper, the router

**`RequestHelper`** (`controller/RequestHelper.java`) strips the context prefix and switches
on what remains: `case "/login":` - one case, one login. (Until 2026-07 this was a
fall-through pair, `/employee/login` + `/manager/login`, whose only difference was a role
gate on the manager variant that rejected non-Supervisors *after* authenticating them. The
gate answered the wrong question: login establishes WHO you are; whether you may use
`/manager/` endpoints is authorization, and `ManagerFilter` already enforces that on every
manager request. One endpoint, no misplaced access control.) It pulls `username` and
`password` from the request and calls **`UserService.authenticate`**. The controller's whole
job is: parse HTTP, delegate, map the outcome back to HTTP. The business rule lives one
layer down.

## Hop 6 - UserService, the first business decision

**`UserService.authenticate(usernameOrEmail, rawPassword)`**
(`java/com/revature/service/UserService.java`): the identifier `employee2` contains no `@`,
so it is treated as a username -> `findByUsername`. (This rule replaced a five-clause TLD
heuristic whose operator-precedence bug broke logins for years - CHANGELOG, "login and
profile-update logic".) An `@` would have routed to `findByEmail` instead - the first fork
you should be able to predict cold.

## Hops 7-15 - the persistence dive

**`UserRepositoryImpl.findByUsername`** (`java/com/revature/repository/`) opens a Hibernate
session and transaction (hop 7) - and because this is the *first query since Tomcat started*,
hop 8 interrupts: **`HibernateSessionFactory`** (`util/`) builds the singleton
`SessionFactory` from `hibernate.cfg.xml` plus the `dburl`/`dbuser`/`dbpassword` env vars.
Expensive, once per JVM.

Hops 9-12 are the bootstrap's signature: `Request`, `AmazonS3Object`, `SupervisorApproval`,
`Reimbursement` no-arg constructors fire for entities *nobody queried* - the SessionFactory
probing its mapped classes (metadata/proxy setup). **Bootstrap noise: recognize it, never
grade yourself on it.** On every later query you'll see only the hydration that row actually
needs.

Hops 14-15 are real hydration: the `users` row comes back, and Hibernate builds the object
the reflective way - no-arg constructor first (`User`, then its `Role` from the join), then
populate fields. This is why every Hibernate entity must keep a no-arg constructor. (How
Hibernate knows which class maps to which table and column is the `@Entity`/`@Column`
annotations on `User` - read at SessionFactory build, hop 8. [ANNOTATIONS.md](ANNOTATIONS.md)
walks all of them, including what breaks when one is deleted.)

## Hop 16 - BCrypt

Back in `UserService`: `BCryptPasswordEncoder.matches(rawPassword, storedHash)` verifies the
password against the bcrypt hash seeded in the database (plaintext storage died in the
2026-06 pass). Success -> return the `User`. Had it failed, the service would return `null` -
deliberately indistinguishable from "unknown user", so a caller can't probe which usernames
exist.

## Hops 17-19 - decision, session, response

`RequestHelper` (hop 17) runs the login decision tree in order:

1. `user == null`? -> 400 "Invalid Credentials" (wrong password / unknown user, same answer).
2. Session already exists? -> 400 "You already have a current session." (checked only
   *after* credentials prove who's asking - the session hint isn't owed to strangers).
3. Otherwise: `request.getSession()` **creates** the session (Tomcat issues the JSESSIONID
   cookie), userId/username/email/role go in as attributes - the state every later request's
   filters read at *their* hop 1 - and the method returns the user's **role string**. That
   return value is the whole answer to "which page do I get?".

`FrontController` (hop 18) serializes the return value to JSON with Jackson and answers 200.
Serializing a plain string quotes it: the body on the wire is `"Employee"`, quotes included -
which is why the page JS `JSON.parse`s the response instead of comparing raw text. (For
endpoints that return a `User`, the same Jackson pass is where `@JsonIgnore` keeps the
password hash from ever leaving - [ANNOTATIONS.md](ANNOTATIONS.md) shows the live capture of
the hash leaking when that one line is deleted.) `SessionFilter`'s `finally` (hop 19) closes
the frame and clears the ThreadContext: the pooled thread is clean for its next, unrelated
request.

The browser's `onreadystatechange` sees 200 and routes on the role in the body -
`"Supervisor"` opens `supervisorhomepage.html`, anything else `employeehomepage.html`. That
navigation is a *new* request, with a *new* FLOW id, whose SessionFilter line now reads
`session: active (role=Employee)`. That one-line difference is the whole point of login.

Be precise about what the role in the body is *for*: **navigation, not access control**. A
tampered client that opens the supervisor page as an Employee changes nothing - every
`/app/manager/*` call it makes still hits `ManagerFilter`, which reads the role from the
server-side session and answers 401. The browser picks the door; the server guards the rooms.

## The forks you should be able to predict cold

| Input | Departs the happy path at | Outcome |
|---|---|---|
| Wrong password | hop 16 (BCrypt fails) | 400 "Invalid Credentials" |
| Unknown username | hops 14-15 never fire (no row) | same 400 - indistinguishable by design |
| `employee1` (a Supervisor) | never departs - same 19 hops | 200 with body `"Supervisor"`; the JS opens the supervisor homepage |
| Valid login, session already open | hop 17, rule 2 | 400 "You already have a current session." |
| Identifier contains `@` | hop 6 | same path via `findByEmail` |
| Second login after Tomcat restart | hops 8-12 absent | 14 hops, not 19 - bootstrap is gone |

## The cast, one line each

| Class | Layer | Job in this request |
|---|---|---|
| `SessionFilter` | filter | frame owner; auth gate for GETs |
| `FrontController` | controller | single `/app/*` servlet; write-auth; JSON out |
| `RequestHelper` | controller | URL switch; login decision tree; session creation |
| `UserService` | service | email-vs-username rule; BCrypt verdict |
| `UserRepositoryImpl` | repository | the actual query, session + transaction |
| `HibernateSessionFactory` | util | singleton SessionFactory; thread-bound sessions |
| `User`, `Role` | model | hydrated entities (no-arg ctor + populate) |

Absent by design: `EmployeeFilter` and `ManagerFilter`. `/app/login` sits outside both of
their URL mappings; they enter the story on the *next* request, once the role they guard
exists in a session.

## The same walkthrough, run by CI — Build Now

Everything above was one request, traced by hand. Jenkins runs the *whole* verification —
fresh clone, containerized JDK-8 build, a throwaway seeded database, the full test suite (this
login path among them), plus three security scans — every time you push, and on demand when you
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
