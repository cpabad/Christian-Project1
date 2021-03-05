package com.revature.repository;

import com.revature.model.SupervisorApprovalStatus;

public interface SupervisorApprovalStatusRepository {
	
	SupervisorApprovalStatus findById(int id);
	SupervisorApprovalStatus findByStatus(String status);

}
