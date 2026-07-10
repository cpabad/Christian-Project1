package com.revature.service;

import java.util.ArrayList;
import java.util.List;

import com.revature.model.CityStatePostal;
import com.revature.repository.CityStatePostalRepository;
import com.revature.repository.CityStatePostalRepositoryImpl;
import com.revature.util.FlowTrace;

public class CityStatePostalService {
	
	private CityStatePostalRepository cityStatePostalRepository;
	
	public CityStatePostalService() {
		cityStatePostalRepository = new CityStatePostalRepositoryImpl();
	}
	
	public CityStatePostal findByPostal(int postalCode) {
		FlowTrace.log(CityStatePostalService.class, "findByPostal: service operation begins");
		return this.cityStatePostalRepository.findByPostal(postalCode);
	}
	
	public List<CityStatePostal> findByCityAndState(String city, String state) {
		FlowTrace.log(CityStatePostalService.class, "findByCityAndState: service operation begins");
		return this.cityStatePostalRepository.findByCityAndState(city, state);
	}
	
	public List<Integer> findPostalByCityAndState(List<CityStatePostal> listOfCityStatePostal) {
		FlowTrace.log(CityStatePostalService.class, "findPostalByCityAndState: service operation begins");
		List<Integer> listOfPostalCodes = new ArrayList<>();
		for(CityStatePostal csp : listOfCityStatePostal) {
			listOfPostalCodes.add(csp.getPostalCode());
		}
		return listOfPostalCodes;
	}
	
	public void insert(CityStatePostal cityStatePostal) {
		
	}

}
