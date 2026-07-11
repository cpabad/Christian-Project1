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

## New here? Start with this

**Time:** about 30 minutes on a fresh machine (most of it is installing tools and the first
Maven build downloading dependencies). **The path:** install four tools → create + seed the
database (Step 1) → build a WAR (Step 2) → hand Tomcat the DB settings (Step 3) → deploy and
start (Step 4) → log in (Step 5). Do the [install appendix](#appendix--installing-the-toolchain)
FIRST if any tool below is missing, then come back to Step 1.

<details>
<summary><b>Terms you'll meet in this guide</b> (click to expand)</summary>

- **JDK vs JRE** - a **JDK** (Java Development Kit) can *compile* code (`javac`); a **JRE** only
  *runs* it. Building this project needs a JDK; a JRE alone fails with "release version not
  supported". This project needs specifically a **JDK 8**.
- **Maven** - the build tool. `mvn package` compiles the code, runs the tests, and bundles the
  result into a WAR. It reads `pom.xml`.
- **WAR** (Web Application Archive) - a single `.war` file containing the whole web app
  (compiled classes + the HTML/JS/CSS frontend). You "deploy" it by dropping it into Tomcat.
- **Servlet container / Tomcat** - the server that runs a WAR. This app is a *servlet* app (the
  pre-Spring-Boot Java web style), so it needs a servlet container; **Tomcat 9** is ours. (It is
  NOT bundled in the WAR - you install and run it separately, unlike a Spring Boot app.)
- **Schema vs database** - in PostgreSQL a **database** contains one or more **schemas**
  (namespaces for tables). This app's tables live in a schema called
  `ExpenseReimbursementManagementSystem`, *inside* a database called `ers` - which is why Step 1
  creates the schema explicitly and every query is schema-qualified.
- **Annotations** (`@Entity`, `@Override`, `@Column`, ...) - the `@`-words all over the Java
  source. They are inert metadata: each one only matters because something specific *reads* it
  (the compiler, Hibernate, Tomcat, Jackson, or a test framework), and who reads it decides
  when a mistake surfaces. [ANNOTATIONS.md](ANNOTATIONS.md) covers every one you'll meet here,
  with live deletion experiments.

</details>

---

## What you need installed

If any of these is missing, the [install appendix](#appendix--installing-the-toolchain) at the
end has copy-paste steps for Linux (apt), macOS (Homebrew), and Windows.

- **JDK 8** (the project targets Java 8)
- **Maven**
- **PostgreSQL**
- **Apache Tomcat 9** (or 8.5)

> IMPORTANT: use **Tomcat 9 or 8.5 - never Tomcat 10 or newer.** This app uses the older
> `javax.servlet` API. Tomcat 10+ switched to `jakarta.servlet` and will refuse to run it.

### Verify your toolchain

Confirm each tool is present and at the right version before you start. If any is missing,
see the [install appendix](#appendix--installing-the-toolchain) at the end.

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

> **✓ Success check.** The load prints a stream of `CREATE TABLE` / `INSERT 0 1` lines and no
> `ERROR:`. Confirm the seed landed:
> ```
> PGPASSWORD=ers psql -h localhost -U ers -d ers -tAc 'SELECT count(*) FROM "ExpenseReimbursementManagementSystem".users;'
> ```
> Expect **`5`**. If you get `role "ers" does not exist` or `database "ers" does not exist`, the
> first line (create role + database) did not run - re-run it with `sudo -u postgres`.

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

> **✓ Success check.** The **first** build is slow - Maven downloads every dependency to `~/.m2`
> (a minute or more; it is not hung). A successful build ends with `BUILD SUCCESS`, a
> `Tests run: 125, Failures: 0, Errors: 0` line, and the WAR file existing:
> ```
> ls -lh ReimbursementManagement/target/ReimbursementManagement-0.0.1-SNAPSHOT.war
> ```
> `BUILD FAILURE` with `release version 8 not supported` means `JAVA_HOME` is not a JDK 8 (see
> the troubleshooting table).

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

> **✓ Success check.** In the terminal running Tomcat you should see a line like
> `Server startup in [1234] milliseconds` and (because FLOW ships on) `FLOW [....|1] SessionFilter
> -> GET ... received` when you load the page. The browser shows the login page. If the page is a
> Tomcat 404, the WAR was not deployed under the name `ReimbursementManagement.war` - re-check the
> `cp` target. If the terminal shows a stack trace mentioning `Session` or a JDBC URL, the DB
> settings did not reach Tomcat (Step 3).

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

> **✓ Success check.** A correct login lands you on the employee home page (or a "Welcome"
> response), and the Tomcat FLOW trace shows `... login decision: SUCCESS ... creating the
> session`. "Invalid Credentials" (400) means wrong username/password for the role - use exactly
> `employee2` / `employeePassword`. "You already have a current session" means you are still
> logged in from before - log out (or use a private window) and retry. **That's it - the app is
> running.** From here, read [Watch the app think](#watch-the-app-think---the-flow-trace) to learn
> how a request flows through the code.

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

Install-level problems (before the app even runs) come first, then app-level ones.

| Symptom | Likely cause | Fix |
| --- | --- | --- |
| `psql: could not connect ... server ... not running` / `Connection refused` | PostgreSQL service isn't started | Linux: `sudo systemctl start postgresql` (and `enable` it). macOS: `brew services start postgresql@16`. Windows: start the "postgresql" service. |
| `sudo -u postgres psql` -> `sudo: a terminal is required` | Running it inside a non-interactive shell | Run it in a normal terminal window, or grant your user CREATEDB once and drop the `sudo -u postgres` prefix |
| `mvn` / `java` build says `release version 8 not supported` | The `java` on `PATH` is a JRE or wrong-version JDK | Install a **JDK 8** (appendix) and `export JAVA_HOME=<jdk8 path>`; verify with `"$JAVA_HOME"/bin/javac -version` |
| `java: command not found` or `mvn: command not found` | Tool not installed or not on `PATH` | Install it (appendix); reopen the terminal so `PATH` refreshes |
| First `mvn` build seems to hang for a minute | It's downloading dependencies to `~/.m2` (one time) | Wait - it is not frozen; subsequent builds are fast |
| `mvn` build fails on `isBlank`/Java version | Maven using the wrong JDK | `export JAVA_HOME=<your JDK 8>`, re-run |
| App loads but every table is empty | DB env vars not seen by Tomcat | Put them in `<tomcat>/bin/setenv.sh` (Step 3) |
| 404 on every page/button | WAR not deployed at `/ReimbursementManagement` | Deploy the WAR as `ReimbursementManagement.war` |
| Login always "Invalid Credentials" | Wrong account/role or DB not seeded | Use `employee2`/`employeePassword`; reload the seed (Step 1) |
| Upload returns 500 | S3 is not configured locally (keys revoked) | Expected; navigate back - the server keeps running |
| `mvn` shows 36 errors `Session.close() ... "s" is null` | Tests cannot reach the DB (env vars not exported) | `export dburl/dbuser/dbpassword` before `mvn`, or add `-DskipTests` |
| `psql: role "<name>" does not exist` | Connected with no `-U`/`-d` (defaulted to your OS user) | `PGPASSWORD=ers psql -h localhost -U ers -d ers` |

---

## Appendix — installing the toolchain

Install anything the [verify-toolchain](#verify-your-toolchain) check flagged as missing. Each
tool below has steps for **Linux (Debian/Ubuntu/Mint, `apt`)**, **macOS (Homebrew)**, and
**Windows**. On this dev box everything is already installed - this appendix is for a fresh
machine. After installing, re-run the verify commands and return to Step 1.

> **The versions that matter:** a **JDK 8** for the build, and **Tomcat 9** (not 10+). Maven and
> PostgreSQL are version-tolerant (any recent Maven 3.x, PostgreSQL 12+).

### Java (JDK 8)

The build needs a JDK **8** specifically (`javac`, not just a JRE). You can keep a newer JDK as
your default - Step 2 points `JAVA_HOME` at the 8 explicitly.

- **Linux (apt):**
  ```
  sudo apt update && sudo apt install openjdk-8-jdk
  # find the install path for JAVA_HOME (Step 2):
  ls /usr/lib/jvm | grep -i 8        # e.g. java-8-openjdk-amd64
  ```
- **macOS (Homebrew):**
  ```
  brew install --cask temurin@8
  /usr/libexec/java_home -v 1.8      # prints the JAVA_HOME path to use in Step 2
  ```
- **Windows:** download the **Eclipse Temurin JDK 8** `.msi` from <https://adoptium.net> and
  install. `JAVA_HOME` is then e.g. `C:\Program Files\Eclipse Adoptium\jdk-8...`. (In WSL, use
  the Linux apt steps instead - simpler for this servlet stack.)
- **Verify:** `"$JAVA_HOME"/bin/javac -version` prints `javac 1.8.0_xxx`.

### Maven

- **Linux (apt):** `sudo apt install maven`
- **macOS:** `brew install maven`
- **Windows:** `winget install Apache.Maven` (or unzip the binary from
  <https://maven.apache.org/download.cgi> and add its `bin` to `PATH`).
- **Verify:** `mvn -version` (any Maven 3.x). It uses whatever `JAVA_HOME` you export in Step 2.

### PostgreSQL

- **Linux (apt):**
  ```
  sudo apt install postgresql
  sudo systemctl enable --now postgresql     # start it and start on boot
  ```
- **macOS (Homebrew):**
  ```
  brew install postgresql@16
  brew services start postgresql@16
  ```
- **Windows:** run the EnterpriseDB installer from
  <https://www.postgresql.org/download/windows/>; it installs and starts the service and gives
  you `psql` and pgAdmin.
- **Verify:** `psql --version` (12+). The service must be *running* before Step 1 - see the first
  troubleshooting row if a connection is refused. Step 1 creates the `ers` role and database.

### Apache Tomcat 9

**Which distribution, and why.** From <https://tomcat.apache.org/download-90.cgi>, under
**Binary Distributions → Core**, take **`apache-tomcat-9.0.x.tar.gz`** (Linux/macOS) or the
**`.zip`** (Windows). Reasoning:

- **Tomcat 9, not 10+** - 9 runs the `javax.servlet` API this app targets; Tomcat 10 switched to
  `jakarta.servlet` and will refuse the WAR.
- **"Core", not "Full documentation", "Deployer", or the source package** - Core is the runnable
  server. The Deployer is a CI/CD helper; the source needs compiling.
- **`.tar.gz` on Linux/macOS** preserves the executable bit on the `bin/*.sh` scripts; use the
  **`.zip`** on Windows (which ships `bin/*.bat`).
- **Not `apt install tomcat9`** - recent Ubuntu/Mint **dropped that package**, and its layout
  (`/etc/tomcat9`, systemd) splits the install across the filesystem instead of the single
  `<tomcat>` directory this guide assumes. The Core archive is self-contained, version-pinned,
  and needs no root.

- **Linux / macOS:**
  ```
  cd ~
  curl -LO https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.119/bin/apache-tomcat-9.0.119.tar.gz
  tar xzf apache-tomcat-9.0.119.tar.gz
  mv apache-tomcat-9.0.119 tomcat9      # name it ~/tomcat9 to match this guide
  ~/tomcat9/bin/version.sh              # -> Server version: Apache Tomcat/9.0.119
  rm ~/apache-tomcat-9.0.119.tar.gz     # optional cleanup
  ```
- **Windows:** unzip `apache-tomcat-9.0.x.zip` to e.g. `C:\tomcat9`; start it with
  `C:\tomcat9\bin\catalina.bat run`. Wherever `<tomcat>` appears in this guide, use `C:\tomcat9`,
  and use `setenv.bat` (not `setenv.sh`) for the Step 3 environment variables.

Tomcat needs a JDK/JRE on `PATH` (you installed one above). After this, return to **Step 3** -
create `<tomcat>/bin/setenv.sh` with the database variables, then deploy in Step 4.
