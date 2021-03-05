package com.revature.repository;

import java.sql.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.SupervisorApproval;
import com.revature.model.SupervisorApprovalConfirmation;
import com.revature.util.HibernateSessionFactory;

public class SupervisorApprovalConfirmationRepositoryImpl implements SupervisorApprovalConfirmationRepository {

	@Override
	public SupervisorApprovalConfirmation findById(int id) {
		SupervisorApprovalConfirmation confirmation = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			confirmation = s.createQuery("FROM SupervisorApprovalConfirmation sac WHERE sac.confirmationId = :id", SupervisorApprovalConfirmation.class)
					.setParameter("id", id)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.commit();
		} finally {
			s.close();
		}
		return confirmation;
	}

	@Override
	public SupervisorApprovalConfirmation findByDateAndApproval(String date, SupervisorApproval approval) {
		SupervisorApprovalConfirmation confirmation = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			confirmation = s.createQuery("FROM SupervisorApprovalConfirmation sac WHERE sac.confirmationDate = :date AND sac.approval = :approval", SupervisorApprovalConfirmation.class)
					.setParameter("date", Date.valueOf(date))
					.setParameter("approval", approval)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.commit();
		} finally {
			s.close();
		}
		return confirmation;
	}

	@Override
	public void addConfirmation(SupervisorApproval approval) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteConfirmation(SupervisorApproval approval) {
		// TODO Auto-generated method stub
		
	}

}
