# Employee Reimbursement System (ERS)

## Project Description

A reimbursement system for a company's employee/ Employees can request reimbursements and managers can approve or deny those requests.

## Technologies Used

* Java
* JDBC
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
* `CHANGELOG.md` - the 2026 refresh (build fix, test coverage, security hardening) with the
  root cause and fix for each change.
* `COVERAGE.md` - how the 98% service-layer test coverage is measured and reproduced.
* `NORMALIZATION.md` - the proof that the database design is in 5NF.
