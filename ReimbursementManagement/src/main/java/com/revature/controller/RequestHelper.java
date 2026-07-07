package com.revature.controller;

import java.io.IOException;
import java.sql.Date;

import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.revature.model.AmazonS3Object;
import com.revature.model.ApprovalOutcome;
import com.revature.model.ProfileUpdateForm;
import com.revature.model.Request;
import com.revature.model.User;
import com.revature.service.AmazonS3ObjectService;
//import com.revature.service.AmazonS3ObjectService;
import com.revature.service.CityStatePostalService;
import com.revature.service.EventLocationService;
import com.revature.service.HierarchyService;
import com.revature.service.RequestService;
import com.revature.service.RequestStatusService;
import com.revature.service.SupervisorApprovalService;
import com.revature.service.UserService;

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
		case "/employee/view-requests":
			final int vrEmpUserId = (Integer) request.getSession(false).getAttribute("userId");
			return new RequestService().findByRequester(new UserService().findById(vrEmpUserId));
		case "/employee/view-pending-requests":
			final int vprEmpUserId = (Integer) request.getSession(false).getAttribute("userId");
			return new RequestService().findByRequesterAndPendingStatus(new UserService().findById(vprEmpUserId));
		case "/employee/view-resolved-requests":
			final int vrrEmpUserId = (Integer) request.getSession(false).getAttribute("userId");
			return new RequestService().findByRequesterAndResolvedStatus(new UserService().findById(vrrEmpUserId));
		case "/manager/view-user-information":
			return new UserService().findById((Integer) request.getSession(false).getAttribute("userId"));
		case "/manager/view-pending-requests":
			final int vprMgrUserId = (Integer) request.getSession(false).getAttribute("userId");
			return new RequestService().findByRequesterAndPendingStatus(new UserService().findById(vprMgrUserId));
		case "/manager/view-resolved-requests":
			final int vrrMgrUserId = (Integer) request.getSession(false).getAttribute("userId");
			return new RequestService().findByRequesterAndResolvedStatus(new UserService().findById(vrrMgrUserId));
		case "/manager/view-employee-pending-approvals":
			final int vepaMgrUserId = (Integer) request.getSession(false).getAttribute("userId");
			return new SupervisorApprovalService().findPendingRequestsForManager(new UserService().findById(vepaMgrUserId));
		case "/manager/view-employees-and-managers":
			return new HierarchyService().findAll();
		case "/manager/view-employee-resolved-approvals":
			return new SupervisorApprovalService().findWhoResolvedAllRequests();
		case "/manager/view-requests-by-employee":
			final int vrbeMgrUserId = (Integer) request.getSession(false).getAttribute("userId");
			return new SupervisorApprovalService().findRequestsMadeByEmployee(new UserService().findById(vrbeMgrUserId), new UserService().findById(Integer.parseInt(request.getParameter("employeeId"))));
		case "/manager/view-your-employees":
			final int vyeMgrUserId = (Integer) request.getSession(false).getAttribute("userId");
			return new HierarchyService().findEmployeesForSupervisor(new HierarchyService().findBySupervisor(new UserService().findById(vyeMgrUserId)));
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
			
		case "/deny":
			response.setStatus(401);
			LOG.debug("Requested action is not permitted");
			return "The requested action is not permitted";
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
		case "/employee/login":
		case "/manager/login":
			User user = new UserService().authenticate(request.getParameter("username"), request.getParameter("password"));
			if(user == null || (resource.equals("/manager/login") && user.getRole().getRoleId() != 1)) {
				response.setStatus(400);
				LOG.debug("Invalid Credentials");
				return "Invalid Credentials";
			} else if(request.getSession(false) != null) {
				response.setStatus(400);
				LOG.debug("Client already has a current session");
				return "You already have a current session. Logout before continuing.";
			} else {
				HttpSession session = request.getSession();
				session.setAttribute("userId", user.getUserId());
				session.setAttribute("username", user.getUsername());
				session.setAttribute("email", user.getEmail());
				session.setAttribute("role", user.getRole().getRole());
				LOG.debug("Login Successful");
				return user.getRole().getRole();
			}
		case "/manager/update-approval":
			ApprovalOutcome outcome = new SupervisorApprovalService().resolveApproval(
					Integer.parseInt(request.getParameter("requestId")),
					(Integer) request.getSession(false).getAttribute("userId"),
					Boolean.parseBoolean(request.getParameter("decision")));
			switch(outcome) {
			case APPROVED:
				return "Reimbursement Request Approved";
			case DENIED:
				return "Reimbursement Request Denied";
			case ESCALATED:
				return "Request will now be determined by your supervisor";
			default:
				response.setStatus(400);
				return "Other managers must make their decisions first";
			}
		case "/manager/select-employee":
			return new RequestService().findByRequester(new UserService().findById(Integer.parseInt(request.getParameter("employeeId"))));
		case "/employee/update-user-information":
		case "/manager/update-user-information":
			ProfileUpdateForm profileUpdateForm = new ProfileUpdateForm(
					request.getParameter("oldusername"), request.getParameter("newusername"), request.getParameter("confirmusername"),
					request.getParameter("oldpassword"), request.getParameter("newpassword"), request.getParameter("confirmpassword"),
					request.getParameter("oldemail"), request.getParameter("newemail"), request.getParameter("confirmemail"));
			switch(new UserService().updateProfile((Integer) request.getSession(false).getAttribute("userId"), profileUpdateForm)) {
			case UPDATED:
				LOG.debug("Update successful");
				return "Update was successful";
			case NO_ENTRIES:
				response.setStatus(400);
				return "Make some entries before clicking submit";
			default:
				response.setStatus(400);
				LOG.debug("Invalid entries");
				return "Invalid entries. Please try again.";
			}
		case "/employee/submit-request":
		case "/manager/submit-request":
			Request newRequest = new Request(
					100,
					Double.parseDouble(request.getParameter("amount")),
					Date.valueOf(request.getParameter("eventYear")+"-"+request.getParameter("eventMonth")+"-"+request.getParameter("eventDay")),
					new EventLocationService().findByStreetNumberNamePostalCode(Integer.parseInt(request.getParameter("streetNumber")), request.getParameter("streetName").toUpperCase(), new CityStatePostalService().findByPostal(Integer.parseInt(request.getParameter("zipCode")))),
					request.getParameter("eventTitle"),
					new UserService().findById((Integer) request.getSession(false).getAttribute("userId")),
					new RequestStatusService().findById(2));
			Request submittedRequest;
			try {
				submittedRequest = new RequestService().submitRequest(newRequest);
			} catch(NonUniqueResultException e) {
				response.setStatus(400);
				LOG.debug("Invalid entries");
				return "Invalid entries. Please try again.";
			}
			request.getSession(false).setAttribute("retrievedRequest", submittedRequest);
			return "Request submitted successfully";
		case "/upload-file":
			LOG.debug("Upload successful");
			new AmazonS3ObjectService().addObject(new AmazonS3Object(100, (String) request.getAttribute("fileName"), (Request) request.getSession(false).getAttribute("retrievedRequest")));
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
