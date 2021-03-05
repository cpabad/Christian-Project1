package com.revature.repository;

import com.revature.model.Reimbursement;
import com.revature.model.Request;
import com.revature.model.User;

public interface ReimbursementRepository {
	
	Reimbursement findById(int id);
	Reimbursement findByRequest(Request request);
	Reimbursement findByEventDateAndRequester(String eventDate, User requester);
	void addReimbursement(Reimbursement reimbursement);
	void updateReimbursement(Reimbursement reimbursement);
	void deleteReimbursement(Reimbursement reimbursement);

}
