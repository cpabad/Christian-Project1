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
import com.revature.repository.RoleRepositoryImpl;

public class RoleServiceTest {
	
	@InjectMocks private static RoleService roleService;
	@Mock private static RoleRepositoryImpl roleRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		roleService = new RoleService();
	}
	
	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testFindById() {
		Mockito.when(roleRepository.findById(1)).thenReturn(new Role(1, "Watch Anime"));
		Assert.assertEquals("Watch Anime", roleService.findById(1).getRole());
	}
	
	@Test
	public void testFindByRole() {
		Mockito.when(roleRepository.findByRole("Watch Anime")).thenReturn(new Role(1, "Watch Anime"));
		Assert.assertEquals("Watch Anime", roleService.findByRole("Watch Anime").getRole());
	}

}
