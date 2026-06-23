package com.revature.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.Role;
import com.revature.util.HibernateSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RoleRepositoryImpl implements RoleRepository {

	private static final Logger LOG = LogManager.getLogger(RoleRepositoryImpl.class);

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
			LOG.error("Role lookup failed", e);
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
			LOG.error("Role lookup failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		return retrievedRole;
	}
	

}
