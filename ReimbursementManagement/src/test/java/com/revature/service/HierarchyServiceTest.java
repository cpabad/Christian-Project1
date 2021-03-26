package com.revature.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.revature.model.Hierarchy;
import com.revature.model.Role;
import com.revature.model.User;
import com.revature.repository.HierarchyRepositoryImpl;

public class HierarchyServiceTest {
	
	@InjectMocks private static HierarchyService hierarchyService;
	@Mock private static HierarchyRepositoryImpl hierarchyRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		hierarchyService = new HierarchyService();
	}
	
	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testFindById() {
		User mockUser1 = new User(1, "Star Platinum", "Stand Proud", "Jotaro", "Kujo", "sp@email.com", new Role(1, "Being awesome"));
		User mockUser2 = new User(2, "Stone Ocean", "Not yet aired", "Jolene", "Kujo", "so@email.com", new Role(2, "Jailed in Florida"));
		Mockito.when(hierarchyRepository.findById(1)).thenReturn(new Hierarchy(1, mockUser1, mockUser2));
		Hierarchy retrievedHierarchy = hierarchyService.findById(1);
		Assert.assertEquals("Stone Ocean", retrievedHierarchy.getEmployeeUser().getUsername());
	}
	
	@Test
	public void testFindBySupervisorAndEmployee() {
		User mockUser1 = new User(1, "Star Platinum", "Stand Proud", "Jotaro", "Kujo", "sp@email.com", new Role(1, "Being awesome"));
		User mockUser2 = new User(2, "Stone Ocean", "Not yet aired", "Jolene", "Kujo", "so@email.com", new Role(2, "Jailed in Florida"));
		Mockito.when(hierarchyRepository.findBySupervisorAndEmployee(mockUser1, mockUser2)).thenReturn(new Hierarchy(1, mockUser1, mockUser2));
		Hierarchy retrievedHierarchy = hierarchyService.findBySupervisorAndEmployee(mockUser1, mockUser2);
		Assert.assertEquals("Stone Ocean", retrievedHierarchy.getEmployeeUser().getUsername());
	}
	
	@Test
	public void testFindBySupervisor() {
		User mockUser1 = new User(1, "Star Platinum", "Stand Proud", "Jotaro", "Kujo", "sp@email.com", new Role(1, "Being awesome"));
		User mockUser2 = new User(2, "Stone Ocean", "Not yet aired", "Jolene", "Kujo", "so@email.com", new Role(2, "Jailed in Florida"));
		User mockUser3 = new User(3, "The World", "mudamudamuda", "Dio", "Brando", "tw@email.com", new Role(3, "Being Dio"));
		Mockito.when(hierarchyRepository.findBySupervisor(mockUser3)).thenReturn(Arrays.asList(
				new Hierarchy(1, mockUser3, mockUser1),
				new Hierarchy(2, mockUser3, mockUser2)));
		List<Hierarchy> retrievedHierarchies = hierarchyService.findBySupervisor(mockUser3);
		Assert.assertEquals("Stone Ocean", retrievedHierarchies.get(1).getEmployeeUser().getUsername());
	}
	
	@Test
	public void testFindByEmployee() {
		User mockUser1 = new User(1, "Star Platinum", "Stand Proud", "Jotaro", "Kujo", "sp@email.com", new Role(1, "Being awesome"));
		User mockUser2 = new User(2, "Stone Ocean", "Not yet aired", "Jolene", "Kujo", "so@email.com", new Role(2, "Jailed in Florida"));
		User mockUser3 = new User(3, "The World", "mudamudamuda", "Dio", "Brando", "tw@email.com", new Role(3, "Being Dio"));
		Mockito.when(hierarchyRepository.findByEmployee(mockUser2)).thenReturn(Arrays.asList(
				new Hierarchy(1, mockUser1, mockUser2),
				new Hierarchy(2, mockUser3, mockUser2)));
		List<Hierarchy> retrievedHierarchies = hierarchyService.findByEmployee(mockUser2);
		Assert.assertEquals("Stone Ocean", retrievedHierarchies.get(1).getEmployeeUser().getUsername());
	}
	
	@Test
	public void testFindBySupervisorsForEmployee() {
		User mockUser1 = new User(1, "Star Platinum", "Stand Proud", "Jotaro", "Kujo", "sp@email.com", new Role(1, "Being awesome"));
		User mockUser2 = new User(2, "Stone Ocean", "Not yet aired", "Jolene", "Kujo", "so@email.com", new Role(2, "Jailed in Florida"));
		User mockUser3 = new User(3, "The World", "mudamudamuda", "Dio", "Brando", "tw@email.com", new Role(3, "Being Dio"));
		Mockito.when(hierarchyRepository.findByEmployee(mockUser2)).thenReturn(Arrays.asList(
				new Hierarchy(1, mockUser1, mockUser2),
				new Hierarchy(2, mockUser3, mockUser2)));
		List<Hierarchy> retrievedHierarchies = hierarchyService.findByEmployee(mockUser2);
		List<User> retrievedSupervisors = hierarchyService.findSupervisorsForEmployee(retrievedHierarchies);
		Assert.assertEquals("Star Platinum", retrievedSupervisors.get(0).getUsername());
		Assert.assertEquals("The World", retrievedSupervisors.get(1).getUsername());
	}
	
	@Test
	public void testFindByEmployeesForSupervisor() {
		User mockUser1 = new User(1, "Star Platinum", "Stand Proud", "Jotaro", "Kujo", "sp@email.com", new Role(1, "Being awesome"));
		User mockUser2 = new User(2, "Stone Ocean", "Not yet aired", "Jolene", "Kujo", "so@email.com", new Role(2, "Jailed in Florida"));
		User mockUser3 = new User(3, "The World", "mudamudamuda", "Dio", "Brando", "tw@email.com", new Role(3, "Being Dio"));
		Mockito.when(hierarchyRepository.findBySupervisor(mockUser3)).thenReturn(Arrays.asList(
				new Hierarchy(1, mockUser3, mockUser1),
				new Hierarchy(2, mockUser3, mockUser2)));
		List<Hierarchy> retrievedHierarchies = hierarchyService.findBySupervisor(mockUser3);
		List<User> retrievedEmployees = hierarchyService.findEmployeesForSupervisor(retrievedHierarchies);
		Assert.assertEquals("Star Platinum", retrievedEmployees.get(0).getUsername());
		Assert.assertEquals("Stone Ocean", retrievedEmployees.get(1).getUsername());
	}

}
