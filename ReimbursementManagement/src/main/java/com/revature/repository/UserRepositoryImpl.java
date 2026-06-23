package com.revature.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.User;
import com.revature.util.HibernateSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserRepositoryImpl implements UserRepository {

	private static final Logger LOG = LogManager.getLogger(UserRepositoryImpl.class);
	
	@Override
	public User findById(int id) {
		User user = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			user = s.createQuery("FROM User user WHERE user.userId = :userId", User.class)
					.setParameter("userId", id)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			LOG.error("User persistence operation failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		return user;
	}

	@Override
	public User findByUsername(String username) {
		User user = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			user = s.createQuery("FROM User user WHERE user.username = :username", User.class)
					.setParameter("username", username)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			LOG.error("User persistence operation failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		return user;
	}

	@Override
	public User findByEmail(String email) {
		User user = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			user = s.createQuery("FROM User user WHERE user.email = :email", User.class)
					.setParameter("email", email)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			LOG.error("User persistence operation failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		return user;
	}

	@Override
	public void registerEmployee(User employee) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateEmployee(User employee) {
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			s.merge(employee);
			tx.commit();
		} catch (HibernateException e) {
			LOG.error("User persistence operation failed", e);
			tx.rollback();
		} finally {
			s.close();
		}
		
	}

	@Override
	public void deleteEmployee(User employee) {
		// TODO Auto-generated method stub
		
	}

}
