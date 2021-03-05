package com.revature.repository;

import com.revature.model.RequestStatus;

public interface RequestStatusRepository {
	
	RequestStatus findById(int id);
	RequestStatus findByStatus(String status);

}
