package com.revature.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionFactory {

	private static final Logger LOG = LogManager.getLogger(ConnectionFactory.class);
	
	private static Connection conn;
	
	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(
					System.getenv("dburl"), 
					System.getenv("dbuser"),
					System.getenv("dbpassword"));
		} catch (ClassNotFoundException e) {
			LOG.error("Failed to load PostgreSQL JDBC driver", e);
		}
		return conn;
	}

}
