# Test Coverage

## Headline
The service layer (`com.revature.service`) has **98.06% instruction coverage**
(556 / 567 instructions), measured with JaCoCo across a suite of **97 passing tests**.

## Scope and denominator
- **Denominator:** all 15 classes in the `com.revature.service` package. **No exclusions.**
- Why no exclusions are needed: the AWS SDK is the only genuinely hard-to-unit-test dependency,
  and it is deliberately isolated in `util` (`ClientBuilder`) and `controller`
  (`UploadFile`, `DemoS3BucketUpload`). The service layer imports zero AWS SDK types
  (`grep -r com.amazonaws src/main/java/com/revature/service` returns nothing), so there is no
  untestable glue in the service layer to exclude from the count. The reported figure is the
  whole package, not a hand-picked subset.

## How the service layer is tested
- **Unit tests with Mockito, no database.** Each service depends on a repository *interface*; the
  test mocks the repository (`@Mock` / `@InjectMocks`) and constructs plain in-memory model
  objects for data. Persistence concerns stay one layer down.
- The three filter methods in `SupervisorApprovalService` use keep/drop fixtures: data that
  satisfies the filter plus data that fails each individual condition, so both halves of every
  `&&` are exercised.
- Void delegations are asserted with `Mockito.verify(...)`. The three empty `delete*` stubs are
  pinned with `Mockito.verifyNoInteractions(...)`, which documents that they currently do nothing.

## Unit vs integration
The repository layer is covered separately by integration tests that run against a real, seeded
PostgreSQL database (`ers_script.sql`). Those exercise the SQL and Hibernate mappings; the service
unit tests exercise business logic in isolation. The 98.06% figure is the service (unit) layer.

## Reproduce it
    cd ReimbursementManagement
    mvn test
    # then open target/site/jacoco/index.html

JaCoCo is bound in `pom.xml` (`prepare-agent` before the tests, `report` in the test phase), so a
plain `mvn test` regenerates the report. Per-package and per-class numbers are in
`target/site/jacoco/jacoco.csv`.

## Metric note
JaCoCo's headline "Coverage" is instruction coverage (executed bytecode instructions / total),
which is the figure quoted above. Line, branch, and method coverage are also in the report.
