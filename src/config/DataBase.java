package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import config.Global;

public class DataBase {
	
	public void checkAndCreateTable() 
	{
		// Read RDS Connection Information from the Environment
		  String dbName = Global.dbName;
		  String userName = Global.userName;
		  String password = Global.password;
		  String hostname = Global.hostname;
		  String port = Global.port;
		  String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
		    port + "/" + dbName + "?user=" + userName + "&password=" + password;
		  
		  // Load the JDBC Driver
		  try {
		    System.out.println("Loading driver...");
		    Class.forName("com.mysql.jdbc.Driver");
		    System.out.println("Driver loaded!");
		  } catch (ClassNotFoundException e) {
		    throw new RuntimeException("Cannot find the driver in the classpath!", e);
		  }
		  
		  Connection conn = null;
		  Statement setupStatement = null;
		  try {
		    // Create connection to RDS instance
		    conn = DriverManager.getConnection(jdbcUrl);
		    
		    // Create a table and write two rows
		    setupStatement = conn.createStatement();
		    String createTable1 = "CREATE TABLE IF NOT EXISTS info ("
		    		+ "twid varchar(50) NOT NULL PRIMARY KEY, "
		    		+ "time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
		    		+ "lati VARCHAR(20), "
		    		+ "longi VARCHAR(20) "
		    		+");";		    
		    String createTable3 = "CREATE TABLE IF NOT EXISTS keyword ("
		    		+ "keyid INT NOT NULL PRIMARY KEY AUTO_INCREMENT, "
		    		+ "kword VARCHAR(50) NOT NULL,"
		    		+ "UNIQUE (kword)"
		    		+ ");";
		    
		    String createTable2 = "CREATE TABLE IF NOT EXISTS twit_keyword ("
		    		+ "twid VARCHAR(50) NOT NULL REFERENCES info(twid), "
		    		+ "keyid INT NOT NULL REFERENCES KEYWORD(kid)"
		    		+ ");";
		    String createTable4 = "CREATE TABLE IF NOT EXISTS twit_sent ("
		    		+ "twid VARCHAR(50) NOT NULL REFERENCES info(twid), "
		    		+ "sent VARCHAR(30)"
		    		+ ");";
		    setupStatement.addBatch(createTable1);
		    setupStatement.addBatch(createTable3);
		    setupStatement.addBatch(createTable2);
		    setupStatement.addBatch(createTable4);
		    setupStatement.executeBatch();
		    setupStatement.close();
		    
		  } catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		  } finally {
		    System.out.println("Closing the connection.");
		    if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
		  }

	}
}
