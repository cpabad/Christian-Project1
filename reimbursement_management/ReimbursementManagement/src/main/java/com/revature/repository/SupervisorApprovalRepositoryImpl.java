package com.revature.repository;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.Request;
import com.revature.model.SupervisorApproval;
import com.revature.model.User;
import com.revature.util.HibernateSessionFactory;

public class SupervisorApprovalRepositoryImpl implements SupervisorApprovalRepository {

	@Override
	public List<SupervisorApproval> findAll() {
		List<SupervisorApproval> approvals = new ArrayList<>();
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			approvals = s.createQuery("FROM SupervisorApproval", SupervisorApproval.class).getResultList();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return approvals;
	}

	@Override
	public SupervisorApproval findById(int id) {
		SupervisorApproval approval = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			approval = s.createQuery("FROM SupervisorApproval sa WHERE sa.approvalId = :id", SupervisorApproval.class)
					.setParameter("id", id)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return approval;
	}

	@Override
	public SupervisorApproval findByRequestAndRequester(Request request, User requester) {
		SupervisorApproval approval = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			approval = s.createQuery("FROM SupervisorApproval sa WHERE sa.request = :request AND sa.request.requester = :user", SupervisorApproval.class)
					.setParameter("request", request)
					.setParameter("user", requester)
					.getSingleResult();
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return approval;
	}

	@Override
	public void addApproval(SupervisorApproval approval) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateApproval(SupervisorApproval approval) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteApproval(SupervisorApproval approval) {
		// TODO Auto-generated method stub
		
	}
	

}
