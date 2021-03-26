package com.revature.repository;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.Role;

public class RoleRepositoryTest {
	
	private static RoleRepositoryImpl roleRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		roleRepository = new RoleRepositoryImpl();
	}
	
	@Test
	public void testFindById() {
		Role retrievedRole = roleRepository.findById(2);
		Assert.assertEquals("Employee", retrievedRole.getRole());
	}
	
	@Test
	public void testFindByRole() {
		Role retrievedRole = roleRepository.findByRole("Employee");
		Assert.assertEquals("Employee", retrievedRole.getRole());
	}

}
