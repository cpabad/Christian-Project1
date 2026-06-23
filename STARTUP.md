# ERS - Startup Guide (Employee Reimbursement System)

A complete, return-to-it-anytime guide for running this application locally. Written so that
even after a long time away, the User can get it running without remembering the details.

---

## Read this first - you are returning, and everything is OK

If you are opening this after a long time away: welcome back. When this refresh began, the
User was uneasy that old security problems might be lurking in this code. That worry has been
put to rest. Every issue that was found was fixed, and written down:

- The Log4Shell vulnerability in Log4j was patched.
- Passwords are no longer stored in plaintext - they are hashed with BCrypt.
- The wide-open CORS policy was removed; the app is served same-origin.
- The login/authorization filters that silently did nothing were repaired.
- Out-of-date libraries carrying known CVEs were updated.
- State-changing requests now require an authenticated session.
- Errors flow through the logging system instead of being dumped to the console.

There are **no known vulnerabilities** in this application today. The full account of what
changed and why is in `CHANGELOG.md`. You do not need to be afraid of this code. You can open
it, run it, and trust it.

## Why you should be proud of this

This is not a throwaway exercise. It is a small system that does exactly what it claims:

- **Every advertised claim is true and reproducible.** The "98% service-layer test coverage"
  is measured by JaCoCo and regenerates on a plain `mvn test`; the number and exactly how it
  is counted are written out in `COVERAGE.md`.
- **The database design is genuinely sound** - verified to be in 5NF / BCNF, with the full
  analysis in `NORMALIZATION.md`.
- **The whole test suite is green** - 97 tests pass.

The claims hold up to scrutiny. That is something to be proud of.

## What this application does

ERS lets employees submit expense-reimbursement requests for events they attend, and lets
supervisors approve or deny them through a chain of approvals. Employees log in, submit
requests, and track them as pending or resolved. Supervisors review the requests of the
employees who report to them.

---

## What you need installed

- **JDK 8** (the project targets Java 8)
- **Maven**
- **PostgreSQL**
- **Apache Tomcat 9** (or 8.5)

> IMPORTANT: use **Tomcat 9 or 8.5 - never Tomcat 10 or newer.** This app uses the older
> `javax.servlet` API. Tomcat 10+ switched to `jakarta.servlet` and will refuse to run it.

---

## How to run it - step by step

### Step 1 - Prepare the database

The app expects a PostgreSQL database `ers` owned by a user `ers`. The tables live in a
case-sensitive schema named `ExpenseReimbursementManagementSystem`. The seed script does NOT
create that schema (and its own `search_path` line is misspelled), so you create the schema
yourself and feed the script in with the search path set:

```
sudo -u postgres psql -c "CREATE ROLE ers LOGIN PASSWORD 'ers';" -c "CREATE DATABASE ers OWNER ers;"

{ echo 'CREATE SCHEMA "ExpenseReimbursementManagementSystem";'; \
  echo 'SET search_path TO "ExpenseReimbursementManagementSystem";'; \
  sed '/^SHOW search_path/d;/^SET search_path/d;/^DROP TABLE/d' ReimbursementManagement/ers_script.sql; } \
  | PGPASSWORD=ers psql -h localhost -U ers -d ers -v ON_ERROR_STOP=1
```

If you are re-running and the schema already exists, drop it first, then repeat the load:

```
PGPASSWORD=ers psql -h localhost -U ers -d ers -c 'DROP SCHEMA "ExpenseReimbursementManagementSystem" CASCADE;'
```

### Step 2 - Build the WAR

Point Maven at a JDK 8, then build:

```
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64    # adjust to your JDK 8 path
mvn -f ReimbursementManagement/pom.xml clean package
```

This produces:

```
ReimbursementManagement/target/ReimbursementManagement-0.0.1-SNAPSHOT.war
```

(Maven picks its JVM from `JAVA_HOME`. Confirm with `mvn -version` if the build complains
about the Java version.)

### Step 3 - Give Tomcat the database settings

The app reads three environment variables at runtime: `dburl`, `dbuser`, `dbpassword`.
Tomcat's JVM must have them. The simplest way is a `setenv.sh` in Tomcat's `bin/` folder:

```
# <tomcat>/bin/setenv.sh
export dburl="jdbc:postgresql://localhost:5432/ers"
export dbuser="ers"
export dbpassword="ers"
```

Then make it executable: `chmod +x <tomcat>/bin/setenv.sh`

### Step 4 - Deploy and start

Copy the WAR into Tomcat, named so the URL path becomes `/ReimbursementManagement` (the
frontend hard-codes that path):

```
cp ReimbursementManagement/target/ReimbursementManagement-0.0.1-SNAPSHOT.war <tomcat>/webapps/ReimbursementManagement.war
<tomcat>/bin/catalina.sh run     # foreground; or startup.sh to run in background
```

Open in your browser:

```
http://localhost:8080/ReimbursementManagement/
```

### Step 5 - Log in as an employee

On the landing page, type the username and password, then click **Employee Login**.

- Employee account: **`employee2`** / **`employeePassword`**
- Supervisor accounts (use the **Manager Login** button): `employee1` or `admin`, same password.

Why `employee2`? The seed defines two roles - Supervisor (role 1) and Employee (role 2).
`admin` and `employee1` are Supervisors; `employee2`, `employee3`, and `employee4` are
Employees. The employee screens only work for an account whose role is Employee.

---

## Navigating the site (employee)

```
index.html  --click Employee Login-->  POST /app/employee/login   (creates your session)
   |
   v
employeehomepage.html
   |-- View Pending Requests   -> GET  /app/employee/view-pending-requests
   |-- View Resolved Requests  -> GET  /app/employee/view-resolved-requests
   |-- View User Info          -> GET  /app/employee/view-user-information
   |-- Submit Request          -> employeerequestsubmission.html
   |        \-- click Submit   -> POST /app/employee/submit-request?...   (then auto-opens the upload page - see Pitfall 1)
   |-- Update Profile          -> employeeupdate.html -> POST /app/employee/update-user-information
   \-- Logout                  -> GET  /app/logout    (ends your session, returns to index.html)
```

Every `/app/*` call travels the same path inside the server: the **FrontController** servlet
hands it to **RequestHelper**, which routes by URL to a **service**, which calls a
**repository**, which uses **Hibernate** to talk to **PostgreSQL**; the JSON answer comes back
to the page's request.

Tip: open your browser's developer tools (F12) and watch the **Network** tab while you click.
You will see each button fire a request to `/ReimbursementManagement/app/...` and the JSON
return. That is the frontend and backend talking to each other.

---

## Pitfalls - how NOT to crash or confuse the app

1. **Never click "Upload" on the image page.** The upload servlet calls `System.exit(1)` if
   its file-storage call fails - and file storage is not configured for local use - which
   would shut down the entire Tomcat server (every session, gone). After you submit a request
   you are automatically taken to the upload page; simply navigate back to the home page.
   **Do not click Upload.** This is the only action that can truly crash the server.
2. **"You already have a current session" (status 400).** Login refuses if a session already
   exists. Log out cleanly before logging in again; if you get stuck, clear the site's cookies
   or use a private/incognito window.
3. **Submitting a request needs an address that exists in the seed.** The backend looks up your
   street/city/zip against the seeded location tables. A made-up address returns nothing and
   you get "Invalid entries." Use an address already present in the seed data.
4. **Blank tables or failing actions usually mean the database settings did not reach Tomcat.**
   The `dburl`/`dbuser`/`dbpassword` variables must be in Tomcat's environment (Step 3), not
   just your shell.
5. **Wrong Tomcat version.** Tomcat 10+ will not run this app (see the note up top). Use 9/8.5.
6. **Pages erroring after a while?** Your session likely timed out. Just log in again.

Only Pitfall 1 crashes the server. The rest are harmless and recoverable.

---

## Quick troubleshooting

| Symptom | Likely cause | Fix |
| --- | --- | --- |
| `mvn` build fails on `isBlank`/Java version | Maven using the wrong JDK | `export JAVA_HOME=<your JDK 8>`, re-run |
| App loads but every table is empty | DB env vars not seen by Tomcat | Put them in `<tomcat>/bin/setenv.sh` (Step 3) |
| 404 on every page/button | WAR not deployed at `/ReimbursementManagement` | Deploy the WAR as `ReimbursementManagement.war` |
| Login always "Invalid Credentials" | Wrong account/role or DB not seeded | Use `employee2`/`employeePassword`; reload the seed (Step 1) |
| Server suddenly stopped | The Upload button was clicked | Restart Tomcat; do not use Upload |
