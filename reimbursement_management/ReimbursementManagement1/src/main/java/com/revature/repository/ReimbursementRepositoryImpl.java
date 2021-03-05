package com.revature.repository;

import java.sql.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.Reimbursement;
import com.revature.model.Request;
import com.revature.model.User;
import com.revature.util.HibernateSessionFactory;

public class ReimbursementRepositoryImpl implements ReimbursementRepository {

	@Override
	public Reimbursement findById(int id) {
		Reimbursement reimbursement = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			reimbursement = s.createQuery("FROM Reimbursement r WHERE r.reimbursementId = :id", Reimbursement.class)
					.setParameter("id", id)
					.getSingleResult();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return reimbursement;
	}

	@Override
	public Reimbursement findByRequest(Request request) {
		Reimbursement reimbursement = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			reimbursement = s.createQuery("FROM Reimbursement r WHERE r.finalApproval.request = :request", Reimbursement.class)
					.setParameter("request", request)
					.getSingleResult();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return reimbursement;
	}

	@Override
	public Reimbursement findByEventDateAndRequester(String eventDate, User requester) {
		Reimbursement reimbursement = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			reimbursement = s.createQuery("FROM Reimbursement r WHERE r.finalApproval.request.eventDate = :date AND r.finalApproval.request.requester = :user", Reimbursement.class)
					.setParameter("date", Date.valueOf(eventDate))
					.setParameter("user", requester)
					.getSingleResult();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return reimbursement;
	}

	@Override
	public void addReimbursement(Reimbursement reimbursement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateReimbursement(Reimbursement reimbursement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteReimbursement(Reimbursement reimbursement) {
		// TODO Auto-generated method stub
		
	}

}
