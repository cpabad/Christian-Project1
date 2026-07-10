package com.revature.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.util.FlowTrace;

/**
 * Front controller for /app/*. Unauthenticated GETs are redirected to the login view by
 * SessionFilter; this controller additionally requires a session for the state-changing methods
 * (POST/PUT/DELETE), exempting login URLs, so an unauthenticated write is rejected rather than
 * processed. Role-based access is enforced by EmployeeFilter / ManagerFilter.
 */
public class FrontController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public FrontController() {
		super();
	}

	/** True when the request has no session and is not a login URL. */
	private boolean isUnauthenticated(HttpServletRequest request) {
		return request.getSession(false) == null && !request.getRequestURI().contains("login");
	}

	private void rejectUnauthenticated(HttpServletResponse response) throws IOException {
		FlowTrace.log(FrontController.class, "write-auth check: FAIL (no session, not a login URL) - rejecting with 401 before any routing");
		response.setStatus(401);
		response.getWriter().write(new ObjectMapper().writeValueAsString("You're not authenticated"));
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FlowTrace.log(FrontController.class, "doGet received GET " + request.getRequestURI() + " - handing off to RequestHelper");
		response.setContentType("application/json");
		String JSON = new ObjectMapper().writeValueAsString(RequestHelper.processGet(request, response));
		response.getWriter().write(JSON);
		FlowTrace.log(FrontController.class, "result serialized to JSON; information is being sent to the client (status " + response.getStatus() + ")");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FlowTrace.log(FrontController.class, "doPost received POST " + request.getRequestURI()
				+ " - write-auth check: " + (isUnauthenticated(request) ? "FAIL" : "PASS (session exists or login URL)"));
		response.setContentType("application/json");
		if (isUnauthenticated(request)) {
			rejectUnauthenticated(response);
			return;
		}
		String JSON = new ObjectMapper().writeValueAsString(RequestHelper.processPost(request, response));
		response.getWriter().write(JSON);
		FlowTrace.log(FrontController.class, "result serialized to JSON; information is being sent to the client (status " + response.getStatus() + ")");
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FlowTrace.log(FrontController.class, "doPut received PUT " + request.getRequestURI()
				+ " - write-auth check: " + (isUnauthenticated(request) ? "FAIL" : "PASS (session exists or login URL)"));
		response.setContentType("application/json");
		if (isUnauthenticated(request)) {
			rejectUnauthenticated(response);
			return;
		}
		String JSON = new ObjectMapper().writeValueAsString(RequestHelper.processPut(request, response));
		response.getWriter().write(JSON);
		FlowTrace.log(FrontController.class, "result serialized to JSON; information is being sent to the client (status " + response.getStatus() + ")");
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FlowTrace.log(FrontController.class, "doDelete received DELETE " + request.getRequestURI()
				+ " - write-auth check: " + (isUnauthenticated(request) ? "FAIL" : "PASS (session exists or login URL)"));
		response.setContentType("application/json");
		if (isUnauthenticated(request)) {
			rejectUnauthenticated(response);
			return;
		}
		String JSON = new ObjectMapper().writeValueAsString(RequestHelper.processDelete(request, response));
		response.getWriter().write(JSON);
		FlowTrace.log(FrontController.class, "result serialized to JSON; information is being sent to the client (status " + response.getStatus() + ")");
	}

}
