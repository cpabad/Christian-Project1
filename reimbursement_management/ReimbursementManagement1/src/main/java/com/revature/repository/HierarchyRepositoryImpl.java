package com.revature.repository;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.Hierarchy;
import com.revature.model.User;
import com.revature.util.HibernateSessionFactory;

public class HierarchyRepositoryImpl implements HierarchyRepository{

	@Override
	public Hierarchy findById(int id) {
		Hierarchy hierarchy = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			hierarchy = s.createQuery("FROM Hierarchy hierarchy WHERE hierarchy.hierarchyId = :hierarchyId", Hierarchy.class)
					.setParameter("hierarchyId", id)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return hierarchy;
	}

	@Override
	public Hierarchy findBySupervisorAndEmployee(User supervisor, User employee) {
		Hierarchy hierarchy = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			hierarchy = s.createQuery("FROM Hierarchy hierarchy WHERE hierarchy.supervisorUser = :supervisorUser AND hierarchy.employeeUser = :employeeUser", Hierarchy.class)
					.setParameter("supervisorUser", supervisor)
					.setParameter("employeeUser", employee)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return hierarchy;
	}

	@Override
	public List<Hierarchy> findBySupervisor(User supervisor) {
		List<Hierarchy> employees = new ArrayList<>();
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			employees = s.createQuery("From Hierarchy h where h.supervisorUser = :supervisorUser", Hierarchy.class).setParameter("supervisorUser", supervisor).getResultList();
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return employees;
	}

	@Override
	public List<Hierarchy> findByEmployee(User employee) {
		List<Hierarchy> supervisors = new ArrayList<>();
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			supervisors = s.createQuery("From Hierarchy h where h.employeeUser = :employeeUser", Hierarchy.class).setParameter("employeeUser", employee).getResultList();
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return supervisors;
	}

	@Override
	public void createRelationship(User supervisor, User employee) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteRelationship(User supervisor, User employee) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateRelationship(User supervisor, User employee) {
		// TODO Auto-generated method stub
		
	}

}
