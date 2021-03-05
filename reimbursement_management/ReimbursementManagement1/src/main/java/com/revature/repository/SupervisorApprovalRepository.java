package com.revature.repository;

import java.util.List;

import com.revature.model.Request;
import com.revature.model.SupervisorApproval;
import com.revature.model.User;

public interface SupervisorApprovalRepository {
	
	List<SupervisorApproval> findAll();
	SupervisorApproval findById(int id);
	SupervisorApproval findByRequestAndRequester(Request request, User requester);
	void addApproval(SupervisorApproval approval);
	void updateApproval(SupervisorApproval approval);
	void deleteApproval(SupervisorApproval approval);

}
