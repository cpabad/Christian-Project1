package com.revature.service;

import java.util.ArrayList;
import java.util.List;

import com.revature.model.Hierarchy;
import com.revature.model.User;
import com.revature.repository.HierarchyRepository;
import com.revature.repository.HierarchyRepositoryImpl;
import com.revature.util.FlowTrace;

public class HierarchyService {
	
	private HierarchyRepository hierarchyRepository;
	
	public HierarchyService() {
		hierarchyRepository = new HierarchyRepositoryImpl();
	}
	
	public Hierarchy findById(int id) {
		FlowTrace.log(HierarchyService.class, "findById: service operation begins");
		return this.hierarchyRepository.findById(id);
	}
	
	public Hierarchy findBySupervisorAndEmployee(User supervisor, User employee) {
		FlowTrace.log(HierarchyService.class, "findBySupervisorAndEmployee: service operation begins");
		return this.hierarchyRepository.findBySupervisorAndEmployee(supervisor, employee);
	}
	
	public List<Hierarchy> findAll() {
		FlowTrace.log(HierarchyService.class, "findAll: service operation begins");
		return this.hierarchyRepository.findAll();
	}
	
	public List<Hierarchy> findBySupervisor(User supervisor) {
		FlowTrace.log(HierarchyService.class, "findBySupervisor: service operation begins");
		return this.hierarchyRepository.findBySupervisor(supervisor);
	}
	
	public List<Hierarchy> findByEmployee(User employee) {
		FlowTrace.log(HierarchyService.class, "findByEmployee: service operation begins");
		return this.hierarchyRepository.findByEmployee(employee);
	}
	
	public List<User> findSupervisorsForEmployee(List<Hierarchy> listOfSupervisors) {
		FlowTrace.log(HierarchyService.class, "findSupervisorsForEmployee: service operation begins");
		List<User> supervisors = new ArrayList<>();
		for(Hierarchy h : listOfSupervisors) {
			supervisors.add(h.getSupervisorUser());
		}
		return supervisors;
	}
	
	public List<User> findEmployeesForSupervisor(List<Hierarchy> listOfEmployees) {
		FlowTrace.log(HierarchyService.class, "findEmployeesForSupervisor: service operation begins");
		List<User> employees = new ArrayList<>();
		for(Hierarchy h : listOfEmployees) {
			employees.add(h.getEmployeeUser());
		}
		return employees;
	}
	
	

}
