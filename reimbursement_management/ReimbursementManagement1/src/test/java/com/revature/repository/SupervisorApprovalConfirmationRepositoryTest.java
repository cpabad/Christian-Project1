package com.revature.repository;

import java.sql.Date;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.model.Hierarchy;
import com.revature.model.Request;
import com.revature.model.RequestStatus;
import com.revature.model.Role;
import com.revature.model.SupervisorApproval;
import com.revature.model.SupervisorApprovalConfirmation;
import com.revature.model.SupervisorApprovalStatus;
import com.revature.model.User;

public class SupervisorApprovalConfirmationRepositoryTest {
	
	private static SupervisorApprovalConfirmationRepositoryImpl sacri;
	
	@BeforeClass
	public static void setupBeforeClass() {
		sacri = new SupervisorApprovalConfirmationRepositoryImpl();
	}
	
	@Test
	public void testFindById() {
		SupervisorApprovalConfirmation retrievedConfirmation = sacri.findById(1);
		Assert.assertEquals("Topology Crash Course", retrievedConfirmation.getApproval().getRequest().getRequestedEvent());
	}
	
	@Test
	public void testFindByDateAndApproval() {
		User mockUser1 = new User(1, "admin", "adminPassword", "A", "A", "a@email.com", new Role(1, "Supervisor"));
		User mockUser2 = new User(3, "employee2", "employeePassword", "C", "C", "c@email.com", new Role(1, "Supervisor"));
		Request mockRequest1 = new Request(4, 1.99, Date.valueOf("2021-02-01"), new EventLocation(2, 200, "FREEWAY RD", new CityStatePostal(1, "EVERY", "WHERE")), "Topology Crash Course", mockUser2, new RequestStatus(1, "Resolved"));
		Hierarchy mockRelationship1 = new Hierarchy(1, mockUser1, mockUser2);
		SupervisorApproval mockApproval1 = new SupervisorApproval(5, Date.valueOf("2021-02-06"), mockRequest1, mockRelationship1, new SupervisorApprovalStatus(1, "Resolved"), true);
		SupervisorApprovalConfirmation retrievedConfirmation = sacri.findByDateAndApproval("2021-02-06", mockApproval1);
		Assert.assertEquals("Topology Crash Course", retrievedConfirmation.getApproval().getRequest().getRequestedEvent());
	}

}
