package com.revature.repository;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.CityStatePostal;
import com.revature.util.HibernateSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CityStatePostalRepositoryImpl implements CityStatePostalRepository {

	private static final Logger LOG = LogManager.getLogger(CityStatePostalRepositoryImpl.class);

	@Override
	public CityStatePostal findByPostal(int postalCode) {
		CityStatePostal cityStatePostal = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			cityStatePostal = s.createQuery("FROM CityStatePostal csp WHERE csp.postalCode = :postalCode", CityStatePostal.class)
					.setParameter("postalCode", postalCode)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			LOG.error("City/state/postal lookup failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		return cityStatePostal;
	}

	@Override
	public List<CityStatePostal> findByCityAndState(String city, String state) {
		List<CityStatePostal> listOfCityStatePostal = new ArrayList<>();
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			listOfCityStatePostal = s.createQuery("FROM CityStatePostal csp WHERE csp.city = :city AND csp.state = :state", CityStatePostal.class)
					.setParameter("city", city)
					.setParameter("state", state)
					.getResultList();
			tx.commit();
		} catch (HibernateException e) {
			LOG.error("City/state/postal lookup failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		return listOfCityStatePostal;
	}

	@Override
	public void insert(CityStatePostal cityStatePostal) {
		// TODO Auto-generated method stub
		
	}
	

}
