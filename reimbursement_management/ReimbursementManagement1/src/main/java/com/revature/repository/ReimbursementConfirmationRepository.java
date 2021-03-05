package com.revature.repository;

import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementConfirmation;
import com.revature.model.User;

public interface ReimbursementConfirmationRepository {
	
	ReimbursementConfirmation findById(int id);
	ReimbursementConfirmation findByEventDateAndRequester(String eventDate, User requester);
	ReimbursementConfirmation findByConfirmationDateAndReimbursement(String confirmationDate, Reimbursement reimbursement);
	void addConfirmation(ReimbursementConfirmation confirmation);
	void deleteConfirmation(ReimbursementConfirmation confirmation);

}
