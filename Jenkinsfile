/*
 * ERS monolith CI pipeline — SKELETON (Fable, 2026-07-13).
 *
 * Same contract as the microservice repo's Jenkinsfile: every target stage exists, the
 * pipeline runs green, and the application is untouched — no Java, no dependency, no
 * behavior change. TODO(Opus) stages are inert echo stubs; locked decisions live in
 * ROADMAP.md Phase 7.5, setup steps in the microservice repo's jenkins/README.md
 * (one Jenkins controller serves both repos).
 *
 * Repo-specific facts an implementer must not "fix":
 *   - The Maven project is ReimbursementManagement/, NOT the repo root.
 *   - This codebase is pinned to Java 8 (maven.compiler 1.8) — hence the JDK-8 Maven
 *     image. Building on a newer JDK is the classic trap here (see STARTUP.md).
 *   - The test suite needs a seeded Postgres (db script + env vars — STARTUP.md).
 */
pipeline {
  agent {
    docker {
      image 'maven:3.8-openjdk-8'            // JDK 8 on purpose — the monolith's pin
      args '-v ers-jenkins-m2:/root/.m2'     // shared Maven cache with the other job
    }
  }

  triggers {
    pollSCM('H/2 * * * *')   // polling, not webhooks: GitHub can't reach a non-public box
  }

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '25'))
  }

  stages {
    stage('Build') {
      steps {
        dir('ReimbursementManagement') {
          sh 'mvn -B -ntp -DskipTests package'
        }
      }
    }

    stage('Unit tests') {
      steps {
        // TODO(Opus): needs a seeded Postgres (schema + seed per STARTUP.md) and the DB env
        // vars before this becomes:  dir('ReimbursementManagement') { sh 'mvn -B -ntp test' }
        // then:  junit 'ReimbursementManagement/target/surefire-reports/*.xml'
        echo 'STUB — unit tests not wired yet (needs in-pipeline DB provisioning)'
      }
    }

    stage('SCA — dependency CVEs') {
      steps {
        // TODO(Opus): same tool as the microservice job (Dependency-Check or Trivy). A Java 8
        // tree will surface OLD CVEs — expect real findings here, that is the point.
        echo 'STUB — SCA scan not wired yet'
      }
    }

    stage('SAST — static analysis') {
      steps {
        // TODO(Opus): SpotBugs + FindSecBugs (use versions that still support Java 8 bytecode).
        echo 'STUB — SAST scan not wired yet'
      }
    }

    stage('Secrets scan') {
      steps {
        // TODO(Opus): gitleaks over the checkout; fail on any hit.
        echo 'STUB — secrets scan not wired yet'
      }
    }
  }

  post {
    failure {
      // TODO(Opus): Discord webhook (credential id: discord-webhook) — status/branch/author.
      echo 'STUB — Discord failure notification not wired yet'
    }
    fixed {
      echo 'STUB — Discord recovery notification not wired yet'
    }
  }
}
