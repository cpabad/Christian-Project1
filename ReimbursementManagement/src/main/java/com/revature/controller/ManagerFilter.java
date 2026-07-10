package com.revature.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.revature.util.FlowTrace;

/**
 * Servlet Filter implementation class ManagerFilter
 */
public class ManagerFilter implements Filter {

    /**
     * Default constructor. 
     */
    public ManagerFilter() {
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
		if(session != null &&  "Supervisor".equals((String) session.getAttribute("role"))) {
			FlowTrace.log(ManagerFilter.class, "role check: PASS (session role is Supervisor) - continuing down the filter chain");
			chain.doFilter(request, response);
		} else if(httpRequest.getRequestURI().contains("login")) {
			FlowTrace.log(ManagerFilter.class, "role check: EXEMPT (login URL, no session required yet) - continuing down the filter chain");
			chain.doFilter(request, response);
		} else {
			FlowTrace.log(ManagerFilter.class, "role check: FAIL (no session or wrong role) - forwarding to app/deny");
			RequestDispatcher dispatcher = request.getRequestDispatcher("app/deny");
			dispatcher.forward(httpRequest, response);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
