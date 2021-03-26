package com.revature.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.ObjectNotFoundException;

import com.revature.model.AmazonS3Object;
import com.revature.model.Hierarchy;
import com.revature.model.Reimbursement;
import com.revature.model.Request;
import com.revature.model.SupervisorApproval;
import com.revature.model.User;
import com.revature.service.AmazonS3ObjectService;
//import com.revature.service.AmazonS3ObjectService;
import com.revature.service.CityStatePostalService;
import com.revature.service.EventLocationService;
import com.revature.service.HierarchyService;
import com.revature.service.ReimbursementService;
import com.revature.service.ReimbursementStatusService;
import com.revature.service.RequestService;
import com.revature.service.RequestStatusService;
import com.revature.service.SupervisorApprovalService;
import com.revature.service.SupervisorApprovalStatusService;
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
			final String loginEmpUsername = request.getParameter("username");
			final String loginEmpPassword = request.getParameter("password");
			User employee = null;
			try {
				if(loginEmpUsername.contains("@") && loginEmpUsername.contains(".com") || loginEmpUsername.contains(".net")|| loginEmpUsername.contains(".us")|| loginEmpUsername.contains(".edu")|| loginEmpUsername.contains(".co")) {
					employee = new UserService().findByEmail(loginEmpUsername);
				} else {
					employee = new UserService().findByUsername(loginEmpUsername);
				}
				if(request.getSession(false) != null) {
					response.setStatus(400);
					LOG.debug("Client already has a current session");
					return "You already have a current session. Logout before continuing.";
				} else if(employee != null && loginEmpPassword.isBlank() == false) {
					if(loginEmpPassword.equals(employee.getPassword())) {
						HttpSession session = request.getSession();
						session.setAttribute("userId", employee.getUserId());
						session.setAttribute("username", employee.getUsername());
						session.setAttribute("email", employee.getEmail());
						session.setAttribute("role", employee.getRole().getRole());
						LOG.debug("Login Successful");
						return employee.getRole().getRole();
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
			} catch (NoResultException e) {
				response.setStatus(400);
				LOG.debug("Invalid Credentials");
				return "Invalid Credentials";
			}
		case "/manager/login":
			final String loginMgrUsername = request.getParameter("username");
			final String loginMgrPassword = request.getParameter("password");
			User manager = null;
			try {
				if(loginMgrUsername.contains("@") && loginMgrUsername.contains(".com") || loginMgrUsername.contains(".net")|| loginMgrUsername.contains(".us")|| loginMgrUsername.contains(".edu")|| loginMgrUsername.contains(".co")) {
					manager = new UserService().findByEmail(loginMgrUsername);
				} else {
					manager = new UserService().findByUsername(loginMgrUsername);
				}
				if(request.getSession(false) != null) {
					response.setStatus(400);
					LOG.debug("Client already has a current session");
					return "You already have a current session. Logout before continuing.";
				
				} else if(manager != null && manager.getRole().getRoleId() == 1 && loginMgrPassword.isBlank() == false) {
					if(loginMgrPassword.equals(manager.getPassword())) {
						HttpSession session = request.getSession();
						session.setAttribute("userId", manager.getUserId());
						session.setAttribute("username", manager.getUsername());
						session.setAttribute("email", manager.getEmail());
						session.setAttribute("role", manager.getRole().getRole());
						LOG.debug("Login Successful");
						return manager.getRole().getRole();
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
			} catch (NoResultException e) {
				response.setStatus(400);
				LOG.debug("Invalid Credentials");
				return "Invalid Credentials";
			}
		case "/manager/update-approval":
			Request requestWithDecision = new RequestService().findById(Integer.parseInt(request.getParameter("requestId")));
			SupervisorApproval updatedApproval = new SupervisorApprovalService().findByRequestRequesterManager(requestWithDecision, requestWithDecision.getRequester(), new UserService().findById((Integer) request.getSession(false).getAttribute("userId")));
			updatedApproval.setApproval(Boolean.parseBoolean(request.getParameter("decision")));
			updatedApproval.setSupervisorApprovalStatus(new SupervisorApprovalStatusService().findById(1));
			List<SupervisorApproval> pendingApprovals1 = new SupervisorApprovalService().findByRequestAndRequester(requestWithDecision, requestWithDecision.getRequester());
			List<User> supervisorsNeededForApproval = new ArrayList<>();
			int lastPendingApprovalCounter = 0;
			int pendingApprovalCounter = 0;
			for(SupervisorApproval a : pendingApprovals1) {
				if(new HierarchyService().findByEmployee(a.getHierarchy().getSupervisorUser()).isEmpty() == false && a.getSupervisorApprovalStatus().getStatusId() == 2) {
					supervisorsNeededForApproval.add(a.getHierarchy().getSupervisorUser());
					lastPendingApprovalCounter++;
				} else if(new HierarchyService().findEmployeesForSupervisor(new HierarchyService().findBySupervisor(new UserService().findById((Integer) request.getSession(false).getAttribute("userId")))).contains(a.getHierarchy().getSupervisorUser()) && a.getSupervisorApprovalStatus().getStatusId() == 2) {
					pendingApprovalCounter++;
				}
			}
			if(lastPendingApprovalCounter == 0 && new HierarchyService().findByEmployee(new UserService().findById((Integer) request.getSession(false).getAttribute("userId"))).isEmpty() && updatedApproval.isApproval()) {
				requestWithDecision.setRequestStatus(new RequestStatusService().findById(1));
				Reimbursement reimbursementWithDecision = new ReimbursementService().findByRequest(requestWithDecision);
				reimbursementWithDecision.setReimbursementStatus(new ReimbursementStatusService().findById(1));
				new ReimbursementService().updateReimbursement(reimbursementWithDecision);
				new SupervisorApprovalService().updateApproval(updatedApproval);
				new RequestService().updateRequest(requestWithDecision);
				return "Reimbursement Request Approved";
			} else if(pendingApprovalCounter == 0 && updatedApproval.isApproval()) {
				new SupervisorApprovalService().updateApproval(updatedApproval);
				return "Request will now be determined by your supervisor";
			} else if(pendingApprovalCounter == 0) {
				requestWithDecision.setRequestStatus(new RequestStatusService().findById(1));
				Reimbursement reimbursementWithDecision = new ReimbursementService().findByRequest(requestWithDecision);
				reimbursementWithDecision.setReimbursementStatus(new ReimbursementStatusService().findById(1));
				new ReimbursementService().updateReimbursement(reimbursementWithDecision);
				new SupervisorApprovalService().updateApproval(updatedApproval);
				new RequestService().updateRequest(requestWithDecision);
				return "Reimbursement Request Denied";
			} else {
				response.setStatus(400);
				return "Other managers must make their decisions first";
			}
			
		case "/manager/select-employee":
			return new RequestService().findByRequester(new UserService().findById(Integer.parseInt(request.getParameter("employeeId"))));
		case "/employee/update-user-information":
			User updatedUser = new UserService().findById((Integer) request.getSession(false).getAttribute("userId"));
			if(request.getParameter("confirmusername").isBlank() == false) {
				if(request.getParameter("oldusername").isBlank() == false && updatedUser.getUsername().equals(request.getParameter("oldusername"))) {
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
				} else {
					response.setStatus(400);
					return "Invalid entries. Please try again.";
				}
			}
			if(request.getParameter("confirmpassword").isBlank() == false) {
				if(request.getParameter("oldpassword") != null && updatedUser.getPassword().equals(request.getParameter("oldpassword"))) {
					updatedUser.setPassword(request.getParameter("newpassword"));
				} else {
					response.setStatus(400);
					LOG.debug("Invalid entries");
					return "Invalid entries. Please try again.";
				}
			}
			if(request.getParameter("confirmemail").isBlank() == false) {
				if(request.getParameter("oldemail") != null && updatedUser.getEmail().equals(request.getParameter("oldemail"))) {
					if(request.getParameter("newemail").isBlank() == false) {
						try {
							new UserService().findByEmail(request.getParameter("newemail"));
						} catch (NonUniqueObjectException e) {
							response.setStatus(400);
							LOG.debug("Invalid entries");
							return "Invalid entries. Please try again.";
						} catch (ObjectNotFoundException e) {
							updatedUser.setEmail(request.getParameter("newemail"));
						} catch (NoResultException e) {
							updatedUser.setEmail(request.getParameter("newemail"));
						}
					}
				} else {
					response.setStatus(400);
					LOG.debug("Invalid entries");
					return "Invalid entries. Please try again.";
				}
			}
			if(request.getParameter("confirmusername").isBlank() && request.getParameter("confirmpassword").isBlank() && request.getParameter("confirmemail").isBlank()) {
				response.setStatus(400);
				return "Make some entries before clicking submit";
			}
			new UserService().update(updatedUser);
			LOG.debug("Update successful");
			return "Update was successful";
		case "/employee/submit-request":			
			Request submitRequest = new Request(
					100,
					Double.parseDouble(request.getParameter("amount")),
					Date.valueOf(request.getParameter("eventYear")+"-"+request.getParameter("eventMonth")+"-"+request.getParameter("eventDay")), 
					new EventLocationService().findByStreetNumberNamePostalCode(Integer.parseInt(request.getParameter("streetNumber")), request.getParameter("streetName").toUpperCase(), new CityStatePostalService().findByPostal(Integer.parseInt(request.getParameter("zipCode")))), 
					request.getParameter("eventTitle"), 
					new UserService().findById((Integer) request.getSession(false).getAttribute("userId")), 
					new RequestStatusService().findById(2));
			try {
				new RequestService().makeNewRequest(submitRequest);
			} catch(NonUniqueResultException e) {
				response.setStatus(400);
				LOG.debug("Invalid entries");
				return "Invalid entries. Please try again.";
			}
			
			Request retrievedRequest = new RequestService().findByDateLocationRequester(submitRequest.getEventDate().toString(), submitRequest.getEventLocation(), submitRequest.getRequester());
			List<Hierarchy> supervisorsForEmployee = new HierarchyService().findByEmployee(new UserService().findById((Integer) request.getSession(false).getAttribute("userId")));
			for(Hierarchy h : supervisorsForEmployee) {
				SupervisorApproval newApproval = new SupervisorApproval(100, Date.valueOf("2000-01-01"), retrievedRequest, h, new SupervisorApprovalStatusService().findById(2), false); 
				new SupervisorApprovalService().addApproval(newApproval);
				if(new HierarchyService().findByEmployee(h.getSupervisorUser()).isEmpty()) {
					Reimbursement reimbursement = new Reimbursement(100, retrievedRequest.getAmount(), Date.valueOf("2000-01-01"), newApproval, new ReimbursementStatusService().findById(2));
					new ReimbursementService().addReimbursement(reimbursement);
				}
			}
			request.getSession(false).setAttribute("retrievedRequest", retrievedRequest);
			return "Request submitted successfully";
		case "/manager/submit-request":			
			Request submitMgrRequest = new Request(
					100,
					Double.parseDouble(request.getParameter("amount")),
					Date.valueOf(request.getParameter("eventYear")+"-"+request.getParameter("eventMonth")+"-"+request.getParameter("eventDay")), 
					new EventLocationService().findByStreetNumberNamePostalCode(Integer.parseInt(request.getParameter("streetNumber")), request.getParameter("streetName").toUpperCase(), new CityStatePostalService().findByPostal(Integer.parseInt(request.getParameter("zipCode")))), 
					request.getParameter("eventTitle"), 
					new UserService().findById((Integer) request.getSession(false).getAttribute("userId")), 
					new RequestStatusService().findById(2));
			try {
				new RequestService().makeNewRequest(submitMgrRequest);
			} catch(NonUniqueResultException e) {
				response.setStatus(400);
				LOG.debug("Invalid entries");
				return "Invalid entries. Please try again.";
			}
			
			Request retrievedMgrRequests = new RequestService().findByDateLocationRequester(submitMgrRequest.getEventDate().toString(), submitMgrRequest.getEventLocation(), submitMgrRequest.getRequester());
			List<Hierarchy> supervisorForMgr = new HierarchyService().findByEmployee(new UserService().findById((Integer) request.getSession(false).getAttribute("userId")));
			for(Hierarchy h : supervisorForMgr) {
				SupervisorApproval newApproval = new SupervisorApproval(100, Date.valueOf("2000-01-01"), retrievedMgrRequests, h, new SupervisorApprovalStatusService().findById(2), false); 
				new SupervisorApprovalService().addApproval(newApproval);
				if(new HierarchyService().findByEmployee(h.getSupervisorUser()).isEmpty()) {
					Reimbursement reimbursement = new Reimbursement(100, retrievedMgrRequests.getAmount(), Date.valueOf("2000-01-01"), newApproval, new ReimbursementStatusService().findById(2));
					new ReimbursementService().addReimbursement(reimbursement);
				}
			}
			request.getSession(false).setAttribute("retrievedMgrRequests", retrievedMgrRequests);
			return "Request submitted successfully";
		case "/upload-file":
			LOG.debug("Upload successful");
			new AmazonS3ObjectService().addObject(new AmazonS3Object(100, (String) request.getAttribute("fileName"), (Request) request.getAttribute("retrievedMgrRequests")));
			return "The file was uploaded successfully! Please exit this page to return to the homepage.";
		case "/manager/update-user-information":
			User updatedMgr = new UserService().findById((Integer) request.getSession(false).getAttribute("userId"));
			if(request.getParameter("confirmusername").isBlank() == false) {
				if(request.getParameter("oldusername").isBlank() == false && updatedMgr.getUsername().equals(request.getParameter("oldusername"))) {
					try {
						new UserService().findByUsername(request.getParameter("newusername"));
					} catch (NonUniqueObjectException e) {
						response.setStatus(400);
						LOG.debug("Invalid entries");
						return "Invalid entries. Please try again.";
					} catch (ObjectNotFoundException e) {
						updatedMgr.setUsername(request.getParameter("newusername"));
					} catch (NoResultException e) {
						updatedMgr.setUsername(request.getParameter("newusername"));
					}
				} else {
					response.setStatus(400);
					return "Invalid entries. Please try again.";
				}
			}
			if(request.getParameter("confirmpassword").isBlank() == false) {
				if(request.getParameter("oldpassword") != null && updatedMgr.getPassword().equals(request.getParameter("oldpassword"))) {
					updatedMgr.setPassword(request.getParameter("newpassword"));
				} else {
					response.setStatus(400);
					LOG.debug("Invalid entries");
					return "Invalid entries. Please try again.";
				}
			}
			if(request.getParameter("confirmemail").isBlank() == false) {
				if(request.getParameter("oldemail") != null && updatedMgr.getEmail().equals(request.getParameter("oldemail"))) {
					if(request.getParameter("newemail").isBlank() == false) {
						try {
							new UserService().findByEmail(request.getParameter("newemail"));
						} catch (NonUniqueObjectException e) {
							response.setStatus(400);
							LOG.debug("Invalid entries");
							return "Invalid entries. Please try again.";
						} catch (ObjectNotFoundException e) {
							updatedMgr.setEmail(request.getParameter("newemail"));
						} catch (NoResultException e) {
							updatedMgr.setEmail(request.getParameter("newemail"));
						}
					}
				} else {
					response.setStatus(400);
					LOG.debug("Invalid entries");
					return "Invalid entries. Please try again.";
				}
			}
			if(request.getParameter("confirmusername").isBlank() && request.getParameter("confirmpassword").isBlank() && request.getParameter("confirmemail").isBlank()) {
				response.setStatus(400);
				return "Make some entries before clicking submit";
			}
			new UserService().update(updatedMgr);
			LOG.debug("Update successful");
			return "Update was successful";
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
