package com.revature.service;

import java.util.ArrayList;
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
	
	public List<SupervisorApproval> findByRequestAndRequester(Request request, User requester) {
		return this.sar.findByRequestAndRequester(request, requester);
	}
	
	public SupervisorApproval findByRequestRequesterManager(Request request, User requester, User manager) {
		return this.sar.findByRequestRequesterManager(request, requester, manager);
	}
	
	public List<SupervisorApproval> findPendingRequestsForManager(User manager) {
		List<SupervisorApproval> pendingApprovals = new ArrayList<>();
		for(SupervisorApproval a : this.findAll()) {
			if(a.getSupervisorApprovalStatus().getStatusId() == 2 && a.getHierarchy().getSupervisorUser().equals(manager)) {
				pendingApprovals.add(a);
			}
		}
		return pendingApprovals;
	}
	
	public List<SupervisorApproval> findWhoResolvedAllRequests() {
		List<SupervisorApproval> approvals = new ArrayList<>();
		for(SupervisorApproval a : this.findAll()) {
			if(a.getSupervisorApprovalStatus().getStatusId() == 1) {
				approvals.add(a);
			}
		}
		return approvals;
	}
	
	public List<Request> findRequestsMadeByEmployee(User manager, User employee) {
		List<Request> requests = new ArrayList<>();
		for(SupervisorApproval a : this.findAll()) {
			if(a.getHierarchy().getSupervisorUser().equals(manager) && a.getHierarchy().getEmployeeUser().equals(employee)) {
				requests.add(a.getRequest());
			}
		}
		return requests;
	}
	
	public void addApproval(SupervisorApproval approval) {
		this.sar.addApproval(approval);
	}
	
	public void updateApproval(SupervisorApproval approval) {
		this.sar.updateApproval(approval);
	}
	
	public void deleteApproval(SupervisorApproval approval) {
		
	}

}
