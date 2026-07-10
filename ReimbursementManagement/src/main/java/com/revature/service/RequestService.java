package com.revature.service;

import java.sql.Date;
import java.util.List;

import com.revature.model.EventLocation;
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
import com.revature.repository.SupervisorApprovalRepository;
import com.revature.repository.SupervisorApprovalRepositoryImpl;
import com.revature.repository.SupervisorApprovalStatusRepository;
import com.revature.repository.SupervisorApprovalStatusRepositoryImpl;
import com.revature.util.FlowTrace;

public class RequestService {

	private RequestRepository requestRepository;
	private HierarchyRepository hierarchyRepository;
	private SupervisorApprovalRepository supervisorApprovalRepository;
	private SupervisorApprovalStatusRepository supervisorApprovalStatusRepository;
	private ReimbursementRepository reimbursementRepository;
	private ReimbursementStatusRepository reimbursementStatusRepository;

	public RequestService() {
		requestRepository = new RequestRepositoryImpl();
		hierarchyRepository = new HierarchyRepositoryImpl();
		supervisorApprovalRepository = new SupervisorApprovalRepositoryImpl();
		supervisorApprovalStatusRepository = new SupervisorApprovalStatusRepositoryImpl();
		reimbursementRepository = new ReimbursementRepositoryImpl();
		reimbursementStatusRepository = new ReimbursementStatusRepositoryImpl();
	}
	
	public Request findById(int id) {
		FlowTrace.log(RequestService.class, "findById: service operation begins");
		return this.requestRepository.findById(id);
	}
	
	public Request findByDateLocationRequester(String date, EventLocation eventLocation, User requester) {
		FlowTrace.log(RequestService.class, "findByDateLocationRequester: service operation begins");
		return this.requestRepository.findByDateLocationRequester(date, eventLocation, requester);
	}
	
	public List<Request> findAll() {
		FlowTrace.log(RequestService.class, "findAll: service operation begins");
		return this.requestRepository.findAll();
	}
	
	public List<Request> findByRequester(User requester) {
		FlowTrace.log(RequestService.class, "findByRequester: service operation begins");
		return this.requestRepository.findByRequester(requester);
	}
	
	public List<Request> findByRequesterAndPendingStatus(User requester) {
		FlowTrace.log(RequestService.class, "findByRequesterAndPendingStatus: service operation begins");
		return this.requestRepository.findByRequesterAndPendingStatus(requester);
	}
	
	public List<Request> findByRequesterAndResolvedStatus(User requester) {
		FlowTrace.log(RequestService.class, "findByRequesterAndResolvedStatus: service operation begins");
		return this.requestRepository.findByRequesterAndResolvedStatus(requester);
	}
	
	public void makeNewRequest(Request request) {
		FlowTrace.log(RequestService.class, "makeNewRequest: service operation begins");
		this.requestRepository.makeNewRequest(request);
	}

	/**
	 * Persists a new reimbursement request and fans out its approval chain:
	 * one pending approval per direct supervisor of the requester, plus the
	 * pending reimbursement record for the supervisor at the top of the chain
	 * (the one with nobody above them). Status id 2 = pending. Returns the
	 * request re-read from the database, so it carries its generated id.
	 */
	public Request submitRequest(Request newRequest) {
		FlowTrace.log(RequestService.class, "submitRequest: service operation begins");
		this.requestRepository.makeNewRequest(newRequest);
		Request submittedRequest = this.requestRepository.findByDateLocationRequester(newRequest.getEventDate().toString(), newRequest.getEventLocation(), newRequest.getRequester());
		for(Hierarchy h : this.hierarchyRepository.findByEmployee(newRequest.getRequester())) {
			SupervisorApproval newApproval = new SupervisorApproval(100, Date.valueOf("2000-01-01"), submittedRequest, h, this.supervisorApprovalStatusRepository.findById(2), false);
			this.supervisorApprovalRepository.addApproval(newApproval);
			if(this.hierarchyRepository.findByEmployee(h.getSupervisorUser()).isEmpty()) {
				Reimbursement reimbursement = new Reimbursement(100, submittedRequest.getAmount(), Date.valueOf("2000-01-01"), newApproval, this.reimbursementStatusRepository.findById(2));
				this.reimbursementRepository.addReimbursement(reimbursement);
			}
		}
		return submittedRequest;
	}
	
	public void updateRequest(Request request) {
		FlowTrace.log(RequestService.class, "updateRequest: service operation begins");
		this.requestRepository.updateRequest(request);
	}
	
	public void deleteRequest(Request request) {
		FlowTrace.log(RequestService.class, "deleteRequest: service operation begins");
		
	}

}
