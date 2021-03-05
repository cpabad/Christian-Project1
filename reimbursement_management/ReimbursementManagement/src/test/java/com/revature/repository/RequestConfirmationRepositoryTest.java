package com.revature.repository;

import java.sql.Date;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.model.Request;
import com.revature.model.RequestConfirmation;
import com.revature.model.RequestStatus;
import com.revature.model.Role;
import com.revature.model.User;

public class RequestConfirmationRepositoryTest {
	
	private static RequestConfirmationRepositoryImpl requestConfirmationRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		requestConfirmationRepository = new RequestConfirmationRepositoryImpl();
	}
	
	@Test
	public void testFindById() {
		RequestConfirmation retrievedConfirmation = requestConfirmationRepository.findById(1);
		Assert.assertEquals("Anime Convention", retrievedConfirmation.getRequest().getRequestedEvent());
	}
	
	@Test
	public void testFindByDateAndRequest() {
		Request request = new Request(1, 100.01, Date.valueOf("2021-01-14"), new EventLocation(1, 100, "MAIN AVE", new CityStatePostal(1, "EVERY", "WHERE")), "Anime Convention", new User(2, "employee1", "employeePassword", "B", "B", "b@email.com", new Role(1, "Supervisor")), new RequestStatus(2, "Pending"));
		RequestConfirmation retrievedConfirmation = requestConfirmationRepository.findByDateAndRequest("2021-01-14", request);
		Assert.assertEquals("Anime Convention", retrievedConfirmation.getRequest().getRequestedEvent());
	}

}
