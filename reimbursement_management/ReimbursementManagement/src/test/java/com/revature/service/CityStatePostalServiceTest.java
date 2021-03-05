package com.revature.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.revature.model.CityStatePostal;
import com.revature.repository.CityStatePostalRepositoryImpl;

public class CityStatePostalServiceTest {
	
	@InjectMocks private static CityStatePostalService cityStatePostalService;
	@Mock private static CityStatePostalRepositoryImpl cityStatePostalRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		cityStatePostalService = new CityStatePostalService();
	}
	
	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testFindById() {
		CityStatePostal mockCityStatePostal1 = new CityStatePostal(1, "Fictional City", "Fictional State");
		Mockito.when(cityStatePostalRepository.findByPostal(1)).thenReturn(mockCityStatePostal1);
		CityStatePostal retrievedCityStatePostal = cityStatePostalService.findByPostal(1);
		Assert.assertEquals("Fictional City", retrievedCityStatePostal.getCity());
	}
	
	@Test
	public void testFindByCityAndState() {
		CityStatePostal mockCityStatePostal2 = new CityStatePostal(2, "Non-Fictional City", "Fictional State");
		CityStatePostal mockCityStatePostal3 = new CityStatePostal(3, "Non-Fictional City", "Fictional State");
		Mockito.when(cityStatePostalRepository.findByCityAndState("Non-Fictional City", "Fictional State")).thenReturn(Arrays.asList(
				mockCityStatePostal2,
				mockCityStatePostal3));
		List<CityStatePostal> retrievedListOfCityStatePostal = cityStatePostalService.findByCityAndState("Non-Fictional City", "Fictional State");
		Assert.assertEquals(2, retrievedListOfCityStatePostal.get(0).getPostalCode());
		Assert.assertEquals(3, retrievedListOfCityStatePostal.get(1).getPostalCode());
	}
	
	@Test
	public void testFindPostalByCityAndState() {
		CityStatePostal mockCityStatePostal2 = new CityStatePostal(2, "Non-Fictional City", "Fictional State");
		CityStatePostal mockCityStatePostal3 = new CityStatePostal(3, "Non-Fictional City", "Fictional State");
		Mockito.when(cityStatePostalRepository.findByCityAndState("Non-Fictional City", "Fictional State")).thenReturn(Arrays.asList(
				mockCityStatePostal2,
				mockCityStatePostal3));
		List<CityStatePostal> retrievedListOfCityStatePostal = cityStatePostalService.findByCityAndState("Non-Fictional City", "Fictional State");
		List<Integer> retrievedListOfPostalCodes = cityStatePostalService.findPostalByCityAndState(retrievedListOfCityStatePostal);
		Assert.assertEquals(Integer.valueOf(2), retrievedListOfPostalCodes.get(0));
		Assert.assertEquals(Integer.valueOf(3), retrievedListOfPostalCodes.get(1));
	}

}
