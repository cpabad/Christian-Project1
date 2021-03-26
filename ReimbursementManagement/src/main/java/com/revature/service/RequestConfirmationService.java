package com.revature.service;

import com.revature.model.Request;
import com.revature.model.RequestConfirmation;
import com.revature.repository.RequestConfirmationRepository;
import com.revature.repository.RequestConfirmationRepositoryImpl;

public class RequestConfirmationService {
	
	private RequestConfirmationRepository requestConfirmationRepository;
	
	public RequestConfirmationService() {
		requestConfirmationRepository = new RequestConfirmationRepositoryImpl();
	}
	
	public RequestConfirmation findById(int id) {
		return this.requestConfirmationRepository.findById(id);
	}
	
	public RequestConfirmation findByDateAndRequest(String date, Request request) {
		return this.requestConfirmationRepository.findByDateAndRequest(date, request);
	}
	
	public void addConfirmation(RequestConfirmation confirmation) {
		
	}
	
	public void deleteConfirmation(RequestConfirmation confirmation) {
		
	}

}
