package com.revature.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.Role;
import com.revature.util.HibernateSessionFactory;

public class RoleRepositoryImpl implements RoleRepository {

	@Override
	public Role findById(int id) {
		Role role = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			role = s.createQuery("FROM Role r WHERE r.roleId = :roleId", Role.class)
					.setParameter("roleId", id)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return role;
	}

	@Override
	public Role findByRole(String role) {
		Role retrievedRole = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			retrievedRole = s.createQuery("FROM Role r WHERE r.role = :role", Role.class)
					.setParameter("role", role)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return retrievedRole;
	}
	

}
