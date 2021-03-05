package com.revature.service;

import java.util.List;

import com.revature.model.Request;
import com.revature.model.SupervisorApproval;
import com.revature.model.User;
import com.revature.repository.SupervisorApprovalRepository;
import com.revature.repository.SupervisorApprovalRepositoryImpl;

public class SupervisorApprovalService {
	
	private SupervisorApprovalRepository sar;
	
	public SupervisorApprovalService() {
		sar = new SupervisorApprovalRepositoryImpl();
	}
	
	public List<SupervisorApproval> findAll() {
		return this.sar.findAll();
	}
	
	public SupervisorApproval findById(int id) {
		return this.sar.findById(id);
	}
	
	public SupervisorApproval findByRequestAndRequester(Request request, User requester) {
		return this.sar.findByRequestAndRequester(request, requester);
	}
	
	public void addApproval(SupervisorApproval approval) {
		
	}
	
	public void updateApproval(SupervisorApproval approval) {
		
	}
	
	public void deleteApproval(SupervisorApproval approval) {
		
	}

}
