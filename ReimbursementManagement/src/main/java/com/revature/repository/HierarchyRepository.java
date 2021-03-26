package com.revature.repository;

import java.util.List;

import com.revature.model.Hierarchy;
import com.revature.model.User;

public interface HierarchyRepository {
	
	Hierarchy findById(int id);
	Hierarchy findBySupervisorAndEmployee(User supervisor, User employee);
	List<Hierarchy> findAll();
	List<Hierarchy> findBySupervisor(User supervisor);
	List<Hierarchy> findByEmployee(User employee);
	void createRelationship(User supervisor, User employee);
	void deleteRelationship(User supervisor, User employee);
	void updateRelationship(User supervisor, User employee);

}
