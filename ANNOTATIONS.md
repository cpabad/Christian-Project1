# ERS — Annotations, and who actually reads them

You will meet `@Entity`, `@Override`, `@Column`, `@Test` and friends on every file this
project's [STARTUP.md](STARTUP.md) and [WALKTHROUGH.md](WALKTHROUGH.md) send you to. They all
look the same — a word with an `@` glued to the next line of code — and that sameness hides
the thing that actually matters:

> **An annotation is inert metadata. It does nothing by itself — ever.** Something else has
> to *read* it, and WHO reads it decides everything: what it can do, when a mistake surfaces,
> and what happens if you delete it. In this codebase there are exactly two kinds of reader:
> **the compiler** (reads it during `mvn package`, then throws it away) and **frameworks
> reflecting over your classes at runtime** (Hibernate, Tomcat, Jackson, JUnit, Mockito).

A natural first guess is that something like `@Override` is imported from a library in
`pom.xml`. It isn't — it lives in `java.lang`, ships with the JDK, and no dependency brings
it in or takes it away. The table below says who *really* reads each one.

**Coming from JavaScript?** A JS/TS decorator is a *function that runs* and can rewrite what
it decorates. A Java annotation is closer to a sticky note: pure data, no behavior. If nothing
ever reads the note (and you'll see a real example of that below), the program is
bit-for-bit identical with or without it.

All paths are under `ReimbursementManagement/src/`. Every anchor and every "observed" claim
below was live-verified against this repo on 2026-07-11; the two deletion experiments show
real captured output.

---

## The map

| Annotation | Who reads it | When | Seen at |
|---|---|---|---|
| `@Override` | **javac** (compiler) | `mvn package`, then discarded | `main/java/com/revature/model/User.java:103` |
| `@Entity`, `@Table` | Hibernate | Tomcat start — SessionFactory build | `User.java:19–20` |
| `@Id`, `@GeneratedValue` | Hibernate | SessionFactory build / on `INSERT` | `User.java:27`, `model/Request.java:29` |
| `@Column` | Hibernate | SessionFactory build (validated against the DB!) | `User.java:32` |
| `@ManyToOne`, `@JoinColumn` | Hibernate | SessionFactory build / row hydration | `User.java:58–59` |
| `@JsonIgnore` | Jackson | **every JSON response** | `User.java:38` |
| `@WebServlet`, `@MultipartConfig` | Tomcat | WAR deploy | `main/java/com/revature/controller/UploadFile.java:31–32` |
| `@Test`, `@Before`, `@BeforeClass` | JUnit 4 runner | `mvn test`, per test class | `test/java/com/revature/service/UserServiceTest.java:28–38` |
| `@Mock`, `@InjectMocks` | Mockito — via `openMocks(this)` | `mvn test`, in each `@Before` | `UserServiceTest.java:25–26` |

Read the **When** column twice. It is the whole mental model: a wrong `@Override` fails your
build; a wrong `@Column` fails Tomcat's *startup*; a missing `@JsonIgnore` fails **silently,
per request, in production** — the later the reader runs, the quieter the failure.

---

## Category 1 — read by the compiler, gone by runtime

### `@Override` — `User.java:103, 115, 153` (119 uses across the repo)

**What it is:** an *assertion you make to javac*: "I believe this method replaces one
inherited from a supertype — fail my build if I'm wrong." It ships with the JDK
(`java.lang.Override`), needs no import, and comes from no pom dependency. It is declared
`@Retention(SOURCE)`: the compiler checks it, then it never even reaches the `.class` file.
At runtime it does not exist.

**Why here:** `User` overrides `hashCode()`, `equals()`, `toString()` from `Object`. The
annotation guards against the classic silent bug: typo the name (`hashcode()`) or the
signature (`equals(User)` instead of `equals(Object)`) and *without* `@Override` you'd have
quietly written a brand-new method nobody calls, while collections keep using `Object`'s.

**What if you delete it? — observed.** Removed line 103's `@Override` and force-recompiled:

```
mvn clean compile   →   BUILD SUCCESS
```

Nothing happens. The code compiles and runs identically — the *protection* is what you
deleted, not behavior. And the assertion in the other direction — `@Override` added to
`getUserId()`, which overrides nothing — observed:

```
[ERROR] User.java:[61,9] method does not override or implement a method from a supertype
BUILD FAILURE
```

That pair of results *is* this category: compile-time annotations can only ever change
whether your build passes, never what the built program does.

> Side-note from running this experiment: this project's old `maven-compiler-plugin` (3.1)
> sometimes answers `Nothing to compile - all classes are up to date` after an edit. If a
> deliberate error doesn't fail the build, run `mvn clean compile` — the observation above
> was captured that way.

---

## Category 2 — read at runtime by reflection

Everything else in this repo is `@Retention(RUNTIME)`: the annotation is written into the
`.class` file, and a framework finds it later by **reflection** (`Class.getAnnotations()` and
friends — the same machinery you met in WALKTHROUGH when Hibernate calls a no-arg
constructor it looked up at runtime). Four different frameworks read four different sets, at
four different moments.

### 2a. Hibernate — reads the JPA annotations when the SessionFactory is built

The moment is precise, and you have already watched it happen: WALKTHROUGH hops 9–13, "first
use — building the singleton SessionFactory". *That* is when every `@Entity` class listed in
`main/resources/hibernate.cfg.xml:16–30` gets reflected over, its annotations turned into a
relational mapping. Not at compile time; not per query — once per JVM.

| | What it says | At |
|---|---|---|
| `@Entity` | "this class maps to a table — manage it" | `User.java:19` |
| `@Table(name = "users", schema = …)` | which table (else it guesses the class name) | `User.java:20` |
| `@Id` | this field is the primary key | `User.java:27` |
| `@GeneratedValue(strategy = IDENTITY)` | the DB assigns the id on INSERT — don't send one | `Request.java:29` |
| `@Column(name = "loginUsername", …)` | field ↔ column mapping | `User.java:32` |
| `@ManyToOne` + `@JoinColumn(name = "roles")` | `users.roles` is a foreign key to the `Role` entity's table — hydrate a `Role` object from the join | `User.java:58–59` |

Two details worth a junior dev's attention:

- **`@Column` with no `name` means "column = field name."** Most fields here rely on that
  (`firstName` → column `firstName` — this schema's columns are camelCase). Only two are
  renamed: `username` → `loginUsername` and `password` → `loginPassword`. The `nullable =
  false` / `unique = true` extras only matter when Hibernate *generates* schema — this app
  never does (`validate`), so the real constraints are the ones in the database DDL.
- **`User` has `@Id` but no `@GeneratedValue`** — user ids come from the seed script, so the
  app must supply them. `Request` (`Request.java:29`) uses `IDENTITY` because rows are
  created at runtime and the database hands out the next id.

**What if you delete one? — observed.** Removed `@Column(name = "loginUsername", …)` from
`User.java:32` and ran a single repository test. Hibernate fell back to the field name and
went looking for a column called `username` — and because `hibernate.cfg.xml:15` sets
`hbm2ddl.auto=validate`, it refused to *start*:

```
org.hibernate.tool.schema.spi.SchemaManagementException:
  Schema-validation: missing column [username] in table [`ExpenseReimbursementManagementSystem`.users]
```

Read that as two lessons in one. (1) The annotation was load-bearing: delete it and the
mapping is silently *different*, not absent. (2) `validate` is why the difference surfaced at
**boot** with a named column, instead of as a `SQLGrammarException` on whatever query first
touched the field — possibly days later. That one config line converts a
runtime-reflection-category failure into an almost-compile-time one; it is the same instinct
as `@Override`, applied to the database.

Deleting `@Entity` itself fails the same way but earlier in the chain — the class is listed
in `hibernate.cfg.xml`, so the SessionFactory build rejects it as an unmapped class. (Argued
from the mechanism above, not run — the experiment budget went to the two non-obvious cases.)

### 2b. Jackson — reads `@JsonIgnore` on every serialization

**`@JsonIgnore` — `User.java:38`, on the `password` field.** Read by Jackson's
`ObjectMapper` inside `FrontController` each time a response is serialized (WALKTHROUGH hop
19: "the `User`'s password never leaves"). This is the **latest possible reader**: nothing
checks it at build, nothing checks it at startup — its work happens per HTTP response, and so
does its failure.

**What if you delete it? — observed, live.** Removed line 38, rebuilt, redeployed, logged in
as `employee2`, and called `GET /app/employee/view-user-information`:

```json
{
    "userId": 3,
    "username": "employee2",
    "password": "$2a$10$l.O.YdrDGKRrZUK9ODD…",   ← the BCrypt hash, on the wire
    "firstName": "C",
    ...
}
```

Status 200. Every test that doesn't inspect this exact JSON stays green. No log line, no
error — just a credential hash handed to every client, forever, until a human notices.
Restored the annotation, rebuilt, redeployed: the field is gone from the response again.
This is the sharpest possible contrast with `@Override`: same syntax, but the reader runs so
late that the *only* safety net is a test that pins the serialized shape.

> The microservice repo hits the same problem with the opposite tool: its
> reimbursement-service maps the `users` table **without a password column at all**, so
> there is nothing to forget to hide. Annotation-as-filter vs. schema-as-filter.

### 2c. Tomcat — reads servlet annotations at WAR deploy

**`@WebServlet("/upload-file")` + `@MultipartConfig` — `UploadFile.java:31–32`.** When Tomcat
explodes the WAR it scans `WEB-INF/classes` for servlet annotations and registers what it
finds — this is the annotation alternative to a `<servlet>` block in
`main/webapp/WEB-INF/web.xml`. This app is a living museum of both styles: `FrontController`
is registered **only** in `web.xml:12–20`, while `UploadFile` is registered **twice** —
`web.xml:48–57` maps it to `/UploadFile`, and the annotation maps it to `/upload-file`.

**Observed (live, both URLs, same deployed WAR):**

```
GET /ReimbursementManagement/UploadFile    →  200  "Served at: /ReimbursementManagement"
GET /ReimbursementManagement/upload-file   →  200  "Served at: /ReimbursementManagement"
```

Both answer — Tomcat created **two servlet registrations of the same class**, one per
source. (The two declarations don't merge, because a `@WebServlet` without a `name` defaults
its identity to the fully-qualified class name, which doesn't match web.xml's
`UploadFile`.) Worth knowing because "I changed the mapping in web.xml, why does the old URL
still work?" is exactly the kind of afternoon this paragraph saves you.

`@MultipartConfig` tells the container to parse `multipart/form-data` bodies so
`request.getPart("myFile")` (`UploadFile.java:46`) works; without it, `getPart` throws at
request time — Tomcat only prepares the parts machinery for servlets that declared it.
(Semantics of the Servlet spec, not run.)

### 2d. Test frameworks — JUnit 4 and Mockito, at `mvn test`

**`@Test`, `@BeforeClass`, `@Before` — `UserServiceTest.java:28–38`.** The JUnit **runner**
reflects over the test class: every `@Test` method becomes a test case, `@BeforeClass` runs
once before the class (so it must be `static`), `@Before` runs before *each* test. Delete a
`@Test` and the method still compiles — it just silently stops being a test, and your suite
count drops by one (the coverage gate in this repo is the thing most likely to catch that).

> **The JUnit 4 vs 5 trap.** This monolith is JUnit 4; the microservice repo is JUnit 5.
> Same ideas, different names — mixing the imports produces tests that compile and never run:
>
> | JUnit 4 (here) | JUnit 5 (microservice) |
> |---|---|
> | `org.junit.Test` | `org.junit.jupiter.api.Test` |
> | `@Before` / `@After` | `@BeforeEach` / `@AfterEach` |
> | `@BeforeClass` / `@AfterClass` | `@BeforeAll` / `@AfterAll` |
> | `@Ignore` | `@Disabled` |
> | test methods must be `public` | package-private is fine |

**`@Mock`, `@InjectMocks` — `UserServiceTest.java:25–26`.** And here the "something must
read it" rule stops being theory, because this repo shows you both outcomes:

- In `UserServiceTest`, line 35 calls **`MockitoAnnotations.openMocks(this)`** in `@Before`.
  *That call is the reader* — it reflects over the test instance, replaces the `@Mock` field
  with a Mockito mock, and injects it into the `@InjectMocks` target. The consumer that is
  usually hidden inside a runner (`@RunWith(MockitoJUnitRunner.class)` — not used here) is a
  visible, greppable line of code.
- In `UserRepositoryTest.java:12`, `@InjectMocks` sits on a field — but the class declares
  **no `@Mock` fields and never calls `openMocks`** (the field is assigned by plain `new` in
  `@BeforeClass`). Nothing ever reads that annotation. It is a genuine sticky note on the
  wall of an empty room: delete it and literally nothing changes. Found in the wild, in this
  repo — proof that annotations carry no magic of their own.

---

## Worked example — `User.java`, every annotation glossed

You met this class in WALKTHROUGH hops 15–16 (hydration). Here it is condensed, with each
annotation labeled by reader (**C** = compiler, **H** = Hibernate at SessionFactory build,
**J** = Jackson per response):

```java
@Entity                                                          // H: manage this class
@Table(name = "users", schema = "\"ExpenseReimbursementManagementSystem\"") // H: that table
public class User {

    @Column                                                      // H: column = field name ("userId")
    @Id                                                          // H: primary key (no @GeneratedValue: seed assigns ids)
    private int userId;

    @Column(name = "loginUsername", nullable = false, unique = true) // H: field ≠ column — the renamed one
    private String username;                                     //    (delete name= → boot fails: "missing column [username]")

    @Column(name = "loginPassword", nullable = false)            // H: the other renamed column
    @JsonIgnore                                                  // J: never serialize (delete → hash on the wire, silently)
    private String password;

    @Column(nullable = false)                                    // H: column "firstName" (implicit)
    private String firstName;
    // … lastName, email: same pattern
    @ManyToOne                                                   // H: users.roles → Role entity
    @JoinColumn(name = "roles")                                  //    FK column; hydration builds Role from the join
    private Role role;

    @Override                                                    // C: assert this replaces Object.hashCode — erased after compile
    public int hashCode() { … }
    @Override public boolean equals(Object obj) { … }            // C
    @Override public String toString() { … }                     // C: note password=[PROTECTED] — @JsonIgnore does NOT cover toString/logs

    public User() { … }   // no annotation at all — but Hibernate requires it (reflective hydration, WALKTHROUGH hop 15)
}
```

Three readers in one file, three failure times: compile (`@Override`), Tomcat boot
(`@Column`), silent production response (`@JsonIgnore`).

---

## Everything else

That's every annotation on the STARTUP/WALKTHROUGH reading paths plus the confusing ones. For
any straggler you meet elsewhere, the question to ask is always the same — *who reads this,
and when?* — and the answer is in its Javadoc: [JPA annotations](https://jakarta.ee/specifications/persistence/)
(`javax.persistence.*` in this Java-8 era codebase), [Jackson annotations](https://github.com/FasterXML/jackson-annotations),
[JUnit 4](https://junit.org/junit4/javadoc/latest/), [Mockito](https://javadoc.io/doc/org.mockito/mockito-core/latest/index.html),
and `java.lang.annotation.Retention` for the SOURCE/CLASS/RUNTIME machinery underneath it all.
