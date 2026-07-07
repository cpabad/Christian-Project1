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

## 2026-07 - Stabilization pass

Quick wins from an architecture review: close the two remaining ROADMAP items, fix one latent
error-path bug across the repository layer, and remove a committed credential.

### Security - AWS credentials were committed to the repository
- **Symptom.** `src/main/webapp/WEB-INF/s3.properties` contained an AWS access key ID and secret key, tracked in git since the original 2021 push - roughly five years of public exposure on GitHub.
- **Root cause.** The file was checked in during initial development. Nothing ever read it: the code builds its S3 client through the SDK's default provider chain (environment variables), so the file was pure liability with zero function.
- **Resolution.** Deleted the file from the working tree and index. **Still required (manual):** treat the key pair as compromised and deactivate/delete it in AWS IAM; decide whether to scrub it from git history with `git-filter-repo` (as was done for the P3 repo) - removal from HEAD alone does not remove it from old commits.
- **Takeaway.** A secret in git is public the moment the repo is; the fix is rotation, not deletion. Credentials belong in the environment or a secrets manager, never in the tree - and a config file no code reads should be deleted the day it becomes dead.

### Reliability - repository error paths could NPE and mask the real failure
- **Symptom.** Every method in the 15 repository `Impl` classes followed the same pattern: `catch` calls `tx.rollback()`, `finally` calls `s.close()` - with no null checks.
- **Root cause.** `Session s = null; Transaction tx = null;` are assigned *inside* the `try`. If `HibernateSessionFactory.getSession()` throws (database down, bad credentials), `tx` and `s` are still null, so the `catch` and `finally` blocks throw `NullPointerException` - replacing the informative original exception with a misleading one and skipping the log line that would have explained the outage.
- **Resolution.** Null-guarded all 52 `tx.rollback()` and all 52 `s.close()` calls across the 15 `Impl` classes (a mechanical, uniform rewrite; the happy path is untouched). All 97 tests stay green.
- **Takeaway.** Cleanup code runs precisely when things have already gone wrong, so it must assume the least - a `finally` block that can itself throw hides the failure it was meant to clean up after. (Modern Java solves this pattern with try-with-resources.)

### Build - the 98% coverage claim is now enforced, not just reported
- **Symptom.** The ROADMAP's last Phase 1 item: nothing failed the build if service-layer coverage regressed below the advertised 98%.
- **Resolution.** Added a JaCoCo `check` rule (test phase, so plain `mvn test` enforces it): instruction coverage for `com.revature.service` must be >= 0.98. Negative-tested by temporarily raising the minimum to 0.99, which correctly produced `Rule violated ... covered ratio is 0.98, but expected minimum is 0.99` and a build failure.
- **Takeaway.** An unenforced quality claim decays silently; a threshold in the build turns "we believe" into "the build proves."

### Cleanup - UploadFile servlet and dead AWS demo code
- **Symptom.** The flagged follow-up from the 2026-06 refresh: `UploadFile` carried ~70 lines of commented-out dead code, hard-coded Windows paths (`F:\...` in `@MultipartConfig` and the file path), `System.out`/`System.err` output, and `System.exit(1)` on an AWS error - which would have terminated the entire Tomcat JVM. `util/ClientBuilder` was an uncalled AWS demo class with the same `System.exit(1)` and hard-coded paths.
- **Resolution.**
  - Removed the dead block, the auto-generated TODO stubs, and the unused `DiskFileItemFactory`.
  - `@MultipartConfig` no longer pins a Windows directory; uploads buffer in the container's temp directory (`ServletContext.TEMPDIR`), portable across OS and machine.
  - `System.exit(1)` replaced with `LOG.error(...)` + HTTP 500 - an upload failure now fails the request, not the server.
  - Console output replaced with Log4j; dropped the gratuitous `s3.getS3AccountOwner()` call (an extra network round-trip that only fed a debug print).
  - Bug fix: the servlet stored `fileName` in the *session*, but `RequestHelper`'s `/upload-file` case reads it as a *request* attribute (which the `forward` carries), so the read was always null. It is now set on the request.
  - Deleted `ClientBuilder` (zero callers, same category as the previously removed `DemoS3BucketUpload`).
- **Known remaining gap.** The upload-to-database linkage is still broken end-to-end: `RequestHelper` reads `retrievedMgrRequests` as a request attribute, but it is stored in the session (and the employee flow stores `retrievedRequest` under a different key), so the `AmazonS3Object` record is created with a null request. Fixing it properly belongs to the planned service-extraction pass, not a servlet-local patch.
- **Takeaway.** `System.exit()` in servlet code is a container-wide kill switch; error handling in a shared JVM must fail the unit of work, never the process.

### Architecture - approval resolution moved out of the controller into the service layer
- **Symptom.** The heart of the ERS domain - deciding whether a manager's approve/deny finalizes a reimbursement, escalates it up the chain, or waits on peer managers - lived in a ~40-line block inside `RequestHelper`'s POST switch, not in the service layer. The services were pure one-line delegates, so the advertised "98% service-layer coverage" measured delegation, not business rules.
- **Root cause.** The fat-controller pattern: HTTP parsing and domain logic grew together in the servlet layer, while the service layer stayed decorative. (This was the anti-pattern even by servlet-era standards - the extraction keeps the project era-authentic.)
- **Resolution.**
  - New `SupervisorApprovalService.resolveApproval(requestId, managerId, decision)` owns the cascade and returns a new `ApprovalOutcome` enum (`APPROVED` / `DENIED` / `ESCALATED` / `WAITING_ON_OTHERS`); the controller shrinks to parameter parsing plus a switch mapping outcomes to the exact same HTTP statuses and messages as before. The service gained the collaborating repositories as fields (same constructor style as its siblings), so the existing `@Mock`/`@InjectMocks` test pattern keeps working.
  - Behavior-preserving by construction: condition order, status-id writes (1 = resolved, 2 = pending), and which paths persist are identical; a write-only local list (`supervisorsNeededForApproval`) was dropped, and a repeated per-iteration hierarchy lookup was hoisted out of the loop (pure reads - fewer queries, same result).
  - Four new unit tests cover the cascade (one per outcome, keep/drop fixtures) - suite 97 -> 101, all green.
  - `ApprovalOutcome` lives in `com.revature.model`, deliberately: `COVERAGE.md`'s denominator is "all classes in `com.revature.service`, no exclusions", and an enum's compiler-generated `values()`/`valueOf()` would register as permanently-uncovered instructions there.
- **Coverage effect.** Service-layer instruction coverage: 98.06% (556/567) -> **98.57% (756/767)**. The denominator grew by ~200 instructions of real domain logic that is now unit-tested; `SupervisorApprovalService` is at 100%.
- **Takeaway.** Thin controller, rules in services: the controller answers "what did HTTP say," the service answers "what does the business do." Only after the move does a service-coverage number measure something worth advertising.

### Architecture - submit-request cascade moved into the service layer, duplicate branches merged
- **Symptom.** Submitting a reimbursement request ran a second domain cascade inside `RequestHelper`: persist the request, re-read it to obtain its database id, fan out one pending `SupervisorApproval` per direct supervisor, and create the pending `Reimbursement` record for the top-of-chain supervisor. The `/employee/submit-request` and `/manager/submit-request` cases were verbatim copies of each other (~29 lines each), differing only in local variable names and the session-attribute key at the end.
- **Root cause.** Same fat-controller growth as the approval cascade - plus copy-paste reuse: the manager flow was cloned from the employee flow instead of shared.
- **Resolution.**
  - New `RequestService.submitRequest(Request)` owns the cascade and returns the persisted request. `RequestService` gained the five collaborating repositories as constructor-wired fields, mirroring the `SupervisorApprovalService` pattern from the approval extraction.
  - The two controller cases collapsed into one fall-through case (`case "/employee/submit-request": case "/manager/submit-request":`): build the `Request` from HTTP parameters, call the service, store the result under the flow's session key (`retrievedRequest` vs `retrievedMgrRequests` - both preserved because the upload flow still expects them; that linkage is the next fix). Net: ~58 controller lines became ~20, and seven now-unused imports were removed.
  - Behavior-preserving: same call sequence, same sentinel values (placeholder id `100`, placeholder date `2000-01-01`), same status-id writes (2 = pending), same 400 + "Invalid entries" on `NonUniqueResultException`. One deliberate widening: that catch now wraps the whole cascade rather than only the insert, so a duplicate-row anomaly during the re-read also returns 400 instead of escaping as a raw 500.
  - Three new unit tests (reimbursement created at chain top / approval only below the top / no supervisors at all), using `ArgumentCaptor` to assert what the fan-out actually constructs. Suite 101 -> 104, all green.
- **Coverage effect.** Service-layer instruction coverage 98.57% -> **98.73% (856/867)**; the denominator grew by another ~100 instructions of formerly untested controller logic.
- **Takeaway.** Duplicated controller blocks are a smell with a standard fix: push the shared logic down a layer and the duplication disappears at the source. The extraction also exposes quirks worth revisiting later (the insert-then-re-read pattern exists because the id comes from a database sequence; the sentinel date stands in for "not yet decided").

### Architecture - login and profile-update logic moved into UserService, twin blocks merged
- **Symptom.** The last two domain blobs in `RequestHelper`: (a) two near-identical login cases doing lookup + BCrypt verification inline, and (b) two *verbatim-identical* ~50-line `update-user-information` cases (employee/manager) validating and applying username/password/email changes.
- **Root cause.** Same fat-controller/copy-paste pattern as the two cascades before it - plus one real bug in the login lookup (below).
- **Resolution.**
  - `UserService.authenticate(usernameOrEmail, rawPassword)`: looks the user up (identifier containing `@` = email), verifies the BCrypt hash, returns the user or null - null for unknown identifier, blank password, and wrong password alike, so a caller cannot tell which check failed. Both login cases collapsed into one fall-through case; the manager flow keeps its supervisor-role gate in the controller.
  - `UserService.updateProfile(userId, ProfileUpdateForm)`: applies the three optional form sections with the exact original validation, returning a `ProfileUpdateOutcome` (`UPDATED` / `INVALID_ENTRIES` / `NO_ENTRIES`) that the controller maps to the original messages and statuses. `ProfileUpdateForm` (a plain 9-field form object) and both outcome enums live in `com.revature.model`, keeping servlet types out of the service and enum boilerplate out of the coverage denominator.
  - Original quirks preserved and now **pinned by tests**: a taken username or email is silently skipped (the update still reports success, value unchanged), and a confirmed email section with a blank new address does nothing but still reports success.
  - 20 new unit tests (7 authenticate, 13 updateProfile); suite 104 -> 124, all green.
- **Bug fix (deliberate behavior change).** The old email-vs-username test was `contains("@") && contains(".com") || contains(".net") || ...` - and `&&` binds tighter than `||`, so a *username* containing ".net"/".us"/".edu"/".co" was routed to the email lookup (login broken for such usernames), while a real email ending in any other TLD (`.org`, `.io`, ...) fell through to the username lookup (login broken for those emails). The rule is now simply: contains `@` = email. A regression test pins the "username containing .net" case.
- **Second deliberate change (corner case).** Credentials are now validated before session state is reported: logging in with a *wrong* password while already holding a session returns "Invalid Credentials" (400) instead of "You already have a current session" (400). Valid credentials with an existing session still get the session message. Rationale: the session hint is only owed to callers who prove who they are.
- **Coverage effect.** Service layer 98.73% -> **98.95% (1034/1045)**; `UserService` at 100%, including a "taken email" test added specifically because JaCoCo showed the `findByEmail` call otherwise only ever completing exceptionally.
- **Takeaway.** Operator precedence is not reading order: `a && b || c` is `(a && b) || c`. The five-clause TLD heuristic hid that for years; the fix was not more clauses but a simpler rule. And when every test of a line throws, coverage tools rightly complain - the normal path was genuinely untested.

### Bug fix - receipt uploads never linked to their reimbursement request
- **Symptom.** The `AmazonS3Object` record that ties an uploaded receipt to its reimbursement request was always created with a null request (and, before this refresh, a null file name too) - uploads "succeeded" but the database linkage was garbage.
- **Root cause.** An attribute-scope and key mismatch across the three-step flow. Submit stored the persisted request in the *session*, under *two different keys* by flow (`retrievedRequest` for employees, `retrievedMgrRequests` for managers). The upload handler then read both values as *request* attributes - and only under the manager key. `HttpServletRequest.getAttribute` and `HttpSession.getAttribute` are different scopes: request attributes live for one request (they do survive a `forward`, which is why `fileName`, fixed earlier, now arrives), while session attributes live for the login. So the lookup missed on scope for managers and on both scope and key for employees. The repository then swallowed any insert failure (`HibernateException` logged, not rethrown), keeping the breakage silent.
- **Resolution.** One writer, one reader, one key, one scope: both submit flows store the persisted request in the session under `retrievedRequest` (the two keys' only consumer was this handler, so the manager key was dropped), and the upload case reads it from the session. `fileName` stays a request attribute since it genuinely belongs to the single upload request.
- **Limitation.** Verified by inspection and the full suite (124/124; this is controller wiring, outside the service-layer tests). End-to-end verification needs a live S3 bucket, which no longer exists - the AWS keys were revoked. If the upload feature is ever revived, re-test the whole chain first.
- **Takeaway.** Servlet attribute scopes are namespaces with lifetimes: request scope survives a `forward` but not a redirect or a second request; session scope survives the login. A handoff across two user actions (submit, then upload) can only travel in the session - and swallowed persistence exceptions turn a wiring bug like this into years of silently useless rows.

### Hygiene - log output was tracked by git
- **Symptom.** `ReimbursementManagement/logs/ExpenseReimbursementEventTrialLog.txt` was version-controlled, so every test run dirtied the working tree.
- **Resolution.** Untracked the file and added `logs/` (and the local `.claude/` tooling directory) to `.gitignore`. Also carried the `.factorypath` refresh that mirrors the 2026-06 dependency bumps (Eclipse annotation-processing metadata; keeping it consistent with the POM).

## Planned / not yet done
- **Optional: git history scrub.** The exposed AWS key pair was revoked and all EC2 instances terminated (2026-07-06), so the credential is dead; scrubbing `s3.properties` from git history with `git-filter-repo` (as was done for P3) remains available as pure tidiness.
- **Microservice refactor (Phase 4).** Extracted to its own repo (`Revature931-Project1-Microservice`, Spring Boot 3 / Java 17); the auth and request slices are done there. This repo stays the monolith. The 2026-07 service extractions make the remaining decomposition mostly mechanical: the cascades now live in services that port to Spring beans directly.
- **Three empty `delete*` stubs** (`deleteApproval`, `deleteRequest`, `deleteReimbursement`): intentional placeholders so every entity has a full CRUD surface; implement when needed, kept on purpose (not dead code).
