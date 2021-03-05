package com.revature.service;

import com.revature.model.ReimbursementStatus;
import com.revature.repository.ReimbursementStatusRepository;
import com.revature.repository.ReimbursementStatusRepositoryImpl;

public class ReimbursementStatusService {
	
	private ReimbursementStatusRepository statusRepository;
	
	public ReimbursementStatusService() {
		statusRepository = new ReimbursementStatusRepositoryImpl();
	}
	
	public ReimbursementStatus findById(int id) {
		return this.statusRepository.findById(id);
	}
	
	public ReimbursementStatus findByStatus(String status) {
		return this.statusRepository.findByStatus(status);
	}

}
