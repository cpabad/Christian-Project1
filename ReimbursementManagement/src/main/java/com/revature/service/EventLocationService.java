package com.revature.service;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.repository.EventLocationRepository;
import com.revature.repository.EventLocationRepositoryImpl;
import com.revature.util.FlowTrace;

public class EventLocationService {
	
	private EventLocationRepository eventLocationRepository;
	
	public EventLocationService() {
		eventLocationRepository = new EventLocationRepositoryImpl();
	}
	
	public EventLocation findById(int id) {
		FlowTrace.log(EventLocationService.class, "findById: service operation begins");
		return this.eventLocationRepository.findById(id);
	}
	
	public EventLocation findByStreetNumberNamePostalCode(int streetNumber, String streetName, CityStatePostal postalCode) {
		FlowTrace.log(EventLocationService.class, "findByStreetNumberNamePostalCode: service operation begins");
		return this.eventLocationRepository.findByStreetNumberNamePostalCode(streetNumber, streetName, postalCode);
	}

}
