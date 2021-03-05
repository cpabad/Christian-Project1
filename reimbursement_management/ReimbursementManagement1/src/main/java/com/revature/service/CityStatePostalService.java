package com.revature.service;

import java.util.ArrayList;
import java.util.List;

import com.revature.model.CityStatePostal;
import com.revature.repository.CityStatePostalRepository;
import com.revature.repository.CityStatePostalRepositoryImpl;

public class CityStatePostalService {
	
	private CityStatePostalRepository cityStatePostalRepository;
	
	public CityStatePostalService() {
		cityStatePostalRepository = new CityStatePostalRepositoryImpl();
	}
	
	public CityStatePostal findByPostal(int postalCode) {
		return this.cityStatePostalRepository.findByPostal(postalCode);
	}
	
	public List<CityStatePostal> findByCityAndState(String city, String state) {
		return this.cityStatePostalRepository.findByCityAndState(city, state);
	}
	
	public List<Integer> findPostalByCityAndState(List<CityStatePostal> listOfCityStatePostal) {
		List<Integer> listOfPostalCodes = new ArrayList<>();
		for(CityStatePostal csp : listOfCityStatePostal) {
			listOfPostalCodes.add(csp.getPostalCode());
		}
		return listOfPostalCodes;
	}
	
	public void insert(CityStatePostal cityStatePostal) {
		
	}

}
