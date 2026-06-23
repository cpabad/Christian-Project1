package com.revature.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

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
		response.setStatus(401);
		response.getWriter().write(new ObjectMapper().writeValueAsString("You're not authenticated"));
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		String JSON = new ObjectMapper().writeValueAsString(RequestHelper.processGet(request, response));
		response.getWriter().write(JSON);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		if (isUnauthenticated(request)) {
			rejectUnauthenticated(response);
			return;
		}
		String JSON = new ObjectMapper().writeValueAsString(RequestHelper.processPost(request, response));
		response.getWriter().write(JSON);
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		if (isUnauthenticated(request)) {
			rejectUnauthenticated(response);
			return;
		}
		String JSON = new ObjectMapper().writeValueAsString(RequestHelper.processPut(request, response));
		response.getWriter().write(JSON);
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		if (isUnauthenticated(request)) {
			rejectUnauthenticated(response);
			return;
		}
		String JSON = new ObjectMapper().writeValueAsString(RequestHelper.processDelete(request, response));
		response.getWriter().write(JSON);
	}

}
