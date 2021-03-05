package com.revature.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionClosers {
	
	public static void closeConnectionStatement(Connection conn, Statement stmt) {
		try {
			conn.close();
			stmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeConnectionStatementResultSet(Connection conn, Statement stmt, ResultSet set) {
		try {
			conn.close();
			stmt.close();
			set.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
