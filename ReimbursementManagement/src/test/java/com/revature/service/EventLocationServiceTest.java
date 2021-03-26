package com.revature.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.repository.EventLocationRepositoryImpl;

public class EventLocationServiceTest {
	
	@InjectMocks private static EventLocationService eventLocationService;
	@Mock private static EventLocationRepositoryImpl eventLocationRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		eventLocationService = new EventLocationService();
	}
	
	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testFindById() {
		EventLocation mockEventLocation = new EventLocation(1, 1, "COAST OF CALIFORNIA", new CityStatePostal(1, "CORN CITY", "UNITED STATES"));
		Mockito.when(eventLocationRepository.findById(1)).thenReturn(mockEventLocation);
		EventLocation retrievedLocation = eventLocationService.findById(1);
		Assert.assertEquals("CORN CITY", retrievedLocation.getCityStatePostal().getCity());
	}
	
	@Test
	public void testFindByStreetNumberNamePostalCode() {
		EventLocation mockEventLocation = new EventLocation(1, 1, "COAST OF CALIFORNIA", new CityStatePostal(1, "CORN CITY", "UNITED STATES"));
		Mockito.when(eventLocationRepository.findByStreetNumberNamePostalCode(1, "COAST OF CALIFORNIA", new CityStatePostal(1, "CORN CITY", "UNITED STATES"))).thenReturn(mockEventLocation);
		EventLocation retrievedLocation = eventLocationService.findByStreetNumberNamePostalCode(1, "COAST OF CALIFORNIA", new CityStatePostal(1, "CORN CITY", "UNITED STATES"));
		Assert.assertEquals("CORN CITY", retrievedLocation.getCityStatePostal().getCity());
	}

}
