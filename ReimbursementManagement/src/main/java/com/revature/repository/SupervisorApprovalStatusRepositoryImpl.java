package com.revature.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.SupervisorApprovalStatus;
import com.revature.util.HibernateSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SupervisorApprovalStatusRepositoryImpl implements SupervisorApprovalStatusRepository {

	private static final Logger LOG = LogManager.getLogger(SupervisorApprovalStatusRepositoryImpl.class);

	@Override
	public SupervisorApprovalStatus findById(int id) {
		SupervisorApprovalStatus sas = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			sas = s.createQuery("FROM SupervisorApprovalStatus sas WHERE sas.statusId = :id", SupervisorApprovalStatus.class)
					.setParameter("id", id)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			LOG.error("Supervisor approval status lookup failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		return sas;
	}

	@Override
	public SupervisorApprovalStatus findByStatus(String status) {
		SupervisorApprovalStatus sas = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			sas = s.createQuery("FROM SupervisorApprovalStatus sas WHERE sas.status = :status", SupervisorApprovalStatus.class)
					.setParameter("status", status)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			LOG.error("Supervisor approval status lookup failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		return sas;
	}

}
