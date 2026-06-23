package com.revature.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.ReimbursementStatus;
import com.revature.util.HibernateSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReimbursementStatusRepositoryImpl implements ReimbursementStatusRepository {

	private static final Logger LOG = LogManager.getLogger(ReimbursementStatusRepositoryImpl.class);

	@Override
	public ReimbursementStatus findById(int id) {
		ReimbursementStatus reimbursementStatus = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			reimbursementStatus = s.createQuery("FROM ReimbursementStatus rs WHERE rs.statusId = :id", ReimbursementStatus.class)
					.setParameter("id", id)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			LOG.error("Reimbursement status lookup failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		return reimbursementStatus;
	}

	@Override
	public ReimbursementStatus findByStatus(String status) {
		ReimbursementStatus reimbursementStatus = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			reimbursementStatus = s.createQuery("FROM ReimbursementStatus rs WHERE rs.status = :status", ReimbursementStatus.class)
					.setParameter("status", status)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			LOG.error("Reimbursement status lookup failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		return reimbursementStatus;
	}

}
