package com.revature.repository;

import java.util.List;

import com.revature.model.CityStatePostal;

public interface CityStatePostalRepository {
	
	CityStatePostal findByPostal(int postalCode);
	List<CityStatePostal> findByCityAndState(String city, String state);
	void insert(CityStatePostal cityStatePostal);

}
