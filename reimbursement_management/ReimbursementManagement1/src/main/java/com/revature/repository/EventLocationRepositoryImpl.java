package com.revature.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.util.HibernateSessionFactory;

public class EventLocationRepositoryImpl implements EventLocationRepository {

	@Override
	public EventLocation findById(int id) {
		EventLocation eventLocation = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			eventLocation = s.createQuery("FROM EventLocation e WHERE e.locationId = :locationId", EventLocation.class)
					.setParameter("locationId", id)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return eventLocation;
	}

	@Override
	public EventLocation findByStreetNumberNamePostalCode(int streetNumber, String streetName, CityStatePostal postalCode) {
		EventLocation eventLocation = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			eventLocation = s.createQuery("FROM EventLocation e WHERE e.street_number = :streetNumber AND e.street_name = :streetName AND e.cityStatePostal = :postalCode", EventLocation.class)
					.setParameter("streetNumber", streetNumber)
					.setParameter("streetName", streetName)
					.setParameter("postalCode", postalCode)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return eventLocation;
	}

	@Override
	public void addEventLocation(EventLocation location) {
		// TODO Auto-generated method stub
		
	}

}
