package com.revature.service;

import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementConfirmation;
import com.revature.model.User;
import com.revature.repository.ReimbursementConfirmationRepository;
import com.revature.repository.ReimbursementConfirmationRepositoryImpl;

public class ReimbursementConfirmationService {
	
	private ReimbursementConfirmationRepository confirmationRepository;
	
	public ReimbursementConfirmationService() {
		confirmationRepository = new ReimbursementConfirmationRepositoryImpl();
	}
	
	public ReimbursementConfirmation findById(int id) {
		return this.confirmationRepository.findById(id);
	}
	
	public ReimbursementConfirmation findByEventDateAndRequester(String eventDate, User requester) {
		return this.confirmationRepository.findByEventDateAndRequester(eventDate, requester);
	}
	
	public ReimbursementConfirmation findByConfirmationDateAndReimbursement(String confirmationDate, Reimbursement reimbursement) {
		return this.confirmationRepository.findByConfirmationDateAndReimbursement(confirmationDate, reimbursement);
	}
	
	public void addConfirmation(ReimbursementConfirmation confirmation) {
		
	}
	
	public void deleteConfirmation(ReimbursementConfirmation confirmation) {
		
	}

}
