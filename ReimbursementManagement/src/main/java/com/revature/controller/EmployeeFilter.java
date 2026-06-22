package com.revature.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter for /app/employee/*. Restricts those URLs to authenticated
 * users with the Employee role (the mirror of ManagerFilter for /app/manager/*).
 * Login URLs are exempt so a user can authenticate; everyone else is sent to the
 * deny view.
 */
public class EmployeeFilter implements Filter {

	public EmployeeFilter() {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession(false);
		if(session != null && "Employee".equals((String) session.getAttribute("role"))) {
			chain.doFilter(request, response);
		} else if(httpRequest.getRequestURI().contains("login")) {
			chain.doFilter(request, response);
		} else {
			request.getRequestDispatcher("app/deny").forward(httpRequest, response);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
