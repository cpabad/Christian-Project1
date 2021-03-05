package com.revature.repository;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;

public interface EventLocationRepository {
	
	EventLocation findById(int id);
	EventLocation findByStreetNumberNamePostalCode(int streetNumber, String streetName, CityStatePostal postalCode);
	void addEventLocation(EventLocation location);

}
