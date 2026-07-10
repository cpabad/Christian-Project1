package com.revature.service;

import java.util.List;

import com.revature.model.Reimbursement;
import com.revature.model.Request;
import com.revature.model.User;
import com.revature.repository.ReimbursementRepository;
import com.revature.repository.ReimbursementRepositoryImpl;
import com.revature.util.FlowTrace;

public class ReimbursementService {
	
	private ReimbursementRepository reimbursementRepository;
	
	public ReimbursementService() {
		reimbursementRepository = new ReimbursementRepositoryImpl();
	}
	
	public Reimbursement findById(int id) {
		FlowTrace.log(ReimbursementService.class, "findById: service operation begins");
		return this.reimbursementRepository.findById(id);
	}
	
	public Reimbursement findByRequest(Request request) {
		FlowTrace.log(ReimbursementService.class, "findByRequest: service operation begins");
		return this.reimbursementRepository.findByRequest(request);
	}
	
	public Reimbursement findByEventDateAndRequester(String eventDate, User requester) {
		FlowTrace.log(ReimbursementService.class, "findByEventDateAndRequester: service operation begins");
		return this.reimbursementRepository.findByEventDateAndRequester(eventDate, requester);
	}
	
	public List<Reimbursement> findAll(){
		FlowTrace.log(ReimbursementService.class, "findAll: service operation begins");
		return this.reimbursementRepository.findAll();
	}
	
	public void addReimbursement(Reimbursement reimbursement) {
		FlowTrace.log(ReimbursementService.class, "addReimbursement: service operation begins");
		this.reimbursementRepository.addReimbursement(reimbursement);
	}
	
	public void updateReimbursement(Reimbursement reimbursement) {
		FlowTrace.log(ReimbursementService.class, "updateReimbursement: service operation begins");
		this.reimbursementRepository.updateReimbursement(reimbursement);
	}
	
	public void deleteReimbursement(Reimbursement reimbursement) {
		FlowTrace.log(ReimbursementService.class, "deleteReimbursement: service operation begins");
		
	}

}
