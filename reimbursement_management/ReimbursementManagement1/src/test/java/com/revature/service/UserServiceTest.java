package com.revature.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.revature.model.Role;
import com.revature.model.User;
import com.revature.repository.UserRepositoryImpl;

public class UserServiceTest {
	
	@InjectMocks private static UserService userService;
	@Mock private static UserRepositoryImpl userRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		userService = new UserService();
	}
	
	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testFindById() {
		Mockito.when(userRepository.findById(1)).thenReturn(new User(1, "Major", "GhostInTheShell", "Matoko", "Kusanagi", "mk@section9.com", new Role(1, "Being a badass")));
		User retrievedUser = userService.findById(1);
		Assert.assertEquals("Being a badass", retrievedUser.getRole().getRole());
	}
	
	@Test
	public void testFindByUsername() {
		Mockito.when(userRepository.findByUsername("Major")).thenReturn(new User(1, "Major", "GhostInTheShell", "Matoko", "Kusanagi", "mk@section9.com", new Role(1, "Being a badass")));
		User retrievedUser = userService.findByUsername("Major");
		Assert.assertEquals("Being a badass", retrievedUser.getRole().getRole());
	}
	
	@Test
	public void testFindByEmail() {
		Mockito.when(userRepository.findByEmail("mk@section9.com")).thenReturn(new User(1, "Major", "GhostInTheShell", "Matoko", "Kusanagi", "mk@section9.com", new Role(1, "Being a badass")));
		User retrievedUser = userService.findByEmail("mk@section9.com");
		Assert.assertEquals("Being a badass", retrievedUser.getRole().getRole());
	}

}
