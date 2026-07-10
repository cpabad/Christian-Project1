package com.revature.service;

import com.revature.model.Request;
import com.revature.model.RequestConfirmation;
import com.revature.repository.RequestConfirmationRepository;
import com.revature.repository.RequestConfirmationRepositoryImpl;
import com.revature.util.FlowTrace;

public class RequestConfirmationService {
	
	private RequestConfirmationRepository requestConfirmationRepository;
	
	public RequestConfirmationService() {
		requestConfirmationRepository = new RequestConfirmationRepositoryImpl();
	}
	
	public RequestConfirmation findById(int id) {
		FlowTrace.log(RequestConfirmationService.class, "findById: service operation begins");
		return this.requestConfirmationRepository.findById(id);
	}
	
	public RequestConfirmation findByDateAndRequest(String date, Request request) {
		FlowTrace.log(RequestConfirmationService.class, "findByDateAndRequest: service operation begins");
		return this.requestConfirmationRepository.findByDateAndRequest(date, request);
	}
	
	public void addConfirmation(RequestConfirmation confirmation) {
		
	}
	
	public void deleteConfirmation(RequestConfirmation confirmation) {
		
	}

}
