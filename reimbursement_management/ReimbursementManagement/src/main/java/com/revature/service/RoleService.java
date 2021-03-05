package com.revature.service;

import com.revature.model.Role;
import com.revature.repository.RoleRepository;
import com.revature.repository.RoleRepositoryImpl;

public class RoleService {
	
	private RoleRepository roleRepository;
	
	public RoleService() {
		roleRepository = new RoleRepositoryImpl();
	}
	
	public Role findById(int id) {
		return this.roleRepository.findById(id);
	}
	
	public Role findByRole(String role) {
		return this.roleRepository.findByRole(role);
	}

}
