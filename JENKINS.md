# Jenkins CI — this repo's pipeline

Continuous integration for the monolith. On every push (and on demand via **Build Now**),
Jenkins rebuilds the WAR from a fresh clone, runs all 125 tests against a throwaway seeded
database, and runs three security scans. The pipeline is fully implemented in the
[`Jenkinsfile`](Jenkinsfile) at the repo root — its header comment is the detailed design doc;
this file is the operator's view.

## Where the controller lives

One Jenkins controller serves both ERS repos, and its infrastructure (Dockerfile, compose
file, watchdog, first-time setup guide) lives in the **microservice repo**:
`~/Repo/Revature931-Project1-Microservice/jenkins/`. Start it there:

```bash
cd ~/Repo/Revature931-Project1-Microservice/jenkins
docker compose up -d --build        # UI at http://localhost:8090 (loopback-only)
```

This repo contributes only the `Jenkinsfile`; the `ers-monolith` job points at this repo's
GitHub URL and reads it from the root. Why not Jenkins inside Tomcat (the Revature-era setup):
that shares a JVM, port, and lifecycle with the Tomcat that serves the app under test — the
Docker container is fully isolated from it.

## The pipeline at a glance

| Stage | What runs | On findings |
| --- | --- | --- |
| Build | `mvn package` in a `maven:3.8-openjdk-8` container (the JDK-8 pin travels with CI) | FAILURE |
| Tests | 125 tests against a per-build `postgres:16` sidecar seeded from `ers_script.sql` | FAILURE |
| SCA | Trivy — known CVEs in dependencies (HIGH/CRITICAL) | UNSTABLE (warn-mode) |
| SAST | Semgrep — dangerous patterns in our own source | UNSTABLE (warn-mode) |
| Secrets | gitleaks — credentials in the working tree | FAILURE (no grace period) |

Notifications go to Discord on FAILURE, UNSTABLE, and recovery, naming the commit author.
The webhook URL lives only in Jenkins' credentials store (id `discord-webhook`).

Triggering is **SCM polling** (`H/2 * * * *`) — Jenkins dials out to GitHub every ~2 minutes;
nothing dials in. Combined with the loopback-only port bind, this Jenkins has zero inbound
network exposure (full security posture: STARTUP.md, "Can this thing be used to break into my
machine?").

## Result semantics

- **SUCCESS** — build, tests, and scans all clean. The working agreement: once Jenkins is
  green, stop worrying about the code until a scan raises a concern.
- **UNSTABLE** — a security scan reported findings. This is the *warn-then-ratchet* policy:
  SCA/SAST findings warn first so the initial backlog can be triaged without blocking work.
  **The ratchet:** after triage, delete the `catchError` wrappers in the `Jenkinsfile` and
  those scans become hard failures.
- **FAILURE** — compilation, a test, or the secrets gate broke. Fix before anything else.

## Boundary rule

Jenkins work never touches `.github/workflows/` (the GitHub-to-GitLab mirroring is a separate,
independently-owned system with its own credentials) — a CI change's diff may contain only the
`Jenkinsfile` and this file. And the invariant that shaped the pipeline: **the application
builds, tests, and runs identically whether or not Jenkins exists.** CI observes the code; it
never shapes it.

## Local facts CI depends on (do not "fix" these)

- The Maven project is `ReimbursementManagement/`, not the repo root.
- Tests need env vars `dburl` / `dbuser` / `dbpassword` (lowercase) and the seeded schema
  `"ExpenseReimbursementManagementSystem"` — the pipeline reproduces STARTUP.md Step 1
  exactly, including prepending `CREATE SCHEMA` and filtering the seed script's broken
  `search_path` / `DROP TABLE` lines.
- Seed `INSERT` order matters (repository queries have no `ORDER BY`); the seed is never
  reordered.
