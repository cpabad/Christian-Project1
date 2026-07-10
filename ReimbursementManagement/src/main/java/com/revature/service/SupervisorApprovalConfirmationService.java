package com.revature.service;

import com.revature.model.SupervisorApproval;
import com.revature.model.SupervisorApprovalConfirmation;
import com.revature.repository.SupervisorApprovalConfirmationRepository;
import com.revature.repository.SupervisorApprovalConfirmationRepositoryImpl;
import com.revature.util.FlowTrace;

public class SupervisorApprovalConfirmationService {
	
	private SupervisorApprovalConfirmationRepository sacr;
	
	public SupervisorApprovalConfirmationService() {
		sacr = new SupervisorApprovalConfirmationRepositoryImpl();
	}
	
	public SupervisorApprovalConfirmation findById(int id) {
		FlowTrace.log(SupervisorApprovalConfirmationService.class, "findById: service operation begins");
		return this.sacr.findById(id);
	}
	
	public SupervisorApprovalConfirmation findByDateAndApproval(String date, SupervisorApproval approval) {
		FlowTrace.log(SupervisorApprovalConfirmationService.class, "findByDateAndApproval: service operation begins");
		return this.sacr.findByDateAndApproval(date, approval);
	}
	
	public void addConfirmation(SupervisorApprovalConfirmation confirmation) {
		
	}
	
	public void deleteConfirmation(SupervisorApprovalConfirmation confirmation) {
		
	}

}
