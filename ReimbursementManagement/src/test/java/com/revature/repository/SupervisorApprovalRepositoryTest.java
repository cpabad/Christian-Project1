package com.revature.repository;

import java.sql.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.model.Request;
import com.revature.model.RequestStatus;
import com.revature.model.Role;
import com.revature.model.SupervisorApproval;
import com.revature.model.User;

public class SupervisorApprovalRepositoryTest {
	
	private static SupervisorApprovalRepositoryImpl sari;
	
	@BeforeClass
	public static void setupBeforeClass() {
		sari = new SupervisorApprovalRepositoryImpl();
	}
	
	@Test
	public void testFindAll() {
		List<SupervisorApproval> retrievedApprovals = sari.findAll();
		Assert.assertEquals(true, retrievedApprovals.get(4).isApproval());
	}
	
	@Test
	public void testFindById() {
		SupervisorApproval retrievedApproval = sari.findById(1);
		Assert.assertEquals("Anime Convention", retrievedApproval.getRequest().getRequestedEvent());
	}
	
//	@Test
//	public void testFindByRequestAndRequester() {
//		Request request = new Request(4, 1.99, Date.valueOf("2021-02-01"), new EventLocation(2, 200, "FREEWAY RD", new CityStatePostal(1, "EVERY", "WHERE")), "Topology Crash Course", new User(3, "employee2", "employeePassword", "C", "C", "c@email.com", new Role(1, "Supervisor")), new RequestStatus(1, "Resolved"));
//		User requester = request.getRequester();
//		SupervisorApproval retrievedApproval = sari.findByRequestAndRequester(request, requester);
//		Assert.assertEquals("Topology Crash Course", retrievedApproval.getRequest().getRequestedEvent());
//	}

}
