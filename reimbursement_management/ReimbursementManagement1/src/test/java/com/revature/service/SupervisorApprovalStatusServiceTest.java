package com.revature.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.revature.model.SupervisorApprovalStatus;
import com.revature.repository.SupervisorApprovalStatusRepositoryImpl;

public class SupervisorApprovalStatusServiceTest {
	
	@InjectMocks private static SupervisorApprovalStatusService sass;
	@Mock private static SupervisorApprovalStatusRepositoryImpl sasri;
	
	@BeforeClass
	public static void setupBeforeClass() {
		sass = new SupervisorApprovalStatusService();
	}
	
	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testFindById() {
		SupervisorApprovalStatus mockStatus = new SupervisorApprovalStatus(1, "Exploding");
		Mockito.when(sasri.findById(1)).thenReturn(mockStatus);
		SupervisorApprovalStatus retrievedStatus = sass.findById(1);
		Assert.assertEquals("Exploding", retrievedStatus.getStatus());		
	}
	
	@Test
	public void testFindByStatus() {
		SupervisorApprovalStatus mockStatus = new SupervisorApprovalStatus(1, "Exploding");
		Mockito.when(sasri.findByStatus("Exploding")).thenReturn(mockStatus);
		SupervisorApprovalStatus retrievedStatus = sass.findByStatus("Exploding");
		Assert.assertEquals("Exploding", retrievedStatus.getStatus());
	}

}
