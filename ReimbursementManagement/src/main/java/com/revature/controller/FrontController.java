package com.revature.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class FrontController
 */
public class FrontController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FrontController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		String JSON = new ObjectMapper().writeValueAsString(RequestHelper.processGet(request, response));
		response.getWriter().write(JSON);
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		String JSON = new ObjectMapper().writeValueAsString(RequestHelper.processPost(request, response));
		response.getWriter().write(JSON);
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		if(request.getMethod().equals("PUT")) {
			String JSON = new ObjectMapper().writeValueAsString(RequestHelper.processPut(request, response));
			response.getWriter().write(JSON);
		} else {
			response.setStatus(401);
			response.getWriter().write(new ObjectMapper().writeValueAsString("You're not authenticated"));
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		if(request.getMethod().equals("DELETE")) {
			String JSON = new ObjectMapper().writeValueAsString(RequestHelper.processDelete(request, response));
			response.getWriter().write(JSON);
		} else {
			response.setStatus(401);
			response.getWriter().write(new ObjectMapper().writeValueAsString("You're not authenticated"));
		}
	}

}
