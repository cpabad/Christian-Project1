package com.revature.repository;

import com.revature.model.SupervisorApproval;
import com.revature.model.SupervisorApprovalConfirmation;

public interface SupervisorApprovalConfirmationRepository {
	
	SupervisorApprovalConfirmation findById(int id);
	SupervisorApprovalConfirmation findByDateAndApproval(String date, SupervisorApproval approval);
	void addConfirmation(SupervisorApproval approval);
	void deleteConfirmation(SupervisorApproval approval);

}
