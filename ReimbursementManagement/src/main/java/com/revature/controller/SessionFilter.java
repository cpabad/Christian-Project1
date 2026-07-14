package com.revature.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.revature.util.FlowTrace;

/**
 * Servlet Filter implementation class SessionFilter
 *
 * Mapped to /* so it is the FIRST filter every request meets - which makes it the owner of
 * the FLOW trace frame: begin() here, end() in the finally, so the request id spans the whole
 * chain (filters, forwards, servlet, services) and pooled threads never leak a stale id.
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
		final boolean flowOwner = FlowTrace.begin();
		try {
			HttpSession session = httpRequest.getSession(false);
			FlowTrace.log(SessionFilter.class, httpRequest.getMethod() + " " + httpRequest.getRequestURI()
					+ " received; session: " + (session == null ? "none" : "active (role=" + session.getAttribute("role") + ")"));
			// /health is exempt from the login forward: monitor probes (Uptime Kuma, K8s-style
			// checks) are anonymous GETs, and forwarding them to the login view would hand the
			// monitor a false 200 "everything is fine" page instead of the real health verdict.
			if(session == null && httpRequest.getRequestURI().equals("/ReimbursementManagement/") == false
					&& httpRequest.getRequestURI().equals("/ReimbursementManagement/health") == false
					&& httpRequest.getMethod().equals("GET")) {
				FlowTrace.log(SessionFilter.class, "auth check: FAIL (no session on a non-landing GET) - forwarding to app/login view");
				request.getRequestDispatcher("app/login").forward(httpRequest, response);
				return;
//		} else if(session != null && role == "Employee" && httpRequest.getRequestURI().equals("/ReimbursementManagement/upload-file")) {
//			System.out.println("Hello!");
//			RequestDispatcher dispatcher = request.getRequestDispatcher("app/login");
//			dispatcher.forward(httpRequest, response);
			} else if(session != null && httpRequest.getRequestURI().contains("/manager/") && "Employee".equals((String) session.getAttribute("role"))) {
				FlowTrace.log(SessionFilter.class, "role check: FAIL (Employee role on a /manager/ URL) - forwarding to app/deny");
				request.getRequestDispatcher("app/deny").forward(httpRequest, response);
				return;
			}
			FlowTrace.log(SessionFilter.class, "auth check: PASS - continuing down the filter chain");
			chain.doFilter(request, response);
		} finally {
			if (flowOwner) {
				FlowTrace.log(SessionFilter.class, "response on its way to the client - request frame ends");
				FlowTrace.end();
			}
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
