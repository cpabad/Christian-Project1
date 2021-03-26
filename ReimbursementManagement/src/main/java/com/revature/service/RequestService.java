package com.revature.service;

import java.util.List;

import com.revature.model.EventLocation;
import com.revature.model.Request;
import com.revature.model.User;
import com.revature.repository.RequestRepository;
import com.revature.repository.RequestRepositoryImpl;

public class RequestService {
	
	private RequestRepository requestRepository;
	
	public RequestService() {
		requestRepository = new RequestRepositoryImpl();
	}
	
	public Request findById(int id) {
		return this.requestRepository.findById(id);
	}
	
	public Request findByDateLocationRequester(String date, EventLocation eventLocation, User requester) {
		return this.requestRepository.findByDateLocationRequester(date, eventLocation, requester);
	}
	
	public List<Request> findAll() {
		return this.requestRepository.findAll();
	}
	
	public List<Request> findByRequester(User requester) {
		return this.requestRepository.findByRequester(requester);
	}
	
	public List<Request> findByRequesterAndPendingStatus(User requester) {
		return this.requestRepository.findByRequesterAndPendingStatus(requester);
	}
	
	public List<Request> findByRequesterAndResolvedStatus(User requester) {
		return this.requestRepository.findByRequesterAndResolvedStatus(requester);
	}
	
	public void makeNewRequest(Request request) {
		this.requestRepository.makeNewRequest(request);
	}
	
	public void updateRequest(Request request) {
		this.requestRepository.updateRequest(request);
	}
	
	public void deleteRequest(Request request) {
		
	}

}
