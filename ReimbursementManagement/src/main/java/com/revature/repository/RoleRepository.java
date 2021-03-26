package com.revature.repository;

import com.revature.model.Role;

public interface RoleRepository {
	
	Role findById(int id);
	Role findByRole(String role);

}
