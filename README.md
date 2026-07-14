# Employee Reimbursement System (ERS)

## Project Description

A reimbursement system for a company's employees. Employees can request reimbursements and managers can approve or deny those requests.

## Technologies Used

* Java
* JDBC
* Hibernate
* Jackson Databind
* JUnit
* Log4J
* Spring Security Crypto (BCrypt)
* Maven
* Tomcat
* PostgreSQL
* HTML
* CSS
* JavaScript
* Jenkins (containerized CI: build, tests against a disposable database, Trivy SCA,
  Semgrep SAST, gitleaks secrets scan - see `JENKINS.md`)

## Features

List of features ready and opportunities for future development
* Users can view the status of previously submitted requests
* Users can submit an image of a receipt for reimbursement requests
* Administrators can view all past requests from all users
* Administrators can view an employee and the associated manager.

Opportunities:
* Users can submit multiple images of their receipts
* Sending confirmation emails to requesters whose requests have been approved.

## Getting Started

Clone the repository:

```bash
git clone https://github.com/cpabad/Christian-Project1.git
```

Prerequisites: Java 8 (JDK 1.8), Maven, PostgreSQL, and a servlet container (Tomcat).

1. Create a PostgreSQL database and load the schema and seed data from
   `ReimbursementManagement/ers_script.sql` into a schema named `ExpenseReimbursementManagementSystem`.
2. Provide the database connection through environment variables (read at runtime):

   ```bash
   export dburl="jdbc:postgresql://localhost:5432/<your-db>"
   export dbuser="<user>"
   export dbpassword="<password>"
   ```

3. Build and test from `ReimbursementManagement/`:

   ```bash
   mvn clean package
   ```

   The suite includes repository integration tests that need the seeded database and the variables
   above; add `-DskipTests` to build the WAR without them.
4. Deploy `target/ReimbursementManagement-0.0.1-SNAPSHOT.war` to Tomcat. The frontend (HTML/JS/CSS)
   is served by the same WAR, on the same origin as the API.

## Documentation
* `STARTUP.md` - the complete run-it-locally guide: DB seed, build, deploy, login, the FLOW
  request trace, pitfalls, and troubleshooting.
* `WALKTHROUGH.md` - one request (the employee login) narrated end-to-end through every
  class, keyed to a real captured FLOW trace.
* `JENKINS.md` - the CI pipeline: what each stage verifies, result semantics
  (SUCCESS/UNSTABLE/FAILURE), the warn-then-ratchet scan policy, and where the shared
  Jenkins controller lives.
* `CHANGELOG.md` - the 2026 refresh (build fix, test coverage, security hardening) with the
  root cause and fix for each change.
* `COVERAGE.md` - how the service-layer test coverage (99.46%, enforced at >= 98%) is
  measured and reproduced.
* `NORMALIZATION.md` - the proof that the database design is in 5NF.
