package com.revature.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.revature.model.RequestStatus;
import com.revature.repository.RequestStatusRepositoryImpl;

public class RequestStatusServiceTest {
	
	@InjectMocks private static RequestStatusService requestStatusService;
	@Mock private static RequestStatusRepositoryImpl requestStatusRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		requestStatusService = new RequestStatusService();
	}
	
	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testFindById() {
		RequestStatus mockStatus = new RequestStatus(1, "Exploding");
		Mockito.when(requestStatusRepository.findById(1)).thenReturn(mockStatus);
		RequestStatus retrievedStatus = requestStatusService.findById(1);
		Assert.assertEquals("Exploding", retrievedStatus.getStatus());
	}
	
	@Test
	public void testFindByStatus() {
		RequestStatus mockStatus = new RequestStatus(1, "Exploding");
		Mockito.when(requestStatusRepository.findByStatus("Exploding")).thenReturn(mockStatus);
		RequestStatus retrievedStatus = requestStatusService.findByStatus("Exploding");
		Assert.assertEquals("Exploding", retrievedStatus.getStatus());
	}

}
