package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import config.Global;

public class DBConn {
	private static String dbName = Global.dbName;
	private static String userName = Global.userName;
	private static String password = Global.password;
	private static String hostname = Global.hostname;
	private static String port = Global.port;
	
	public static Connection getConnection(){
		String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
			    port + "/" + dbName + "?user=" + userName + "&password=" + password;
		
		// Load the JDBC Driver
		  try {
		    Class.forName("com.mysql.jdbc.Driver");
		  } catch (ClassNotFoundException e) {
		    throw new RuntimeException("Cannot find the driver in the classpath!", e);
		  }
		  Connection conn = null;
		  try {
			conn = DriverManager.getConnection(jdbcUrl);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return conn;		
	}
	
	
	public static boolean closeConnection(Connection conn){
		boolean rt = true;
		if (conn!=null){
			try{
				conn.close();
			}catch (SQLException ignore) {
			}
		}
		return rt;		
	}
	
	public static void doInsert(String sql, Connection conn){
		Statement statement = null;
		try {
			statement = conn.createStatement();
			statement.addBatch(sql);
			statement.executeBatch();
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void doDelete(String sql, Connection conn){
		Statement statement = null;
		try {
			statement = conn.createStatement();
			statement.addBatch(sql);
			statement.executeBatch();
		    
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
		
	public static ResultSet doSelect (String sql, Connection conn, Statement stmt){
		
		ResultSet rs = null;		
		try {
			stmt = conn.createStatement();
			if (stmt.execute(sql)) {
			    rs = stmt.getResultSet();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	public static void closeResultSet(ResultSet rs, Statement stmt){
		if (rs != null) {
	        try {
	            rs.close();
	        } catch (SQLException sqlEx) { } // ignore
		}
		rs = null;
	    if (stmt != null) {
		     try {
		        stmt.close();
		     } catch (SQLException sqlEx) { } // ignore
		        stmt = null;   
	    }		
	}
}
