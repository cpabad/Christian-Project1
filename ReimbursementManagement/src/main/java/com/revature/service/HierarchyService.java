package com.revature.service;

import java.util.ArrayList;
import java.util.List;

import com.revature.model.Hierarchy;
import com.revature.model.User;
import com.revature.repository.HierarchyRepository;
import com.revature.repository.HierarchyRepositoryImpl;

public class HierarchyService {
	
	private HierarchyRepository hierarchyRepository;
	
	public HierarchyService() {
		hierarchyRepository = new HierarchyRepositoryImpl();
	}
	
	public Hierarchy findById(int id) {
		return this.hierarchyRepository.findById(id);
	}
	
	public Hierarchy findBySupervisorAndEmployee(User supervisor, User employee) {
		return this.hierarchyRepository.findBySupervisorAndEmployee(supervisor, employee);
	}
	
	public List<Hierarchy> findAll() {
		return this.hierarchyRepository.findAll();
	}
	
	public List<Hierarchy> findBySupervisor(User supervisor) {
		return this.hierarchyRepository.findBySupervisor(supervisor);
	}
	
	public List<Hierarchy> findByEmployee(User employee) {
		return this.hierarchyRepository.findByEmployee(employee);
	}
	
	public List<User> findSupervisorsForEmployee(List<Hierarchy> listOfSupervisors) {
		List<User> supervisors = new ArrayList<>();
		for(Hierarchy h : listOfSupervisors) {
			supervisors.add(h.getSupervisorUser());
		}
		return supervisors;
	}
	
	public List<User> findEmployeesForSupervisor(List<Hierarchy> listOfEmployees) {
		List<User> employees = new ArrayList<>();
		for(Hierarchy h : listOfEmployees) {
			employees.add(h.getEmployeeUser());
		}
		return employees;
	}
	
	

}
