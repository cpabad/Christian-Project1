package com.revature.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.model.CityStatePostal;

public class CityStatePostalRepositoryTest {
	
	public static CityStatePostalRepositoryImpl cityStatePostalRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		cityStatePostalRepository = new CityStatePostalRepositoryImpl();
	}
	
	@Test
	public void testFindByPostal() {
		CityStatePostal retrievedCityStatePostal = cityStatePostalRepository.findByPostal(1);
		Assert.assertEquals("WHERE", retrievedCityStatePostal.getState());
	}
	
	@Test
	public void testFindByCityAndState() {
		List<CityStatePostal> retrievedListOfCityStatePostal = cityStatePostalRepository.findByCityAndState("EVERY", "WHERE");
		Assert.assertEquals(1, retrievedListOfCityStatePostal.get(0).getPostalCode());
		Assert.assertEquals(2, retrievedListOfCityStatePostal.get(1).getPostalCode());
	}

}
