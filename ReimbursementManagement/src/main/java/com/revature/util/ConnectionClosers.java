package com.revature.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionClosers {

	private static final Logger LOG = LogManager.getLogger(ConnectionClosers.class);
	
	public static void closeConnectionStatement(Connection conn, Statement stmt) {
		try {
			conn.close();
			stmt.close();
		} catch(SQLException e) {
			LOG.error("Failed to close JDBC resources", e);
		}
	}
	
	public static void closeConnectionStatementResultSet(Connection conn, Statement stmt, ResultSet set) {
		try {
			conn.close();
			stmt.close();
			set.close();
		} catch(SQLException e) {
			LOG.error("Failed to close JDBC resources", e);
		}
	}

}
