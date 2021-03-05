package com.revature.service;

import java.util.List;

import com.revature.model.Reimbursement;
import com.revature.model.Request;
import com.revature.model.User;
import com.revature.repository.ReimbursementRepository;
import com.revature.repository.ReimbursementRepositoryImpl;

public class ReimbursementService {
	
	private ReimbursementRepository reimbursementRepository;
	
	public ReimbursementService() {
		reimbursementRepository = new ReimbursementRepositoryImpl();
	}
	
	public Reimbursement findById(int id) {
		return this.reimbursementRepository.findById(id);
	}
	
	public Reimbursement findByRequest(Request request) {
		return this.reimbursementRepository.findByRequest(request);
	}
	
	public Reimbursement findByEventDateAndRequester(String eventDate, User requester) {
		return this.reimbursementRepository.findByEventDateAndRequester(eventDate, requester);
	}
	
	public List<Reimbursement> findAll(){
		return this.reimbursementRepository.findAll();
	}
	
	public void addReimbursement(Reimbursement reimbursement) {
		
	}
	
	public void updateReimbursement(Reimbursement reimbursement) {
		
	}
	
	public void deleteReimbursement(Reimbursement reimbursement) {
		
	}

}
