package com.revature.service;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.repository.EventLocationRepository;
import com.revature.repository.EventLocationRepositoryImpl;

public class EventLocationService {
	
	private EventLocationRepository eventLocationRepository;
	
	public EventLocationService() {
		eventLocationRepository = new EventLocationRepositoryImpl();
	}
	
	public EventLocation findById(int id) {
		return this.eventLocationRepository.findById(id);
	}
	
	public EventLocation findByStreetNumberNamePostalCode(int streetNumber, String streetName, CityStatePostal postalCode) {
		return this.eventLocationRepository.findByStreetNumberNamePostalCode(streetNumber, streetName, postalCode);
	}
	
	public void addEventLocation(EventLocation location) {
		
	}

}
