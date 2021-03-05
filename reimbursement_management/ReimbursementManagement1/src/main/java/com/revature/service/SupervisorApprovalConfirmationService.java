package com.revature.service;

import com.revature.model.SupervisorApproval;
import com.revature.model.SupervisorApprovalConfirmation;
import com.revature.repository.SupervisorApprovalConfirmationRepository;
import com.revature.repository.SupervisorApprovalConfirmationRepositoryImpl;

public class SupervisorApprovalConfirmationService {
	
	private SupervisorApprovalConfirmationRepository sacr;
	
	public SupervisorApprovalConfirmationService() {
		sacr = new SupervisorApprovalConfirmationRepositoryImpl();
	}
	
	public SupervisorApprovalConfirmation findById(int id) {
		return this.sacr.findById(id);
	}
	
	public SupervisorApprovalConfirmation findByDateAndApproval(String date, SupervisorApproval approval) {
		return this.sacr.findByDateAndApproval(date, approval);
	}
	
	public void addConfirmation(SupervisorApprovalConfirmation confirmation) {
		
	}
	
	public void deleteConfirmation(SupervisorApprovalConfirmation confirmation) {
		
	}

}
