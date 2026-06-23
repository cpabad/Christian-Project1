package com.revature.repository;

import java.sql.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.Request;
import com.revature.model.RequestConfirmation;
import com.revature.util.HibernateSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestConfirmationRepositoryImpl implements RequestConfirmationRepository {

	private static final Logger LOG = LogManager.getLogger(RequestConfirmationRepositoryImpl.class);

	@Override
	public RequestConfirmation findById(int id) {
		RequestConfirmation confirmation = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			confirmation = s.createQuery("FROM RequestConfirmation rc WHERE rc.confirmationId = :id", RequestConfirmation.class)
					.setParameter("id", id)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			LOG.error("Request confirmation persistence operation failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		return confirmation;
	}

	@Override
	public RequestConfirmation findByDateAndRequest(String date, Request request) {
		Date eventDate = Date.valueOf(date);
		RequestConfirmation confirmation = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			confirmation = s.createQuery("FROM RequestConfirmation rc WHERE rc.confirmationDate = :date AND rc.request = :request", RequestConfirmation.class)
					.setParameter("date", eventDate)
					.setParameter("request", request)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			LOG.error("Request confirmation persistence operation failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		return confirmation;
	}

	@Override
	public void addConfirmation(RequestConfirmation confirmation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteConfirmation(RequestConfirmation confirmation) {
		// TODO Auto-generated method stub
		
	}

}
