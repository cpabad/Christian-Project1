package com.revature.service;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

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
import com.revature.model.RequestStatus;
import com.revature.model.Role;
import com.revature.model.User;
import com.revature.repository.RequestRepositoryImpl;

public class RequestServiceTest {
	
	@InjectMocks private static RequestService requestService;
	@Mock private static RequestRepositoryImpl requestRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		requestService = new RequestService();
	}
	
	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testFindById() {
		Request mockRequest = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Rumbling", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Mockito.when(requestRepository.findById(1)).thenReturn(mockRequest);
		Request retrievedRequest = requestService.findById(1);
		Assert.assertEquals("Rumbling", retrievedRequest.getRequestedEvent());
	}
	
	@Test
	public void testFindByDateLocationUser() {
		Request mockRequest = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Rumbling", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Mockito.when(requestRepository.findByDateLocationRequester("2000-01-01", new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")))).thenReturn(mockRequest);
		Request retrievedRequest = requestService.findByDateLocationRequester("2000-01-01", new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")));
		Assert.assertEquals("Rumbling", retrievedRequest.getRequestedEvent());
	}
	
	@Test
	public void testFindAll() {
		Request mockRequest1 = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Rumbling", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Request mockRequest2 = new Request(2, 1000.00, Date.valueOf("2000-01-02"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Walking", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Request mockRequest3 = new Request(3, 10000.00, Date.valueOf("2021-02-14"), new EventLocation(1, 1, "E-Rantel Markets", new CityStatePostal(2, "E-Rantel", "Re-Estize Kingdom")), "Being Momon", new User(2, "Momonga", "ainzooalgown", "Suzuki", "Satoru", "ss@email.com", new Role(2, "Guild Master")), new RequestStatus(2, "Approved because all hail Momonga"));
		Mockito.when(requestRepository.findAll()).thenReturn(Arrays.asList(mockRequest1, mockRequest2, mockRequest3));
		List<Request> retrievedRequests = requestService.findAll();
		Assert.assertEquals("Rumbling", retrievedRequests.get(0).getRequestedEvent());
		Assert.assertEquals("Walking", retrievedRequests.get(1).getRequestedEvent());
		Assert.assertEquals("Being Momon", retrievedRequests.get(2).getRequestedEvent());
	}
	
	@Test
	public void testFindByRequester() {
		Request mockRequest1 = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Rumbling", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Request mockRequest2 = new Request(2, 1000.00, Date.valueOf("2000-01-02"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Walking", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Mockito.when(requestRepository.findByRequester(new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")))).thenReturn(Arrays.asList(mockRequest1, mockRequest2));
		List<Request> retrievedRequests = requestService.findByRequester(new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")));
		Assert.assertEquals("Rumbling", retrievedRequests.get(0).getRequestedEvent());
		Assert.assertEquals("Walking", retrievedRequests.get(1).getRequestedEvent());
	}

}
