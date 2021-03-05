package com.revature.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.Hierarchy;
import com.revature.model.Role;
import com.revature.model.User;

public class HierarchyRepositoryTest {
	
	private static HierarchyRepositoryImpl hierarchyRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		hierarchyRepository = new HierarchyRepositoryImpl();
	}
	
	@Test
	public void testFindById() {
		Hierarchy retrievedHierarchy = hierarchyRepository.findById(1);
		Assert.assertEquals("B", retrievedHierarchy.getEmployeeUser().getFirstName());
	}
	
	@Test
	public void testFindBySupervisorAndEmployee() {
		User supervisorUser = new User(2, "employee1", "employeePassword", "B", "B", "b@email.com", new Role(1, "Supervisor"));
		User employeeUser = new User(4, "employee3", "employeePassword", "D", "D", "d@email.com", new Role(2, "Employee"));
		Hierarchy retrievedHierarchy = hierarchyRepository.findBySupervisorAndEmployee(supervisorUser, employeeUser);
		Assert.assertEquals("D", retrievedHierarchy.getEmployeeUser().getFirstName());
	}
	
	@Test
	public void testFindBySupervisor() {
		User supervisorUser = new User(1, "admin", "adminPassword", "A", "A", "a@email.com", new Role(1, "Supervisor"));
		User employeeUser1 = new User(2, "employee1", "employeePassword", "B", "B", "b@email.com", new Role(1, "Supervisor"));
		User employeeUser2 = new User(4, "employee3", "employeePassword", "D", "D", "d@email.com", new Role(2, "Employee"));
		List<Hierarchy> employees = hierarchyRepository.findBySupervisor(supervisorUser);
		Assert.assertEquals(employeeUser1, employees.get(0).getEmployeeUser());
		Assert.assertEquals(employeeUser2, employees.get(2).getEmployeeUser());
	}
	
	@Test
	public void testFindByEmployee() {
		User supervisorUser1 = new User(1, "admin", "adminPassword", "A", "A", "a@email.com", new Role(1, "Supervisor"));
		User supervisorUser2 = new User(2, "employee1", "employeePassword", "B", "B", "b@email.com", new Role(1, "Supervisor"));
		User employeeUser = new User(4, "employee3", "employeePassword", "D", "D", "d@email.com", new Role(2, "Employee"));
		List<Hierarchy> supervisors = hierarchyRepository.findByEmployee(employeeUser);
		Assert.assertEquals(supervisorUser2, supervisors.get(0).getSupervisorUser());
		Assert.assertEquals(supervisorUser1, supervisors.get(1).getSupervisorUser());
	}

}
