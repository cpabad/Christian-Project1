package com.revature.repository;

import java.util.List;

import com.revature.model.EventLocation;
import com.revature.model.Request;
import com.revature.model.User;

public interface RequestRepository {
	
	Request findById(int id);
	Request findByDateLocationRequester(String eventDate, EventLocation eventLocation, User requester);
	List<Request> findAll();
	List<Request> findByRequester(User requester);
	List<Request> findByRequesterAndPendingStatus(User requester);
	List<Request> findByRequesterAndResolvedStatus(User requester);
	void makeNewRequest(Request request);
	void updateRequest(Request request);
	void deleteRequest(Request request);

}
