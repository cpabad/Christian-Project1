package com.revature.repository;

import java.sql.Date;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.model.Hierarchy;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementConfirmation;
import com.revature.model.ReimbursementStatus;
import com.revature.model.Request;
import com.revature.model.RequestStatus;
import com.revature.model.Role;
import com.revature.model.SupervisorApproval;
import com.revature.model.SupervisorApprovalStatus;
import com.revature.model.User;

public class ReimbursementConfirmationRepositoryTest {
	
	private static ReimbursementConfirmationRepositoryImpl rcri;
	
	@BeforeClass
	public static void setupBeforeClass() {
		rcri = new ReimbursementConfirmationRepositoryImpl();
	}
	
	@Test
	public void testFindById() {
		ReimbursementConfirmation retrievedConfirmation = rcri.findById(1);
		Assert.assertEquals("Topology Crash Course", retrievedConfirmation.getReimbursementAwarded().getFinalApproval().getRequest().getRequestedEvent());
	}
	
	@Test
	public void testFindByEventDateAndRequester() {
		User requester = new User(3, "employee2", "employeePassword", "C", "C", "c@email.com", new Role(1, "Supervisor"));
		ReimbursementConfirmation retrievedConfirmation = rcri.findByEventDateAndRequester("2021-02-01", requester);
		Assert.assertEquals("Topology Crash Course", retrievedConfirmation.getReimbursementAwarded().getFinalApproval().getRequest().getRequestedEvent());
	}
	
	@Test
	public void testFindByConfirmationDateAndReimbursement() {
		User requester = new User(3, "employee2", "employeePassword", "C", "C", "c@email.com", new Role(1, "Supervisor"));
		User supervisor = new User(1, "admin", "adminPassword", "A", "A", "a@email.com", new Role(1, "Supervisor"));
		Hierarchy relationship = new Hierarchy(2, supervisor, requester);
		Request request = new Request(4, 1.99, Date.valueOf("2021-02-01"), new EventLocation(2, 200, "FREEWAY RD", new CityStatePostal(2, "EVERY", "WHERE")), "Topology Crash Course", requester, new RequestStatus(1, "Resolved"));
		SupervisorApproval approval = new SupervisorApproval(5, Date.valueOf("2021-02-06"), request, relationship, new SupervisorApprovalStatus(1, "Resolved"), true);
		Reimbursement reimbursement = new Reimbursement(4, 1.99, Date.valueOf("2021-02-10"), approval, new ReimbursementStatus(1, "Resolved"));
		ReimbursementConfirmation retrievedConfirmation = rcri.findByConfirmationDateAndReimbursement("2021-02-10", reimbursement);
		Assert.assertEquals("Topology Crash Course", retrievedConfirmation.getReimbursementAwarded().getFinalApproval().getRequest().getRequestedEvent());
	}

}
