package com.revature.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class EmployeeFilter
 */
public class EmployeeFilter implements Filter {

    /**
     * Default constructor. 
     */
    public EmployeeFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
//		HttpServletRequest httpRequest = (HttpServletRequest) request;
//		HttpServletResponse httpResponse = (HttpServletResponse) response;
//		HttpSession session = httpRequest.getSession(false);
//		String role = (String) session.getAttribute("role");
//		System.out.println(httpRequest.getRequestURI());
//		if(role == "Employee" && httpRequest.getRequestURI().equals("/ReimbursementManagement/app/employee/")) {
//			chain.doFilter(request, response);
//		} else if(role == "Employee" && httpRequest.getRequestURI().equals("/ReimbursementManagement/app/employee/home")) {
			chain.doFilter(request, response);
//		} else {
//			httpResponse.sendError(401, "The requested action is not permitted");
//		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
