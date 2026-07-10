package com.revature.service;

import com.revature.model.Role;
import com.revature.repository.RoleRepository;
import com.revature.repository.RoleRepositoryImpl;
import com.revature.util.FlowTrace;

public class RoleService {
	
	private RoleRepository roleRepository;
	
	public RoleService() {
		roleRepository = new RoleRepositoryImpl();
	}
	
	public Role findById(int id) {
		FlowTrace.log(RoleService.class, "findById: service operation begins");
		return this.roleRepository.findById(id);
	}
	
	public Role findByRole(String role) {
		FlowTrace.log(RoleService.class, "findByRole: service operation begins");
		return this.roleRepository.findByRole(role);
	}

}
