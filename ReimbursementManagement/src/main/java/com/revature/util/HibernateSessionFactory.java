package com.revature.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactory {
	
	private static SessionFactory sessionFactory;
	
	public static Session getSession() throws HibernateException {
		if(sessionFactory == null) {
			FlowTrace.log(HibernateSessionFactory.class, "first use - building the singleton SessionFactory from hibernate.cfg.xml + dburl/dbuser/dbpassword env vars (expensive, happens once)");
			sessionFactory = new Configuration()
					.configure()
					.setProperty("hibernate.connection.url", System.getenv("dburl"))
					.setProperty("hibernate.connection.username", System.getenv("dbuser"))
					.setProperty("hibernate.connection.password", System.getenv("dbpassword"))
					.buildSessionFactory();
		}
		FlowTrace.log(HibernateSessionFactory.class, "handing the current thread-bound Hibernate Session to the caller");
		return sessionFactory.getCurrentSession();
	}

}
