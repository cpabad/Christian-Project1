package com.revature.repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.EventLocation;
import com.revature.model.Request;
import com.revature.model.User;
import com.revature.util.HibernateSessionFactory;

public class RequestRepositoryImpl implements RequestRepository {

	@Override
	public Request findById(int id) {
		Request request = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			request = s.createQuery("FROM Request r WHERE r.requestId = :requestId", Request.class)
					.setParameter("requestId", id)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return request;
	}

	@Override
	public Request findByDateLocationRequester(String date, EventLocation eventLocation, User requester) {
		Date eventDate = Date.valueOf(date);
		Request request = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			request = s.createQuery("FROM Request r WHERE r.eventDate = :eventDate AND r.eventLocation = :eventLocation AND r.requester = :user", Request.class)
					.setParameter("eventDate", eventDate)
					.setParameter("eventLocation", eventLocation)
					.setParameter("user", requester)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return request;
	}

	@Override
	public List<Request> findAll() {
		List<Request> requests = new ArrayList<>();
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			requests = s.createQuery("FROM Request", Request.class).getResultList();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return requests;
	}

	@Override
	public List<Request> findByRequester(User requester) {
		List<Request> requests = new ArrayList<>();
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			requests = s.createQuery("FROM Request r WHERE r.requester = :user", Request.class)
					.setParameter("user", requester)
					.getResultList();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return requests;
	}

	@Override
	public List<Request> findByRequesterAndPendingStatus(User requester) {
		List<Request> requests = new ArrayList<>();
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			requests = s.createQuery("FROM Request r WHERE r.requester = :user AND r.requestStatus.statusId = 2", Request.class)
					.setParameter("user", requester)
					.getResultList();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return requests;
	}

	@Override
	public List<Request> findByRequesterAndResolvedStatus(User requester) {
		List<Request> requests = new ArrayList<>();
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			requests = s.createQuery("FROM Request r WHERE r.requester = :user AND r.requestStatus.statusId = 1", Request.class)
					.setParameter("user", requester)
					.getResultList();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return requests;
	}

	@Override
	public void makeNewRequest(Request request) {
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			s.save(request);
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
	}

	@Override
	public void updateRequest(Request request) {
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			s.merge(request);
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
	}

	@Override
	public void deleteRequest(Request request) {
		// TODO Auto-generated method stub
		
	}

}
