package com.revature.repository;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;

public class EventLocationRepositoryTest {
	
	private static EventLocationRepositoryImpl eventLocationRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		eventLocationRepository = new EventLocationRepositoryImpl();
	}
	
	@Test
	public void testFindById() {
		EventLocation retrievedLocation = eventLocationRepository.findById(1);
		Assert.assertEquals(1, retrievedLocation.getCityStatePostal().getPostalCode());
	}
	
	@Test
	public void testFindByStreetNumberNamePostalCode() {
		EventLocation retrievedLocation = eventLocationRepository.findByStreetNumberNamePostalCode(100, "MAIN AVE", new CityStatePostal(1, "EVERY", "WHERE"));
		Assert.assertEquals(1, retrievedLocation.getCityStatePostal().getPostalCode());
	}

}
