package com.revature.service;

import com.revature.model.SupervisorApprovalStatus;
import com.revature.repository.SupervisorApprovalStatusRepository;
import com.revature.repository.SupervisorApprovalStatusRepositoryImpl;
import com.revature.util.FlowTrace;

public class SupervisorApprovalStatusService {
	
	private SupervisorApprovalStatusRepository sasr;
	
	public SupervisorApprovalStatusService() {
		sasr = new SupervisorApprovalStatusRepositoryImpl();
	}
	
	public SupervisorApprovalStatus findById(int id) {
		FlowTrace.log(SupervisorApprovalStatusService.class, "findById: service operation begins");
		return this.sasr.findById(id);
	}
	
	public SupervisorApprovalStatus findByStatus(String status) {
		FlowTrace.log(SupervisorApprovalStatusService.class, "findByStatus: service operation begins");
		return this.sasr.findByStatus(status);
	}

}
