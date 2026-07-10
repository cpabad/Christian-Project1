package com.revature.service;

import com.revature.model.RequestStatus;
import com.revature.repository.RequestStatusRepository;
import com.revature.repository.RequestStatusRepositoryImpl;
import com.revature.util.FlowTrace;

public class RequestStatusService {
	
	private RequestStatusRepository requestStatusRepository;
	
	public RequestStatusService() {
		requestStatusRepository = new RequestStatusRepositoryImpl();
	}
	
	public RequestStatus findById(int id) {
		FlowTrace.log(RequestStatusService.class, "findById: service operation begins");
		return this.requestStatusRepository.findById(id);
	}
	
	public RequestStatus findByStatus(String status) {
		FlowTrace.log(RequestStatusService.class, "findByStatus: service operation begins");
		return this.requestStatusRepository.findByStatus(status);
	}

}
