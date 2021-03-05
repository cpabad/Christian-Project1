package com.revature.repository;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.ReimbursementStatus;

public class ReimbursementStatusRepositoryTest {
	
	private static ReimbursementStatusRepositoryImpl rsri;
	
	@BeforeClass
	public static void setupBeforeClass() {
		rsri = new ReimbursementStatusRepositoryImpl();
	}
	
	@Test
	public void testFindById() {
		ReimbursementStatus retrievedStatus = rsri.findById(2);
		Assert.assertEquals("Pending", retrievedStatus.getStatus());
	}
	
	@Test
	public void testFindByStatus() {
		ReimbursementStatus retrievedStatus = rsri.findByStatus("Pending");
		Assert.assertEquals("Pending", retrievedStatus.getStatus());
	}

}
