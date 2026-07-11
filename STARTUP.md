# ERS - Startup Guide (Employee Reimbursement System)

A complete, return-to-it-anytime guide for running this application locally. Written so that
even after a long time away, the User can get it running without remembering the details.

---

## Current state (verified by live run, 2026-07-10)

The full record of every change and why is in `CHANGELOG.md` - that is the re-onboarding
read. The fastest way to re-learn how the app *works* is the FLOW trace (section below):
start the app and watch it narrate every request, layer by layer.

Verified facts as of this refresh:

- **Test suite: 125/125 green** on a plain `mvn test` (needs the seeded DB + env vars, Step 2).
- **Service-layer coverage: 99.46% instruction (1298/1305)**, measured by JaCoCo, enforced by
  a build-failing `check` rule at >= 98%. Methodology and denominator are in `COVERAGE.md`.
- **Database design: 5NF / BCNF**, full analysis in `NORMALIZATION.md` (15 tables, verified
  against the live schema).
- **No known vulnerabilities in this monolith** as of 2026-07-10: the 2026-06 hardening pass
  (Log4Shell, BCrypt, CORS, dead filters, dependency CVEs, write-path auth) plus the stored-XSS
  closure of 2026-07-09 (`innerHTML` -> `textContent`, commit 1c7c199). Two honest caveats:
  a documented latent *reliability* bug (seed row 2 can 500 the approve path - see ROADMAP
  Notes; not a vulnerability), and the repository layer swallows Hibernate errors by design
  (era-authentic anti-pattern, documented). The microservice repo tracks its own open
  hardening items separately - this claim covers this repo only.

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

### Verify your toolchain

Confirm each tool is present and at the right version before you start. If Tomcat is missing,
see the [Tomcat 9 install appendix](#appendix--installing-tomcat-9) at the end.

```
java -version                              # need a JDK 8 for the build (see note below)
mvn -version                               # any recent Maven 3.x
psql --version                             # PostgreSQL 12+ is fine
~/tomcat9/bin/version.sh                    # prints "Server version: Apache Tomcat/9.0.x"
```

> Note on the JDK: `java -version` may report a newer JDK (e.g. 21) that you use for other
> projects - that is fine as your default. The monolith BUILD (Step 2) specifically needs a
> **JDK 8**, which is why Step 2 sets `JAVA_HOME` to it explicitly rather than relying on the
> default. Confirm you have one: `ls /usr/lib/jvm | grep -i 8` (Debian/Ubuntu/Mint layout).

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
export dburl="jdbc:postgresql://localhost:5432/ers" dbuser=ers dbpassword=ers
mvn -f ReimbursementManagement/pom.xml clean package
```

This produces:

```
ReimbursementManagement/target/ReimbursementManagement-0.0.1-SNAPSHOT.war
```

Why the database variables here? `mvn clean package` runs the integration tests, which connect
to the database from Step 1 using `dburl`/`dbuser`/`dbpassword`. If they are missing you will
see 36 errors like `Cannot invoke "org.hibernate.Session.close()" because "s" is null` - that
means the tests could not reach the database, not that anything is broken. To build the WAR
without running the tests, add `-DskipTests`.

(Maven picks its JVM from `JAVA_HOME`; confirm with `mvn -version` if the build complains about
the Java version.)

### Step 3 - Give Tomcat the database settings

> **`<tomcat>` is a placeholder for your Tomcat install directory** - substitute your real path
> everywhere it appears below. On this dev box that is `~/tomcat9`, so `<tomcat>/bin/setenv.sh`
> means `~/tomcat9/bin/setenv.sh`. (Do not type the angle brackets.) If `~/tomcat9/bin/setenv.sh`
> already exists and is executable, this step is already done - skip to Step 4.

The app reads three environment variables at runtime: `dburl`, `dbuser`, `dbpassword`.
Tomcat's JVM must have them. The simplest way is a `setenv.sh` in Tomcat's `bin/` folder:

```
# <tomcat>/bin/setenv.sh
export dburl="jdbc:postgresql://localhost:5432/ers"
export dbuser="ers"
export dbpassword="ers"
```

Then make it executable: `chmod +x <tomcat>/bin/setenv.sh` (e.g. `chmod +x ~/tomcat9/bin/setenv.sh`).
If the file is already there with an `x` bit (check with `ls -l ~/tomcat9/bin/setenv.sh`), you are done.

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
- Supervisor account (use the **Manager Login** button): **`employee1`** / **`employeePassword`**.

Why `employee2`? The seed defines two roles - Supervisor (role 1) and Employee (role 2).
`admin` and `employee1` are Supervisors; `employee2`, `employee3`, and `employee4` are
Employees. The employee screens only work for an account whose role is Employee.

> Verified 2026-07-11: `employee1` / `employeePassword` logs in as a Supervisor. `admin` is also
> a Supervisor in the seed, but its stored bcrypt hash is **not** `employeePassword` (login
> returns 400/401) - use `employee1` for the supervisor flow.

---

## Watch the app think - the FLOW trace

The single best reorientation tool in this repo. A dedicated `FLOW` logger narrates every
request as it moves through the layers - which filter passed it, which route matched, which
service and repository ran, which decisions were taken and why. Instead of reading code cold,
run the app and watch it explain itself.

**Turn it on/off** - one line in `ReimbursementManagement/src/main/resources/log4j2.xml`:

```xml
<Logger name="FLOW" level="debug" additivity="false">   <!-- "debug" = on, "off" = silent -->
```

Rebuild + redeploy after toggling (it ships **on**). FLOW is console-only by design - the
trace appears in the terminal running `catalina.sh run` and dies with the process; nothing is
written to log files. (`log4j2-test.xml` ships with it off, so `mvn test` output stays clean.)

**How to read a line:**

```
FLOW [33ad|7] UserService → authenticate: identifier 'employee2' has no '@' - treating it as a username
      ^    ^  ^              ^
      |    |  |              what happened - at decision points, WHY it happened
      |    |  the class speaking
      |    hop counter - orders the narrative within one request
      request id - 4 hex chars; all lines of one request share it
```

**A real excerpt** (the `employee2` login from this guide's own verification run, trimmed):

```
FLOW [33ad|1]  SessionFilter → POST /ReimbursementManagement/app/employee/login received; session: none
FLOW [33ad|3]  EmployeeFilter → role check: EXEMPT (login URL, no session required yet) - continuing down the filter chain
FLOW [33ad|5]  RequestHelper → routing POST /employee/login through the switch
FLOW [33ad|7]  UserService → authenticate: identifier 'employee2' has no '@' - treating it as a username
FLOW [33ad|8]  UserRepositoryImpl → findByUsername: opening a Hibernate session + transaction
FLOW [33ad|9]  HibernateSessionFactory → first use - building the singleton SessionFactory ... (expensive, happens once)
FLOW [33ad|15] User → no-arg constructor fired (Hibernate hydration builds entities this way)
FLOW [33ad|17] UserService → authenticate outcome: BCrypt verified the password for 'employee2' - returning the user
FLOW [33ad|18] RequestHelper → login decision: SUCCESS for 'employee2' (role Employee) - creating the session
FLOW [33ad|20] SessionFilter → response on its way to the client - request frame ends
```

**Reading cues:**

- **Frame boundaries.** `SessionFilter` always speaks first (`... received; session: ...`) and
  last (`request frame ends`). Everything between those two lines with the same `[id|...]` is
  one request. Interleaved ids = concurrent requests; follow one id at a time.
- **Decision points are the payload.** Lines with `check:`, `decision:`, or `outcome:` are
  where behavior forks (auth pass/fail, route matched, login verdict). When predicting a
  trace from memory, these - not the hop numbers - are what you should be able to name.
- **Hydration noise is normal.** Bursts of `no-arg constructor fired` are Hibernate building
  entities: a big burst on the first-ever query (SessionFactory bootstrap probes the mapped
  classes), then per-row firing on later queries. Recognize it, don't memorize it.

**Drill tie-in:** FLOW traces are the raw material for the trace-prediction drill - predict
the narrative from memory, run the real request, diff. Rules live in `CLAUDE.md` under
"QUESTION-LOG drill cycle".

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

> The login lives in the inline `<script>` of `index.html`, posting to `/app/employee/login` /
> `/app/manager/login` as mapped above. (The old dead login files - `login.html`, `JS/index.js`,
> `JS/login2.js`, `JS/logout.js` - were deleted 2026-07-10; see CHANGELOG.)

Tip - see both halves of a request at once: keep the FLOW trace (section above) in your
terminal and the browser's **Network** tab (F12) open while you click. The Network tab shows
the client half (which button fired which `/app/...` call, what JSON came back); FLOW shows
the server half (what happened between arrival and answer). Same request, both sides.

---

## Connecting to and inspecting the database (psql)

To look inside the database from the terminal (no DBeaver needed), use `psql`. Unlike a saved
DBeaver connection, `psql` needs the host, database, user, and password **every time**.

Connect:

```
PGPASSWORD=ers psql -h localhost -U ers -d ers
```

| DBeaver field | psql flag |
| --- | --- |
| Host | `-h localhost` |
| Database | `-d ers` |
| Username (role) | `-U ers` |
| Password | `PGPASSWORD=ers` (or type it at the `Password:` prompt) |

You do NOT need `sudo` for this - `sudo` is only for admin tasks as the `postgres` system
user. Bare `psql` with no flags fails with `role "<your-username>" does not exist`, because it
defaults to connecting as your operating-system username to a database of the same name -
neither of which exists here.

Look around (these backslash commands are psql's version of DBeaver's left-hand tree):

```
\l                                                -- list databases
\dn                                               -- list schemas
\dt "ExpenseReimbursementManagementSystem".*      -- list tables in our schema
\d  "ExpenseReimbursementManagementSystem".roles  -- describe a table's columns
\q                                                -- quit
```

The tables are NOT in the default `public` schema - they live in the case-sensitive schema
`"ExpenseReimbursementManagementSystem"`, which MUST be double-quoted. Two ways to query:

```sql
-- (a) qualify the table each time:
SELECT * FROM "ExpenseReimbursementManagementSystem".roles;

-- (b) or set the search path once, then use bare table names for the rest of the session:
SET search_path TO "ExpenseReimbursementManagementSystem";
SELECT * FROM roles;
SELECT * FROM users;
```

One-liner straight from the shell (runs the query and exits):

```
PGPASSWORD=ers psql -h localhost -U ers -d ers -c 'SELECT * FROM "ExpenseReimbursementManagementSystem".roles;'
```

---

## Pitfalls - how NOT to crash or confuse the app

1. **The Upload page cannot work locally - expect a 500 if you try.** Uploads go to AWS S3,
   which is not configured here (the keys were revoked). Historical note: the servlet used to
   call `System.exit(1)` on an upload failure - one click shut down all of Tomcat. That was
   fixed in the 2026-07 stabilization pass (`LOG.error` + HTTP 500; the failure now fails the
   request, never the server - see CHANGELOG). After you submit a request you are auto-taken
   to the upload page; just navigate back to the home page.
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

None of these crash the server anymore - all are harmless and recoverable.

---

## Quick troubleshooting

| Symptom | Likely cause | Fix |
| --- | --- | --- |
| `mvn` build fails on `isBlank`/Java version | Maven using the wrong JDK | `export JAVA_HOME=<your JDK 8>`, re-run |
| App loads but every table is empty | DB env vars not seen by Tomcat | Put them in `<tomcat>/bin/setenv.sh` (Step 3) |
| 404 on every page/button | WAR not deployed at `/ReimbursementManagement` | Deploy the WAR as `ReimbursementManagement.war` |
| Login always "Invalid Credentials" | Wrong account/role or DB not seeded | Use `employee2`/`employeePassword`; reload the seed (Step 1) |
| Upload returns 500 | S3 is not configured locally (keys revoked) | Expected; navigate back - the server keeps running |
| `mvn` shows 36 errors `Session.close() ... "s" is null` | Tests cannot reach the DB (env vars not exported) | `export dburl/dbuser/dbpassword` before `mvn`, or add `-DskipTests` |
| `psql: role "<name>" does not exist` | Connected with no `-U`/`-d` (defaulted to your OS user) | `PGPASSWORD=ers psql -h localhost -U ers -d ers` |

---

## Appendix — installing Tomcat 9

You only need this if `~/tomcat9/bin/version.sh` does not report `Apache Tomcat/9.0.x`. On this
dev box Tomcat 9 is already installed at `~/tomcat9`.

### Which distribution to download, and why

From the Apache Tomcat 9 download page (<https://tomcat.apache.org/download-90.cgi>), under
**Binary Distributions → Core**, take **`apache-tomcat-9.0.x.tar.gz`**. Reasoning:

- **Tomcat 9, not 10+** — 9 runs the `javax.servlet` API this app targets; Tomcat 10 switched to
  `jakarta.servlet` and will refuse the WAR (same rule as the top-of-file warning).
- **"Core", not "Full documentation", "Deployer", or the source `.tar.gz`** — Core is the runnable
  server. The Deployer is a CI/CD helper, not a server; the source needs compiling.
- **`.tar.gz`, not `.zip`** — on Linux the tarball preserves the executable bit on the `bin/*.sh`
  scripts; the `.zip` is the Windows-oriented package and strips it (you'd have to `chmod` them).
- **Not `apt install tomcat9`** — Linux Mint's Ubuntu base (24.04) **dropped the `tomcat9`
  package**, and the apt layout (`/etc/tomcat9`, `/var/lib/tomcat9`, systemd) also splits the
  install across the filesystem, which does not match this guide's single-directory `<tomcat>`
  model. The Core tarball is self-contained, version-pinned, needs no root, and is exactly what
  `~/tomcat9` already is.

### Install steps (Linux Mint)

```
# 1. download the Core tarball (check the page for the current 9.0.x patch version)
cd ~
curl -LO https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.119/bin/apache-tomcat-9.0.119.tar.gz

# 2. extract, and name the folder ~/tomcat9 to match this guide
tar xzf apache-tomcat-9.0.119.tar.gz
mv apache-tomcat-9.0.119 tomcat9

# 3. the bin/*.sh scripts are already executable from the tarball; confirm the server runs
~/tomcat9/bin/version.sh          # -> Server version: Apache Tomcat/9.0.119

# 4. (optional) clean up the archive
rm ~/apache-tomcat-9.0.119.tar.gz
```

Tomcat needs a JDK/JRE on `PATH` (you already have one). After this, return to **Step 3** —
create `~/tomcat9/bin/setenv.sh` with the database variables, then deploy in Step 4.
