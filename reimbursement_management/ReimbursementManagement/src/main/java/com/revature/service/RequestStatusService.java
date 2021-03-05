package com.revature.service;

import com.revature.model.RequestStatus;
import com.revature.repository.RequestStatusRepository;
import com.revature.repository.RequestStatusRepositoryImpl;

public class RequestStatusService {
	
	private RequestStatusRepository requestStatusRepository;
	
	public RequestStatusService() {
		requestStatusRepository = new RequestStatusRepositoryImpl();
	}
	
	public RequestStatus findById(int id) {
		return this.requestStatusRepository.findById(id);
	}
	
	public RequestStatus findByStatus(String status) {
		return this.requestStatusRepository.findByStatus(status);
	}

}
