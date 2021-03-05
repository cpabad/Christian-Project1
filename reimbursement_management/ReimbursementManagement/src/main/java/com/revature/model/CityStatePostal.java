package com.revature.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This model represents an event's city, state and postal code location.
 * @author ca132
 *
 */
@Entity
@Table(name = "city_state_postal", schema = "\"ExpenseReimbursementManagementSystem\"")
public class CityStatePostal {
	
	/**
	 * The postalCode field is a unique numeric identifier for a city and state.
	 */
	@Column
	@Id
	private int postalCode;
	/**
	 * The city field is the event location's city.
	 */
	@Column
	private String city;
	/**
	 * The state field is the event location's state.
	 */
	@Column
	private String state;
	public int getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + postalCode;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CityStatePostal other = (CityStatePostal) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (postalCode != other.postalCode)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "CityStatePostal [postalCode=" + postalCode + ", city=" + city + ", state=" + state + "]";
	}
	public CityStatePostal(int postalCode, String city, String state) {
		super();
		this.postalCode = postalCode;
		this.city = city;
		this.state = state;
	}
	public CityStatePostal() {
		super();
	}
	

}
