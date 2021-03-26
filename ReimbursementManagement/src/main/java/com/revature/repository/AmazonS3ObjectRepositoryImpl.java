package com.revature.repository;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.revature.model.AmazonS3Object;
import com.revature.model.Request;
import com.revature.util.HibernateSessionFactory;

public class AmazonS3ObjectRepositoryImpl implements AmazonS3ObjectRepository{

	@Override
	public AmazonS3Object findById(int id) {
		AmazonS3Object object = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			object = s.createQuery("FROM AmazonS3Object s WHERE s.imageId = :id", AmazonS3Object.class)
					.setParameter("id", id)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return object;
	}

	@Override
	public AmazonS3Object findByURL(String url) {
		AmazonS3Object object = null;
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			object = s.createQuery("FROM AmazonS3Object s WHERE s.url = :url", AmazonS3Object.class)
					.setParameter("url", url)
					.getSingleResult();
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return object;
	}

	@Override
	public List<AmazonS3Object> findByRequest(Request request) {
		List<AmazonS3Object> objects = new ArrayList<>();
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			objects = s.createQuery("FROM AmazonS3Object s WHERE s.request = :request", AmazonS3Object.class)
					.setParameter("request", request)
					.getResultList();
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		return objects;
	}

	@Override
	public void addObject(AmazonS3Object object) {
		Session s = null;
		Transaction tx = null;
		try {
			s = HibernateSessionFactory.getSession();
			tx = s.beginTransaction();
			s.save(object);
			tx.commit();
		} catch(HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			s.close();
		}
		
	}

	@Override
	public void deleteObject(AmazonS3Object object) {
		// TODO Auto-generated method stub
		
	}

}
