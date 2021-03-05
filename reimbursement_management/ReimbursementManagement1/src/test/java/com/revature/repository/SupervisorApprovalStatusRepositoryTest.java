package com.revature.repository;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.SupervisorApprovalStatus;

public class SupervisorApprovalStatusRepositoryTest {
	
	private static SupervisorApprovalStatusRepositoryImpl sasri;
	
	@BeforeClass
	public static void setupBeforeClass() {
		sasri = new SupervisorApprovalStatusRepositoryImpl();
	}
	
	@Test
	public void testFindById() {
		SupervisorApprovalStatus retrievedStatus = sasri.findById(2);
		Assert.assertEquals("Pending", retrievedStatus.getStatus());
	}
	
	@Test
	public void testFindByStatus() {
		SupervisorApprovalStatus retrievedStatus = sasri.findByStatus("Pending");
		Assert.assertEquals("Pending", retrievedStatus.getStatus());
	}

}
