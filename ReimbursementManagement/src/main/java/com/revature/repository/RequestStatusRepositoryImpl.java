package com.revature.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.RequestStatus;
import com.revature.util.HibernateSessionFactory;

public class RequestStatusRepositoryImpl implements RequestStatusRepository {

	@Override
	public RequestStatus findById(int id) {
		RequestStatus requestStatus = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			requestStatus = s.createQuery("FROM RequestStatus s WHERE s.statusId = :id", RequestStatus.class)
					.setParameter("id", id)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return requestStatus;
	}

	@Override
	public RequestStatus findByStatus(String status) {
		RequestStatus requestStatus = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			requestStatus = s.createQuery("FROM RequestStatus s WHERE s.status = :status", RequestStatus.class)
					.setParameter("status", status)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return requestStatus;
	}

}
