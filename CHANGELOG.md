# Changelog

A record of refreshing the **Employee Reimbursement System (ERS)** - a Java 8 / JDBC /
Hibernate / standalone-Tomcat project - after roughly five years away. The goals were to
get it building again on its target JDK, make its advertised claims true and reproducible
(>= 98% service-layer test coverage, 5NF database design), and remove known vulnerabilities.

## Purpose
A durable, self-contained record of what changed in this refresh and why - so the project's
intent and current state stay clear even without the local repository, and so this codebase is
approachable to return to instead of an intimidating pile of old, possibly-vulnerable code.
Each entry gives the original problem, its root cause, the fix, and the takeaway.

---

## 2026-06 - Refresh

### Build - the project no longer compiled on Java 8
- **Symptom.** `mvn clean package` failed with 18 `cannot find symbol: method isBlank()` errors in `RequestHelper.java`.
- **Root cause.** `String.isBlank()` is a Java 11 API. The POM declares `source`/`target` 1.8, but those only set the language level and the bytecode version - they do **not** restrict which JDK library methods are visible. The project had been compiled in Eclipse against a JRE 11+, where `isBlank()` resolved fine; on a real Java 8 `javac` it does not exist. The "Java 8" label was never actually enforced.
- **Resolution.** Backported the 14 `isBlank()` calls to `trim().isEmpty()` (equivalent for normal form input).
- **Takeaway.** `source`/`target` is not the same as the API surface. The modern guard is `maven.compiler.release`, which compiles against the named JDK's API and would have caught this at the original build.

### Database - the seed script had drifted from the tests
- **Symptom.** The 14 repository integration tests failed - first with 36 `NullPointerException`s (no database wired up), then, once a seeded PostgreSQL was stood up, with assertion mismatches such as `expected "Topology Crash Course" but was "Magic Tricks Boot Camp"`.
- **Root cause.** The committed `ers_script.sql` no longer matched what the tests expected. Three things had drifted: request #1's requester, a stray supervisor-approval row for request #3, and the ordering of employee #4's hierarchy rows. The repository queries also have no `ORDER BY`, so multi-row assertions depend on insertion order.
- **Resolution.** Treated the tests as the source of truth and corrected the three seed rows; all 75 tests then passed.
- **Verification.** Separately confirmed the database design is in BCNF / 4NF / 5NF, and diffed the live schema against the committed DDL to prove zero structural drift.
- **Takeaway.** When tests and fixtures disagree, the tests usually encode the intended contract. Order-dependent assertions without an `ORDER BY` are latent flakiness.

### Tests - "98% service coverage" was aspirational
- **Symptom.** Measured with JaCoCo, service-layer coverage was ~65%, not the advertised 98%.
- **Root cause.** The codebase had grown past its tests: `AmazonS3ObjectService` had no test at all, and `SupervisorApprovalService` sat at 12.5% (3 of 11 methods). The original figure likely predated that growth and excluded AWS-touching code - though the service layer turned out to contain none (the AWS SDK is isolated in `util`/`controller`).
- **Resolution.** Added Mockito unit tests for the untested methods, bringing the service layer to **98.06%** (556/567 instructions) and the suite from 75 to 97 tests. Bound JaCoCo in the POM so a plain `mvn test` regenerates the report, and documented the denominator in `COVERAGE.md`.
- **Takeaway.** A coverage claim only means something if it is reproducible and its denominator is stated. Service logic is unit-tested by mocking the repository and constructing plain in-memory objects - you mock one collaborator, not "many tables"; the database belongs to the repository's integration tests.

### Security - Log4Shell (CVE-2021-44228)
- **Symptom.** `log4j-core 2.14.0` on the classpath.
- **Root cause.** 2.14.0 sits inside the Log4Shell range, and user-controlled data reaches the logger (the request URI and usernames are logged) - a reachable exploitation sink, not merely a flagged version.
- **Resolution.** Bumped to **2.17.1**, the first release clear of the whole cluster (CVE-2021-44228 / 45046 / 45105 / 44832) and still Java 8 compatible. No logging-config change was needed.
- **Takeaway.** A vulnerable dependency plus a reachable sink is the difference between "theoretical" and "exploitable."

### Security - passwords were exposed on multiple paths
- **JSON responses.** `User` objects were serialized straight to clients (for example `/employee/view-user-information`) with the plaintext `password` field included. Fixed with `@JsonIgnore`.
- **Logs.** `User.toString()` printed the password, leaking it into any log line that stringified a user. Fixed by masking it to `[PROTECTED]`.
- **Root-cause lesson.** `private` is not "secret." It is a compile-time visibility rule that governs which other Java *source code* may name the field; it does nothing to stop a serializer (which reads the public getter, or the field directly via reflection), a logger, or a debugger from reading the value. Confidentiality is about controlling where the value *travels*, not who can name the variable.
- **Still planned.** Storage hashing - the passwords are still stored and compared in plaintext (see Planned).

### Security - SessionFilter authorization was a dead no-op
- **Symptom.** The filter meant to keep employees out of supervisor views never did anything.
- **Root cause (three bugs).**
  1. The role was compared with `==` (object identity) instead of `.equals()`. The role string came from the database, so it was never the same object as the literal `"Employee"`, leaving the condition perpetually false. (String *literals* are interned, so `==` accidentally works between two literals - which is exactly what hides this trap.)
  2. It matched `"supervisor"` in the URL, but the supervisor screens live under `/manager/`, so the check never matched the real URLs in the first place.
  3. `chain.doFilter()` ran even after a `forward()`, double-dispatching the request.
- **Resolution.** `"Employee".equals(...)`, `contains("/manager/")`, and a `return` after each forward. (The same rule was already correctly enforced by `ManagerFilter`; this is now a corrected second layer.)
- **Takeaway.** `==` compares references, not text; and after a filter forwards or redirects it must `return` rather than continue the chain. The production-grade path is to stop hand-rolling auth filters and adopt Spring Security.

### Security - CORS was wide open, and the frontend hard-coded its host
- **Symptom.** `web.xml` enabled Tomcat's `CorsFilter` with `Access-Control-Allow-Origin: *` (any website could call the API), and every frontend call hard-coded `http://localhost:8080/ReimbursementManagement/...`.
- **Root cause.** The frontend had hit CORS errors during development and the wildcard was the quick way to silence them - but the frontend (HTML/JS/CSS) and the API are served by the *same* WAR, so they are same-origin and CORS was never needed. The wildcard was an unnecessary workaround that shipped; the hard-coded host also broke the frontend anywhere other than `localhost:8080`.
- **Resolution.** Made all frontend calls root-relative (`/ReimbursementManagement/...`) so they are same-origin and host-independent, and removed the `CorsFilter` entirely. A comment in `web.xml` records how to reintroduce CORS - scoped to a specific origin, never a wildcard - if a separately hosted client is ever added.
- **Takeaway.** CORS is the browser guarding *cross-origin* responses; a wildcard "fixes" the error by removing the guard for everyone. The durable fix is to remove the cross-origin-ness (same-origin, or a dev proxy), and never pair `*` with credentials.

### Security - EmployeeFilter enforced nothing
- **Symptom.** `EmployeeFilter` (mapped to `/app/employee/*`) had its entire body commented out except a bare `chain.doFilter(...)`, so it applied no access control - any authenticated user, and even unauthenticated non-GET requests, reached employee endpoints.
- **Root cause.** The intended logic was commented out and itself buggy: it compared the role with `==` (the same trap as `SessionFilter`) and matched only two exact URLs.
- **Resolution.** Reworked it into the mirror of `ManagerFilter`: `/app/employee/*` now requires a session with the `Employee` role (login URLs exempt), and everyone else is forwarded to the deny view.
- **Takeaway.** A filter that is mapped but does nothing is worse than none - it reads as protection that is not actually there.

### Security - passwords are now hashed (BCrypt), not plaintext
- **Symptom.** Passwords were stored and compared as plaintext (`password.equals(...)`), and the seed shipped real plaintext values. A database or backup leak would have handed over every credential directly.
- **Root cause.** No hashing was ever applied; the only "hash" in the code was `User.hashCode()`, which is object-bucketing for collections, not cryptographic.
- **Resolution.** Added `spring-security-crypto` and switched to `BCryptPasswordEncoder`: the two logins and the two profile old-password checks now verify with `matches(raw, storedHash)`, and password writes use `encode(...)`. Seed passwords were replaced with per-user bcrypt hashes. `password` was also dropped from `User.equals`/`hashCode` - it is not an identity attribute, and removing it keeps entity equality valid once the stored value is an opaque hash.
- **Takeaway.** Never store a password - store a one-way, salted, slow (adaptive) hash and verify with `matches()`. Access modifiers and `hashCode()` are not security; bcrypt is. No schema change was needed (the `loginPassword` column already held a `varchar`), so the 5NF design is unaffected.

### Security - patched the remaining dated dependencies
- **Symptom.** Several libraries were years behind and carried known CVEs.
- **Resolution.** Bumped each to the latest patch on its existing line (lowest-risk): `jackson-databind` 2.12.1 -> 2.12.7.1 (CVE-2022-42003 / 42004), `postgresql` 42.2.18 -> 42.2.27 (CVE-2022-21724 / 26520), `hibernate-core` and `hibernate-ehcache` 5.4.28 -> 5.4.33, `tomcat-catalina` 8.5.61 -> 8.5.100. All 97 tests stayed green; the repository integration tests exercise Hibernate and the Postgres driver, so an ORM or driver regression would have surfaced.
- **Takeaway.** Staying on the latest patch of a dependency's existing line absorbs security fixes with minimal behavioral churn - a safer first move than jumping major versions.

### Security - write-path authentication and cleanup
- **`FrontController`.** `doPut`/`doDelete` had a tautological `if (method.equals("PUT"))` "check" that never rejected anything. Replaced it with a real one: POST/PUT/DELETE now require a session (login URLs exempt), so an unauthenticated write returns 401 instead of being processed. GET authentication stays with `SessionFilter`; role checks stay with the role filters.
- **`show_sql`.** Turned off Hibernate's SQL echo (`show_sql=false`) - it logged every query (information leak plus noise).
- **Demo code.** Removed the unused `DemoS3BucketUpload` servlet (dead demo code that also disclosed a developer path and an S3 bucket name).

### Logging - errors now flow through Log4j instead of stderr
- **Symptom.** 55 `e.printStackTrace()` calls across 17 classes (the 15 repository `Impl`s plus `ConnectionClosers` and `ConnectionFactory`) dumped stack traces straight to `System.err`, bypassing the configured Log4j appenders.
- **Root cause.** `printStackTrace()` writes to stderr with no level, timestamp, thread, or logger context. In standalone Tomcat that lands in `catalina.out` (or is swallowed) rather than the application's log file, and log aggregators that tail the log files never see it; it also cannot be filtered, routed, or silenced per environment.
- **Resolution.** Each class now declares a `LogManager.getLogger(...)` field (matching the existing `RequestHelper` convention) and logs with `LOG.error("<operation> failed", e)`. The throwable argument preserves the full stack trace - the diagnostic value is unchanged, but it now carries a message and flows through the appenders into the log file. The two commented-out `printStackTrace` calls in `UploadFile` were left as-is (dead code).
- **Bonus fix.** `SupervisorApprovalConfirmationRepositoryImpl` called `tx.commit()` in both `catch` blocks instead of `tx.rollback()` - committing a failed transaction. Corrected to `rollback()` to match every sibling repository.
- **Takeaway.** A stack trace is the right diagnostic; stderr is the wrong destination. Route it through the logging framework so it is leveled, timestamped, and captured where everything else is.

## Planned / not yet done
- **Microservice refactor (Phase 4, gated).** The capstone: decompose ERS into a microservice. The Spring Boot migration and the Spring Security migration (auth + password handling) belong here rather than as separate steps. Gated on finishing the current hardening + docs and on a reference architecture.
- **`UploadFile` servlet cleanup.** The file-upload servlet still carries a large commented-out block, hard-coded Windows paths, and - in its live path - `System.err.println(...)` followed by `System.exit(1)`, which would terminate the Tomcat JVM on an AWS error. Out of scope for the logging sweep; flagged for a dedicated cleanup (or removal).
- **Three empty `delete*` stubs** (`deleteApproval`, `deleteRequest`, `deleteReimbursement`): intentional placeholders so every entity has a full CRUD surface; implement when needed, kept on purpose (not dead code).
