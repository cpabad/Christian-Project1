package com.revature.repository;

import java.sql.Date;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.model.Reimbursement;
import com.revature.model.Request;
import com.revature.model.RequestStatus;
import com.revature.model.Role;
import com.revature.model.User;

public class ReimbursementRepositoryTest {
	
	private static ReimbursementRepositoryImpl reimbursementRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		reimbursementRepository = new ReimbursementRepositoryImpl();
	}
	
	@Test
	public void testFindById() {
		Reimbursement retrievedAward = reimbursementRepository.findById(1);
		Assert.assertEquals("Anime Convention", retrievedAward.getFinalApproval().getRequest().getRequestedEvent());
	}
	
	@Test
	public void testFindByRequest() {
		Request request = new Request(1, 100.01, Date.valueOf("2021-01-14"), new EventLocation(1, 100, "MAIN AVE", new CityStatePostal(1, "EVERY", "WHERE")), "Anime Convention", new User(2, "employee1", "employeePassword", "B", "B", "b@email.com", new Role(1, "Supervisor")), new RequestStatus(2, "Pending"));
		Reimbursement retrievedAward = reimbursementRepository.findByRequest(request);
		Assert.assertEquals("Anime Convention", retrievedAward.getFinalApproval().getRequest().getRequestedEvent());
	}
	
	@Test
	public void testFindByEventDateAndRequester() {
		User employeeUser1 = new User(2, "employee1", "employeePassword", "B", "B", "b@email.com", new Role(1, "Supervisor")); 
		Reimbursement retrievedAward = reimbursementRepository.findByEventDateAndRequester("2021-01-14", employeeUser1);
		Assert.assertEquals("Anime Convention", retrievedAward.getFinalApproval().getRequest().getRequestedEvent());
	}
	

}
