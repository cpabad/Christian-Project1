package com.revature.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	
	private static Connection conn;
	
	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(
					System.getenv("dburl"), 
					System.getenv("dbuser"),
					System.getenv("dbpassword"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return conn;
	}

}
