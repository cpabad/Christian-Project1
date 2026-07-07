package com.revature.service;

import java.util.ArrayList;
import java.util.List;

import com.revature.model.ApprovalOutcome;
import com.revature.model.Hierarchy;
import com.revature.model.Reimbursement;
import com.revature.model.Request;
import com.revature.model.SupervisorApproval;
import com.revature.model.User;
import com.revature.repository.HierarchyRepository;
import com.revature.repository.HierarchyRepositoryImpl;
import com.revature.repository.ReimbursementRepository;
import com.revature.repository.ReimbursementRepositoryImpl;
import com.revature.repository.ReimbursementStatusRepository;
import com.revature.repository.ReimbursementStatusRepositoryImpl;
import com.revature.repository.RequestRepository;
import com.revature.repository.RequestRepositoryImpl;
import com.revature.repository.RequestStatusRepository;
import com.revature.repository.RequestStatusRepositoryImpl;
import com.revature.repository.SupervisorApprovalRepository;
import com.revature.repository.SupervisorApprovalRepositoryImpl;
import com.revature.repository.SupervisorApprovalStatusRepository;
import com.revature.repository.SupervisorApprovalStatusRepositoryImpl;
import com.revature.repository.UserRepository;
import com.revature.repository.UserRepositoryImpl;

public class SupervisorApprovalService {

	private SupervisorApprovalRepository sar;
	private SupervisorApprovalStatusRepository supervisorApprovalStatusRepository;
	private RequestRepository requestRepository;
	private RequestStatusRepository requestStatusRepository;
	private ReimbursementRepository reimbursementRepository;
	private ReimbursementStatusRepository reimbursementStatusRepository;
	private HierarchyRepository hierarchyRepository;
	private UserRepository userRepository;

	public SupervisorApprovalService() {
		sar = new SupervisorApprovalRepositoryImpl();
		supervisorApprovalStatusRepository = new SupervisorApprovalStatusRepositoryImpl();
		requestRepository = new RequestRepositoryImpl();
		requestStatusRepository = new RequestStatusRepositoryImpl();
		reimbursementRepository = new ReimbursementRepositoryImpl();
		reimbursementStatusRepository = new ReimbursementStatusRepositoryImpl();
		hierarchyRepository = new HierarchyRepositoryImpl();
		userRepository = new UserRepositoryImpl();
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

	/**
	 * Applies a manager's approve/deny decision to their approval of a request,
	 * then works out where the request now stands in the approval chain.
	 *
	 * Status ids: 1 = resolved, 2 = pending (approval, request, and reimbursement statuses).
	 * The decision only persists when it settles something: a final approval or a
	 * denial resolves the request and its reimbursement; an approval that still
	 * needs a higher supervisor persists just this approval; and while peer
	 * managers are still pending, nothing is written at all.
	 */
	public ApprovalOutcome resolveApproval(int requestId, int managerId, boolean decision) {
		Request request = this.requestRepository.findById(requestId);
		User manager = this.userRepository.findById(managerId);
		SupervisorApproval approval = this.sar.findByRequestRequesterManager(request, request.getRequester(), manager);
		approval.setApproval(decision);
		approval.setSupervisorApprovalStatus(this.supervisorApprovalStatusRepository.findById(1));

		List<User> managersEmployees = new ArrayList<>();
		for(Hierarchy h : this.hierarchyRepository.findBySupervisor(manager)) {
			managersEmployees.add(h.getEmployeeUser());
		}
		int pendingNonTopApprovals = 0;
		int pendingSubordinateApprovals = 0;
		for(SupervisorApproval a : this.sar.findByRequestAndRequester(request, request.getRequester())) {
			if(this.hierarchyRepository.findByEmployee(a.getHierarchy().getSupervisorUser()).isEmpty() == false && a.getSupervisorApprovalStatus().getStatusId() == 2) {
				pendingNonTopApprovals++;
			} else if(managersEmployees.contains(a.getHierarchy().getSupervisorUser()) && a.getSupervisorApprovalStatus().getStatusId() == 2) {
				pendingSubordinateApprovals++;
			}
		}

		if(pendingNonTopApprovals == 0 && this.hierarchyRepository.findByEmployee(manager).isEmpty() && approval.isApproval()) {
			resolveRequestAndReimbursement(request, approval);
			return ApprovalOutcome.APPROVED;
		} else if(pendingSubordinateApprovals == 0 && approval.isApproval()) {
			this.sar.updateApproval(approval);
			return ApprovalOutcome.ESCALATED;
		} else if(pendingSubordinateApprovals == 0) {
			resolveRequestAndReimbursement(request, approval);
			return ApprovalOutcome.DENIED;
		} else {
			return ApprovalOutcome.WAITING_ON_OTHERS;
		}
	}

	private void resolveRequestAndReimbursement(Request request, SupervisorApproval approval) {
		request.setRequestStatus(this.requestStatusRepository.findById(1));
		Reimbursement reimbursement = this.reimbursementRepository.findByRequest(request);
		reimbursement.setReimbursementStatus(this.reimbursementStatusRepository.findById(1));
		this.reimbursementRepository.updateReimbursement(reimbursement);
		this.sar.updateApproval(approval);
		this.requestRepository.updateRequest(request);
	}
	
	public void deleteApproval(SupervisorApproval approval) {
		
	}

}
