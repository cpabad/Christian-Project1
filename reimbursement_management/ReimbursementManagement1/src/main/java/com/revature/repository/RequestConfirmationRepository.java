package com.revature.repository;

import com.revature.model.Request;
import com.revature.model.RequestConfirmation;

public interface RequestConfirmationRepository {
	
	RequestConfirmation findById(int id);
	RequestConfirmation findByDateAndRequest(String date, Request request);
	void addConfirmation(RequestConfirmation confirmation);
	void deleteConfirmation(RequestConfirmation confirmation);
	

}
