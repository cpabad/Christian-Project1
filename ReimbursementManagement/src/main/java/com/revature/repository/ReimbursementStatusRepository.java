package com.revature.repository;

import com.revature.model.ReimbursementStatus;

public interface ReimbursementStatusRepository {
	
	ReimbursementStatus findById(int id);
	ReimbursementStatus findByStatus(String status);

}
