package com.revature.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.model.Request;
import com.revature.model.Role;
import com.revature.model.User;

public class RequestRepositoryTest {
	
	private static RequestRepositoryImpl requestRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		requestRepository = new RequestRepositoryImpl();
	}
	
	@Test
	public void testFindById() {
		Request retrievedRequest = requestRepository.findById(1);
		Assert.assertEquals("B", retrievedRequest.getRequester().getFirstName());
	}
	
	@Test
	public void testFindByDateLocationUser() {
		Request retrievedRequest = requestRepository.findByDateLocationRequester(
				"2021-01-14", 
				new EventLocation(1, 100, "MAIN AVE", new CityStatePostal(1, "EVERY", "WHERE")), 
				new User(4, "employee3", "employeePassword", "D", "D", "d@email.com", new Role(2, "Employee")));
		Assert.assertEquals("D", retrievedRequest.getRequester().getFirstName());
	}
	
	@Test
	public void testFindAll() {
		List<Request> retrievedRequests = requestRepository.findAll();
		Assert.assertEquals("Anime Convention", retrievedRequests.get(0).getRequestedEvent());
		Assert.assertEquals("Anime Convention", retrievedRequests.get(1).getRequestedEvent());
		Assert.assertEquals("Topology Crash Course", retrievedRequests.get(3).getRequestedEvent());
	}
	
	@Test
	public void testFindByRequester() {
		List<Request> retrievedRequests = requestRepository.findByRequester(new User(2, "employee1", "employeePassword", "B", "B", "b@email.com", new Role(1, "Supervisor")));
		Assert.assertEquals("Anime Convention", retrievedRequests.get(0).getRequestedEvent());
		Assert.assertEquals("Magic Tricks Boot Camp", retrievedRequests.get(1).getRequestedEvent());
		
	}
	
	@Test
	public void testFindByRequesterAndPendingStatus() {
		List<Request> retrievedRequests = requestRepository.findByRequester(new User(2, "employee1", "employeePassword", "B", "B", "b@email.com", new Role(1, "Supervisor")));
		Assert.assertEquals("Anime Convention", retrievedRequests.get(0).getRequestedEvent());
		Assert.assertEquals("Magic Tricks Boot Camp", retrievedRequests.get(1).getRequestedEvent());
	}

}
