# ERS monolith — dev-environment runtime image (Phase 12, 2026-07-25).
#
# This image PACKAGES the app; it does not change it. The monolith is frozen by design
# (see REFERENCE.md / ROADMAP standing rule 2) — nothing here alters application code,
# config, or behaviour. `mvn package` still produces a WAR that deploys to a plain Tomcat 9
# by hand exactly as STARTUP.md describes; this file just gives Jenkins something to ship.
#
# Base image rationale: the app pins maven.compiler to 1.8 and has always been documented
# against Tomcat 9, so the runtime is pinned to the matching official image rather than
# "latest". Tomcat 10+ would BREAK this app outright — it moved the servlet API from
# javax.* to jakarta.*, and every servlet in this repo imports javax.servlet.
#
# The image carries NO credentials. The app reads its database settings from the
# environment (System.getenv("dburl"/"dbuser"/"dbpassword") — see HibernateSessionFactory
# and ConnectionFactory), so they are injected at `docker run` time from the Jenkins
# credential store and never baked into a layer.
FROM tomcat:9.0-jre8-temurin

# The WAR's FILENAME is the context path Tomcat serves it under. Maven names the artifact
# ReimbursementManagement-<version>.war, so it is renamed to drop the version — that keeps
# the URL at /ReimbursementManagement/ for every version, which is what STARTUP.md's URLs
# and the Uptime Kuma monitor already point at. Copy-to-directory then move: a COPY glob
# straight onto a file destination is only legal while exactly one file matches, and this
# survives a stray second artifact in target/.
COPY target/ReimbursementManagement-*.war /tmp/war/
RUN mv /tmp/war/*.war /usr/local/tomcat/webapps/ReimbursementManagement.war && rmdir /tmp/war

# Reuses the Phase 7 /health endpoint (app + DB probe: 200 UP, 503 DOWN), so `docker ps`
# reports healthy/unhealthy on the same signal Uptime Kuma polls. start-period covers
# Tomcat boot + WAR expansion before the first failure is allowed to count.
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -fsS http://127.0.0.1:8080/ReimbursementManagement/health | grep -q UP || exit 1

EXPOSE 8080
