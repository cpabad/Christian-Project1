package com.revature.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class SessionFilter
 */
public class SessionFilter implements Filter {

    /**
     * Default constructor. 
     */
    public SessionFilter() {
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
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession(false);
		if(session == null && httpRequest.getRequestURI().equals("/ReimbursementManagement/") == false && httpRequest.getMethod().equals("GET")) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("app/login");
			dispatcher.forward(httpRequest, response);
//		} else if(session != null && role == "Employee" && httpRequest.getRequestURI().equals("/ReimbursementManagement/upload-file")) {
//			System.out.println("Hello!");
//			RequestDispatcher dispatcher = request.getRequestDispatcher("app/login");
//			dispatcher.forward(httpRequest, response);
		} else if(session != null && httpRequest.getRequestURI().contains("supervisor") && (String) session.getAttribute("role") == "Employee") {
			RequestDispatcher dispatcher = request.getRequestDispatcher("app/deny");
			dispatcher.forward(httpRequest, response);
		}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
