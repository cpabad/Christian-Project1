package com.revature.controller;

import java.io.IOException;
import java.sql.Date;

import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.ObjectNotFoundException;

import com.revature.model.AmazonS3Object;
import com.revature.model.EventLocation;
import com.revature.model.Request;
import com.revature.model.User;
import com.revature.service.AmazonS3ObjectService;
import com.revature.service.CityStatePostalService;
import com.revature.service.EventLocationService;
import com.revature.service.HierarchyService;
import com.revature.service.ReimbursementService;
import com.revature.service.RequestService;
import com.revature.service.RequestStatusService;
import com.revature.service.UserService;

@MultipartConfig(location = "F:\\1-25-2021-msa\\project\\reimbursement_management\\images\\")
public class RequestHelper {
	
	private static final Logger LOG = LogManager.getLogger(RequestHelper.class);
	
	public static Object processGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String URI = request.getRequestURI();
		String resource = URI.replace("/ReimbursementManagement/app", "");
		LOG.debug("Get request hitting the servlet mapped to: " + resource);
		switch(resource) {
		case "/login":
			response.sendRedirect("/ReimbursementManagement/");
			return null;
		case "/upload-file":
			LOG.debug("Upload Successful");
			return "The file was uploaded successfully! Please exit this page to return to the homepage.";
		case "/employee/view-user-information":
			return new UserService().findById((Integer) request.getSession(false).getAttribute("userId"));
//		case "/employee/home":
//			response.sendRedirect("/ReimbursementManagement/app/employee/");
		case "/employee/view-pending-requests":
			final int vprEmpUserId = (Integer) request.getSession(false).getAttribute("userId");
			return new RequestService().findByRequesterAndPendingStatus(new UserService().findById(vprEmpUserId));
		case "/employee/view-resolved-requests":
			final int vrrEmpUserId = (Integer) request.getSession(false).getAttribute("userId");
			return new RequestService().findByRequesterAndResolvedStatus(new UserService().findById(vrrEmpUserId));
		case "/manager/view-pending-requests":
			final int vprMgrUserId = (Integer) request.getSession(false).getAttribute("userId");
			return new RequestService().findByRequesterAndPendingStatus(new UserService().findById(vprMgrUserId));
		case "/manager/view-resolved-requests":
			final int vrrMgrUserId = (Integer) request.getSession(false).getAttribute("userId");
			return new RequestService().findByRequesterAndResolvedStatus(new UserService().findById(vrrMgrUserId));
		case "/manager/view-employee-pending-requests":
			
		case "/manager/view-employees-and-managers":
			return new HierarchyService().findAll();
		case "/manager/view-employee-resolved-requests":
			return new ReimbursementService().findAll();
		case "/logout":
			if(request.getSession(false) != null) {
				final String logoutUsername = (String) request.getSession(false).getAttribute("username");
				request.getSession(false).invalidate();
				LOG.debug("Logout Successful");
				return "You have successfully logged out, " + logoutUsername + ". Come back soon!"; 
			} else {
				response.setStatus(400);
				LOG.debug("No user was logged in");
				return "There was no user logged into the session";
			}
		default:
			response.setStatus(401);
			LOG.debug("Requested action is not permitted");
			return "The requested action is not permitted";
		}
	}
	
	public static Object processPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String URI = request.getRequestURI();
		String resource = URI.replace("/ReimbursementManagement/app", "");
		LOG.debug("Post request hitting the servlet mapped to: " + resource);
		switch(resource) {
		case "/login":
			final String loginUsername = request.getParameter("username");
			final String loginPassword = request.getParameter("password");
			User user = null;
			if(loginUsername.contains("@") && loginUsername.contains(".com") || loginUsername.contains(".net")|| loginUsername.contains(".us")|| loginUsername.contains(".edu")|| loginUsername.contains(".co")) {
				user = new UserService().findByEmail(loginUsername);
			} else {
				user = new UserService().findByUsername(loginUsername);
			}
			if(request.getSession(false) != null) {
				response.setStatus(400);
				LOG.debug("Client already has a current session");
				return "You already have a current session. Logout before continuing.";
			} else if(user != null && loginPassword.isBlank() == false) {
				if(loginPassword.equals(user.getPassword())) {
					HttpSession session = request.getSession();
					session.setAttribute("userId", user.getUserId());
					session.setAttribute("username", user.getUsername());
					session.setAttribute("email", user.getEmail());
					session.setAttribute("role", user.getRole().getRole());
					LOG.debug("Login Successful");
					return user.getRole().getRole();
				} else {
					response.setStatus(400);
					LOG.debug("Invalid Credentials");
					return "Invalid Credentials";
				}
			} else {
				response.setStatus(400);
				LOG.debug("Invalid Credentials");
				return "Invalid Credentials";
			}
		case "/employee/update-user-information":
			User updatedUser = new UserService().findById((Integer) request.getSession(false).getAttribute("userId"));
			if(request.getParameter("oldusername") != null && updatedUser.getUsername().equals(request.getParameter("oldusername"))) {
				if(request.getParameter("newusername") != null) {
					try {
						new UserService().findByUsername(request.getParameter("newusername"));
					} catch (NonUniqueObjectException e) {
						response.setStatus(400);
						LOG.debug("Invalid entries");
						return "Invalid entries. Please try again.";
					} catch (ObjectNotFoundException e) {
						updatedUser.setUsername(request.getParameter("newusername"));
					} catch (NoResultException e) {
						updatedUser.setUsername(request.getParameter("newusername"));
					}
				}
			} else {
				response.setStatus(400);
				return "Invalid entries. Please try again.";
			}
			if(request.getParameter("oldpassword") != null && updatedUser.getPassword().equals(request.getParameter("oldpassword"))) {
				if(request.getParameter("newpassword") != null) {
					updatedUser.setUsername(request.getParameter("newpassword"));
				}
			} else {
				response.setStatus(400);
				LOG.debug("Invalid entries");
				return "Invalid entries. Please try again.";
			}
			if(request.getParameter("oldemail") != null && updatedUser.getEmail().equals(request.getParameter("oldemail"))) {
				if(request.getParameter("newemail") != null) {
					try {
						new UserService().findByEmail(request.getParameter("newemail"));
					} catch (NonUniqueObjectException e) {
						response.setStatus(400);
						LOG.debug("Invalid entries");
						return "Invalid entries. Please try again.";
					} catch (ObjectNotFoundException e) {
						updatedUser.setEmail(request.getParameter("newemail"));
					} catch (NoResultException e) {
						updatedUser.setUsername(request.getParameter("newemail"));
					}
				}
			} else {
				response.setStatus(400);
				LOG.debug("Invalid entries");
				return "Invalid entries. Please try again.";
			}
			new UserService().update(updatedUser);
			LOG.debug("Update successful");
			return "Update was successful";
		case "employee/submit-request":
			Request submitRequest = new Request(
					100,
					Double.parseDouble(request.getParameter("amount")),
					Date.valueOf(request.getParameter("eventYear")+"-"+request.getParameter("eventMonth")+"-"+request.getParameter("eventDay")), 
					new EventLocationService().findByStreetNumberNamePostalCode(Integer.parseInt(request.getParameter("streetNumber")), request.getParameter("streetName").toUpperCase(), new CityStatePostalService().findByPostal(Integer.parseInt(request.getParameter("zipCode")))), 
					request.getParameter("eventTitle"), 
					new UserService().findByUsername(request.getParameter("username")), 
					new RequestStatusService().findById(1));
			new RequestService().makeNewRequest(submitRequest);
			request.getSession(false).setAttribute("requestDate", submitRequest.getEventDate().toString());
			request.getSession(false).setAttribute("requestLocation", submitRequest.getEventLocation());
			
		case "/upload-file":
			LOG.debug("Upload successful");
			new AmazonS3ObjectService().addObject(new AmazonS3Object(100, request.getParameter("url"), new RequestService().findByDateLocationRequester((String) request.getSession(false).getAttribute("requestDate"), (EventLocation) request.getSession(false).getAttribute("requestLocation"), new UserService().findById((Integer) request.getSession(false).getAttribute("userId")))));
			return "The file was uploaded successfully! Please exit this page to return to the homepage.";
		case "/logout":
			if(request.getSession(false) != null) {
				final String logoutUsername = (String) request.getSession().getAttribute("username");
				request.getSession().invalidate();
				LOG.debug("Logout Successful");
				return "You have successfully logged out, " + logoutUsername + ". Come back soon!"; 
			} else {
				response.setStatus(400);
				LOG.debug("No users were logged in");
				return "There was no user logged into the session";
			}
		default:
			response.setStatus(401);
			LOG.debug("Requested action is not permitted");
			return "The requested action is not permitted";
		}
	}
	
	public static Object processPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return null;
	}
	
	public static Object processDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return null;
	}

}
