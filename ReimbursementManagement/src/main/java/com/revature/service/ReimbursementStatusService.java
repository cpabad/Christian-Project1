package com.revature.service;

import com.revature.model.ReimbursementStatus;
import com.revature.repository.ReimbursementStatusRepository;
import com.revature.repository.ReimbursementStatusRepositoryImpl;
import com.revature.util.FlowTrace;

public class ReimbursementStatusService {
	
	private ReimbursementStatusRepository statusRepository;
	
	public ReimbursementStatusService() {
		statusRepository = new ReimbursementStatusRepositoryImpl();
	}
	
	public ReimbursementStatus findById(int id) {
		FlowTrace.log(ReimbursementStatusService.class, "findById: service operation begins");
		return this.statusRepository.findById(id);
	}
	
	public ReimbursementStatus findByStatus(String status) {
		FlowTrace.log(ReimbursementStatusService.class, "findByStatus: service operation begins");
		return this.statusRepository.findByStatus(status);
	}

}
