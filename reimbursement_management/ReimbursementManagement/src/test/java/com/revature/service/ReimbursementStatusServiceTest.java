package com.revature.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.revature.model.ReimbursementStatus;
import com.revature.repository.ReimbursementStatusRepositoryImpl;

public class ReimbursementStatusServiceTest {
	
	@InjectMocks private static ReimbursementStatusService statusService;
	@Mock private static ReimbursementStatusRepositoryImpl statusRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		statusService = new ReimbursementStatusService();
	}
	
	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testFindById() {
		ReimbursementStatus mockStatus1 = new ReimbursementStatus(1, "Exploding");
		Mockito.when(statusRepository.findById(1)).thenReturn(mockStatus1);
		ReimbursementStatus retrievedStatus = statusService.findById(1);
		Assert.assertEquals("Exploding", retrievedStatus.getStatus());
	}
	
	@Test
	public void testFindByStatus() {
		ReimbursementStatus mockStatus1 = new ReimbursementStatus(1, "Exploding");
		Mockito.when(statusRepository.findByStatus("Exploding")).thenReturn(mockStatus1);
		ReimbursementStatus retrievedStatus = statusService.findByStatus("Exploding");
		Assert.assertEquals("Exploding", retrievedStatus.getStatus());
	}

}
