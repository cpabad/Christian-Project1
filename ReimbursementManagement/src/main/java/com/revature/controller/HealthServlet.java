package com.revature.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.util.ConnectionFactory;
import com.revature.util.FlowTrace;

/**
 * Heartbeat endpoint for external monitors (Uptime Kuma today; the same contract a Kubernetes
 * liveness/readiness probe, an ECS health check, or a Route 53 health check consumes).
 *
 * GET /ReimbursementManagement/health -> 200 {"status":"UP"} when the app can reach its
 * database, 503 {"status":"DOWN"} otherwise. Unauthenticated BY DESIGN: monitor probes are
 * anonymous and must never be bounced to a login page (SessionFilter exempts this path).
 * No session is created and nothing sensitive is revealed - the body says only whether the
 * app is serving.
 */
@WebServlet("/health")
public class HealthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger(HealthServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		FlowTrace.log(HealthServlet.class, "doGet: heartbeat probe received - checking database reachability");
		response.setContentType("application/json");
		if(databaseIsReachable()) {
			FlowTrace.log(HealthServlet.class, "probe outcome: database reachable - 200 UP");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write("{\"status\":\"UP\"}");
		} else {
			FlowTrace.log(HealthServlet.class, "probe outcome: database unreachable - 503 DOWN");
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			response.getWriter().write("{\"status\":\"DOWN\"}");
		}
	}

	/**
	 * Raw JDBC on purpose: the probe must still answer when Hibernate's SessionFactory cannot
	 * boot (hbm2ddl validate needs a live, matching schema), and the connection is closed
	 * immediately - a monitor polling every minute must not leak resources. Protected so tests
	 * can substitute the outcome without a real database.
	 */
	protected boolean databaseIsReachable() {
		try (Connection conn = ConnectionFactory.getConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery("SELECT 1")) {
			return rs.next();
		} catch (Exception e) {
			LOG.error("Health probe could not reach the database", e);
			return false;
		}
	}

}
