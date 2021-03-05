package com.revature.service;

import java.sql.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.model.Request;
import com.revature.model.RequestConfirmation;
import com.revature.model.RequestStatus;
import com.revature.model.Role;
import com.revature.model.User;
import com.revature.repository.RequestConfirmationRepositoryImpl;

public class RequestConfirmationServiceTest {
	
	@InjectMocks private static RequestConfirmationService requestConfirmationService;
	@Mock private static RequestConfirmationRepositoryImpl requestConfirmationRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		requestConfirmationService = new RequestConfirmationService();
	}
	
	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testFindById() {
		Request mockRequest = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Rumbling", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		RequestConfirmation mockConfirmation = new RequestConfirmation(1, Date.valueOf("2000-01-01"), mockRequest);
		Mockito.when(requestConfirmationRepository.findById(1)).thenReturn(mockConfirmation);
		RequestConfirmation retrievedConfirmation = requestConfirmationService.findById(1);
		Assert.assertEquals("Rumbling", retrievedConfirmation.getRequest().getRequestedEvent());
	}
	
	@Test
	public void testFindByDateAndRequest() {
		Request mockRequest = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Rumbling", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		RequestConfirmation mockConfirmation = new RequestConfirmation(1, Date.valueOf("2000-01-01"), mockRequest);
		Mockito.when(requestConfirmationRepository.findByDateAndRequest("2000-01-01", mockRequest)).thenReturn(mockConfirmation);
		RequestConfirmation retrievedConfirmation = requestConfirmationService.findByDateAndRequest("2000-01-01", mockRequest);
		Assert.assertEquals("Rumbling", retrievedConfirmation.getRequest().getRequestedEvent());
	}

}
