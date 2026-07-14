/*
 * ERS monolith CI pipeline — FULLY IMPLEMENTED (Fable, 2026-07-13).
 *
 * What Jenkins is here: the team you don't have. On every push (SCM poll, ~2 min) it builds
 * from a FRESH clone in CLEAN containers, runs all 125 tests against a THROWAWAY seeded
 * database, and runs three security scans — so "green" means "a neutral witness rebuilt and
 * re-verified everything from scratch", not "it worked on my machine". The job's
 * build-stability indicator summarizes recent health at a glance.
 *
 * Scan policy (owner-ruled 2026-07-13, ratcheted 2026-07-14): SCA is now a HARD GATE — the
 * initial 36-finding backlog was triaged and fixed (see CHANGELOG), so a HIGH/CRITICAL CVE,
 * or a scanner crash, is red from here on. The first run taught the expensive lesson: a
 * catchError around a scanner turns "the scanner crashed and scanned nothing" into the same
 * yellow as "findings were reported" — warn-mode hid a 429 FATAL for a full build cycle.
 * SAST stays warn-then-ratchet (Semgrep: currently 0 findings). The secrets scan has been a
 * hard RED from day one: a credential in the working tree is never a warning.
 *
 * Repo-specific facts an implementer must not "fix":
 *   - The Maven project is ReimbursementManagement/, NOT the repo root.
 *   - Java 8 pin (maven.compiler 1.8) -> maven:3.8-openjdk-8 image. The container sidesteps
 *     the host's java-21/javac-8 JAVA_HOME trap entirely (see STARTUP.md).
 *   - The repository tests are integration tests: they need Postgres seeded from
 *     ers_script.sql IN ITS INSERT ORDER (queries have no ORDER BY). The script never
 *     creates its schema and its search_path line is misspelled — the seed step below
 *     prepends the real CREATE SCHEMA and strips the broken lines (same recipe as STARTUP.md).
 *   - DB config is read from env vars dburl/dbuser/dbpassword (lowercase, case-sensitive).
 *
 * Infra notes:
 *   - Controller runs in Docker with the host socket (docker-outside-of-docker). Workspace
 *     paths only reach sibling containers through the Docker Pipeline plugin's .inside()
 *     (it uses --volumes-from) — NEVER `docker run -v $WORKSPACE:...`, that path doesn't
 *     exist on the host side of the socket.
 *   - Scanner images ship ENTRYPOINTs; .inside() needs --entrypoint='' or the plugin's
 *     `cat` keep-alive becomes `trivy cat` and dies.
 *   - Maven cache: workspace-local repo (-Dmaven.repo.local) — survives between builds in
 *     the job workspace, no root-owned named-volume permission headaches.
 *
 * Boundary: this file is the whole CI footprint of this repo. The GitHub->GitLab mirror
 * (microservice repo, .github/workflows/) is a separate Fable-owned system — never touch it
 * from Jenkins work.
 */

def notifyDiscord(String message) {
  // Webhook URL lives ONLY in the Jenkins credentials store (Secret text, id: discord-webhook).
  // No credential yet -> log and move on; notification must never break the build.
  try {
    withCredentials([string(credentialsId: 'discord-webhook', variable: 'DISCORD_URL')]) {
      def safe = message.replace('\\', '\\\\').replace('"', '\\"')
      writeFile file: '.discord-payload.json', text: "{\"content\": \"${safe}\"}"
      sh 'curl -fsS -o /dev/null -H "Content-Type: application/json" -d @.discord-payload.json "$DISCORD_URL" || true'
    }
  } catch (ignored) {
    echo "Discord notify skipped - add a Secret-text credential with id 'discord-webhook' to enable pings."
  }
}

def blame() {
  // "Who to blame" — on a one-person team this is a mirror, but it's the habit that counts.
  try {
    return sh(script: "git log -1 --pretty='%an'", returnStdout: true).trim()
  } catch (ignored) {
    return 'unknown'
  }
}

pipeline {
  agent any   // the controller node — it owns the docker CLI; heavy work runs in containers

  triggers {
    pollSCM('H/2 * * * *')   // polling, not webhooks: GitHub can't reach a non-public box
  }

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '25'))
  }

  environment {
    MAVEN_IMAGE = 'maven:3.8-openjdk-8'                      // the monolith's JDK-8 pin
    // Canonical local-repo path (~/.m2/repository under a workspace HOME): survives between
    // builds AND is exactly where Trivy resolves versions offline (see the SCA stage).
    MVN = 'mvn -B -ntp -Dmaven.repo.local="$WORKSPACE/.m2/repository"'
  }

  stages {

    stage('Build') {
      steps {
        script {
          docker.image(env.MAVEN_IMAGE).inside {
            dir('ReimbursementManagement') {
              // -Djacoco.skip goes with -DskipTests: the pom binds jacoco report/check to the
              // test PHASE, which still runs under skipTests - and because the workspace
              // persists between builds, jacoco loads the PREVIOUS build's jacoco.exec against
              // freshly recompiled classes ("execution data does not match"), reads the changed
              // classes as uncovered, and fails the coverage gate on stale data (build 2 died
              // exactly this way). The real gate runs in the Tests stage, where `mvn test`
              // regenerates jacoco.exec before checking it.
              sh "$MVN -DskipTests -Djacoco.skip=true package"
            }
          }
        }
      }
      post {
        success {
          archiveArtifacts artifacts: 'ReimbursementManagement/target/*.war', fingerprint: true
        }
      }
    }

    stage('Tests — full suite against a fresh database') {
      steps {
        script {
          // Prepare the seed exactly like STARTUP.md: prepend the CREATE SCHEMA the script
          // forgot, strip its broken search_path lines and destructive DROPs, keep INSERT order.
          sh '''
            { echo 'CREATE SCHEMA "ExpenseReimbursementManagementSystem";'
              echo 'SET search_path TO "ExpenseReimbursementManagementSystem";'
              sed '/^SHOW search_path/d;/^SET search_path/d;/^DROP TABLE/d' ReimbursementManagement/ers_script.sql
            } > .ci-seed.sql
          '''
          // Throwaway Postgres per build (hermetic — owner-ruled): withRun guarantees the
          // container dies even when tests fail. Sibling containers join its network
          // NAMESPACE (--network container:...), so 127.0.0.1:5432 is the sidecar.
          docker.image('postgres:16-alpine').withRun('-e POSTGRES_USER=ers -e POSTGRES_PASSWORD=ers -e POSTGRES_DB=ers') { db ->
            docker.image('postgres:16-alpine').inside("--network container:${db.id} -e PGPASSWORD=ers") {
              sh '''
                for i in $(seq 1 60); do pg_isready -h 127.0.0.1 -U ers -d ers -q && break; sleep 1; done
                psql -h 127.0.0.1 -U ers -d ers -v ON_ERROR_STOP=1 -f .ci-seed.sql
              '''
            }
            docker.image(env.MAVEN_IMAGE)
                  .inside("--network container:${db.id} -e dburl=jdbc:postgresql://127.0.0.1:5432/ers -e dbuser=ers -e dbpassword=ers") {
              dir('ReimbursementManagement') {
                sh "$MVN test"
              }
            }
          }
        }
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'ReimbursementManagement/target/surefire-reports/*.xml'
        }
      }
    }

    stage('SCA — dependency CVEs (Trivy)') {
      steps {
        script {
          // Trivy over Dependency-Check on purpose: no NVD API key to manage (credential
          // friction is the enemy in this shop). Cache in the workspace: jenkins-uid writable.
          // HOME=$WORKSPACE: to report a CVE Trivy must first RESOLVE each pom's versions,
          // which means reading ~/.m2/repository — the cache the Build stage already populated.
          // Without it Trivy hits Maven Central and gets 429-throttled into a FATAL (exactly
          // what build 1 hid behind the old catchError). HARD GATE — no catchError: findings
          // are red, and so is a scanner crash. Accepted-risk CVEs live in the tracked
          // .trivyignore (each entry carries its justification).
          docker.image('aquasec/trivy:latest').inside("--entrypoint='' -e HOME=$WORKSPACE -e TRIVY_CACHE_DIR=$WORKSPACE/.trivy-cache") {
            sh 'trivy fs --scanners vuln --severity HIGH,CRITICAL --exit-code 1 --no-progress --ignorefile ReimbursementManagement/.trivyignore ReimbursementManagement'
          }
        }
      }
    }

    stage('SAST — static analysis (Semgrep)') {
      steps {
        script {
          // Semgrep instead of SpotBugs HERE only: it reads source in its own container, so
          // the JDK-8 toolchain pin never matters. The microservice (Java 17) uses SpotBugs
          // + FindSecBugs per the ROADMAP. HOME=/tmp: the container runs as the jenkins uid.
          docker.image('semgrep/semgrep:latest').inside("--entrypoint='' -e HOME=/tmp") {
            catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE',
                       message: 'SAST findings — warn-mode; see log. Ratchet: delete this catchError.') {
              sh 'semgrep scan --config p/java --config p/security-audit --error ReimbursementManagement/src'
            }
          }
        }
      }
    }

    stage('Secrets — gitleaks (hard gate)') {
      steps {
        script {
          // Working tree only (--no-git): history contains long-revoked keys from before the
          // 2026 cleanup; a baseline-file history scan is a later, separate step. --redact so
          // a caught secret is not immortalized in the CI log. NO catchError: a live secret
          // in current files is red, full stop.
          // The workspace persists between builds (that's how the .m2 Maven cache works), so
          // the allowlist below keeps gitleaks off the cached third-party jars and scanner
          // caches — we scan OUR files, not the internet's.
          sh '''
            cat > .gitleaks-ci.toml <<'EOF'
[extend]
useDefault = true

[allowlist]
paths = [
  ".m2/.*",
  ".trivy-cache/.*",
  "ReimbursementManagement/target/.*",
]
EOF
          '''
          docker.image('zricethezav/gitleaks:latest').inside("--entrypoint=''") {
            sh 'gitleaks detect --source . --no-git --redact --verbose --config .gitleaks-ci.toml'
          }
        }
      }
    }
  }

  post {
    failure {
      script { notifyDiscord("FAILURE: **${env.JOB_NAME} #${env.BUILD_NUMBER}** failed on `${env.GIT_BRANCH ?: 'main'}` — author: ${blame()} — ${env.BUILD_URL}") }
    }
    unstable {
      script { notifyDiscord("WARNING: **${env.JOB_NAME} #${env.BUILD_NUMBER}** is UNSTABLE — a security scan reported findings (warn-mode) — ${env.BUILD_URL}") }
    }
    fixed {
      script { notifyDiscord("RECOVERED: **${env.JOB_NAME} #${env.BUILD_NUMBER}** is passing again — ${env.BUILD_URL}") }
    }
  }
}
