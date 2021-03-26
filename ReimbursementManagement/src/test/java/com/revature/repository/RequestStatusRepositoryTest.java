package com.revature.repository;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.RequestStatus;

public class RequestStatusRepositoryTest {
	
	private static RequestStatusRepositoryImpl requestStatusRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		requestStatusRepository = new RequestStatusRepositoryImpl();
	}
	
	@Test
	public void testFindById() {
		RequestStatus retrievedStatus = requestStatusRepository.findById(2);
		Assert.assertEquals("Pending", retrievedStatus.getStatus());
	}
	
	@Test
	public void testFindByStatus() {
		RequestStatus retrievedStatus = requestStatusRepository.findByStatus("Pending");
		Assert.assertEquals("Pending", retrievedStatus.getStatus());
	}
	
	

}
