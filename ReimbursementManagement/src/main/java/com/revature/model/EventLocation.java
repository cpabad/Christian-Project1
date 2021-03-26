package com.revature.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A model of an event's location.
 * @author ca132
 *
 */
@Entity
@Table(name = "event_location", schema = "\"ExpenseReimbursementManagementSystem\"")
public class EventLocation {
	
	/**
	 * The locationId field is a unique numeric identifier of a specific event location.
	 */
	@Column
	@Id
	private int locationId;
	/**
	 * The street_number field is a numeric identifier of an event's location along a street.
	 */
	@Column
	private int street_number;
	/**
	 * The street_name field is the street name of an event's location.
	 */
	@Column
	private String street_name;
	/**
	 * The cityStatePostal field is the city, state, and postal code of an event's location.
	 */
	@ManyToOne
	@JoinColumn(name = "postalCode")
	private CityStatePostal cityStatePostal;
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public int getStreet_number() {
		return street_number;
	}
	public void setStreet_number(int street_number) {
		this.street_number = street_number;
	}
	public String getStreet_name() {
		return street_name;
	}
	public void setStreet_name(String street_name) {
		this.street_name = street_name;
	}
	public CityStatePostal getCityStatePostal() {
		return cityStatePostal;
	}
	public void setCityStatePostal(CityStatePostal cityStatePostal) {
		this.cityStatePostal = cityStatePostal;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cityStatePostal == null) ? 0 : cityStatePostal.hashCode());
		result = prime * result + locationId;
		result = prime * result + ((street_name == null) ? 0 : street_name.hashCode());
		result = prime * result + street_number;
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
		EventLocation other = (EventLocation) obj;
		if (cityStatePostal == null) {
			if (other.cityStatePostal != null)
				return false;
		} else if (!cityStatePostal.equals(other.cityStatePostal))
			return false;
		if (locationId != other.locationId)
			return false;
		if (street_name == null) {
			if (other.street_name != null)
				return false;
		} else if (!street_name.equals(other.street_name))
			return false;
		if (street_number != other.street_number)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "EventLocation [locationId=" + locationId + ", street_number=" + street_number + ", street_name="
				+ street_name + ", cityStatePostal=" + cityStatePostal + "]";
	}
	public EventLocation(int locationId, int street_number, String street_name, CityStatePostal cityStatePostal) {
		super();
		this.locationId = locationId;
		this.street_number = street_number;
		this.street_name = street_name;
		this.cityStatePostal = cityStatePostal;
	}
	public EventLocation() {
		super();
	}
	
	

}
