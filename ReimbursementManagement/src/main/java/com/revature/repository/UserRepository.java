package com.revature.repository;

import com.revature.model.User;

public interface UserRepository {
	
	User findById(int id);
	User findByUsername(String username);
	User findByEmail(String email);
	void registerEmployee(User employee);
	void updateEmployee(User employee);
	void deleteEmployee(User employee);

}
