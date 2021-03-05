package com.revature.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.SupervisorApprovalStatus;
import com.revature.util.HibernateSessionFactory;

public class SupervisorApprovalStatusRepositoryImpl implements SupervisorApprovalStatusRepository {

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
			e.printStackTrace();
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
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return sas;
	}

}
