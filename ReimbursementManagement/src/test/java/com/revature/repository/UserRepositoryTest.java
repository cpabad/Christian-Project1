package com.revature.repository;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.revature.model.User;

public class UserRepositoryTest {
	
	@InjectMocks public static UserRepositoryImpl userRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		userRepository = new UserRepositoryImpl();
	}

	@Test
	public void testFindById() {
		User retrievedUser = userRepository.findById(2);
		Assert.assertEquals("Supervisor", retrievedUser.getRole().getRole());
	}
	
	@Test
	public void testFindByUsername() {
		User retrievedUser = userRepository.findByUsername("employee1");
		Assert.assertEquals("Supervisor", retrievedUser.getRole().getRole());
	}
	
	@Test
	public void testFindByEmail() {
		User retrievedUser = userRepository.findByEmail("b@email.com");
		Assert.assertEquals("Supervisor", retrievedUser.getRole().getRole());
	}

}
