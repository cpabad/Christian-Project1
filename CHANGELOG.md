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

## Planned / not yet done
- **Password hashing (BCrypt).** Replace plaintext storage and comparison; requires adding a hashing dependency and updating the seed plus the tests that compare passwords.
- **CORS.** Tighten the wildcard `Access-Control-Allow-Origin: *`. The frontend (HTML/JS/CSS) is served by the same WAR as the API, so the two are same-origin and CORS can largely be removed; a dev proxy covers separate-port development.
- **EmployeeFilter** is an empty no-op; authorization should be consolidated (again, Spring Security).
- **Three empty `delete*` stubs** (`deleteApproval`, `deleteRequest`, `deleteReimbursement`): implement or remove.
