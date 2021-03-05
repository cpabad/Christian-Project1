package com.revature.service;

import com.revature.model.SupervisorApprovalStatus;
import com.revature.repository.SupervisorApprovalStatusRepository;
import com.revature.repository.SupervisorApprovalStatusRepositoryImpl;

public class SupervisorApprovalStatusService {
	
	private SupervisorApprovalStatusRepository sasr;
	
	public SupervisorApprovalStatusService() {
		sasr = new SupervisorApprovalStatusRepositoryImpl();
	}
	
	public SupervisorApprovalStatus findById(int id) {
		return this.sasr.findById(id);
	}
	
	public SupervisorApprovalStatus findByStatus(String status) {
		return this.sasr.findByStatus(status);
	}

}
