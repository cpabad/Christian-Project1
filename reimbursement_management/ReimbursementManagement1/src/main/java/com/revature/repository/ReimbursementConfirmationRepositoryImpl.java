package com.revature.repository;

import java.sql.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementConfirmation;
import com.revature.model.User;
import com.revature.util.HibernateSessionFactory;

public class ReimbursementConfirmationRepositoryImpl implements ReimbursementConfirmationRepository {

	@Override
	public ReimbursementConfirmation findById(int id) {
		ReimbursementConfirmation rc = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			rc = s.createQuery("FROM ReimbursementConfirmation rc WHERE rc.confirmationId = :id", ReimbursementConfirmation.class)
					.setParameter("id", id)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return rc;
	}

	@Override
	public ReimbursementConfirmation findByEventDateAndRequester(String eventDate, User requester) {
		ReimbursementConfirmation rc = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			rc = s.createQuery("FROM ReimbursementConfirmation rc WHERE rc.reimbursementAwarded.finalApproval.request.eventDate = :date AND rc.reimbursementAwarded.finalApproval.request.requester = :user", ReimbursementConfirmation.class)
					.setParameter("date", Date.valueOf(eventDate))
					.setParameter("user", requester)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return rc;
	}

	@Override
	public ReimbursementConfirmation findByConfirmationDateAndReimbursement(String confirmationDate, Reimbursement reimbursement) {
		ReimbursementConfirmation rc = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			rc = s.createQuery("FROM ReimbursementConfirmation rc WHERE rc.confirmationDate = :date AND rc.reimbursementAwarded = :reimbursement", ReimbursementConfirmation.class)
					.setParameter("date", Date.valueOf(confirmationDate))
					.setParameter("reimbursement", reimbursement)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return rc;
	}

	@Override
	public void addConfirmation(ReimbursementConfirmation confirmation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteConfirmation(ReimbursementConfirmation confirmation) {
		// TODO Auto-generated method stub
		
	}
	

}
