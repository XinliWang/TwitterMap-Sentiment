package db;

import java.sql.Connection;
import java.util.TimerTask;


public class DataCleaner extends TimerTask{
  private Connection conn;
  
  public DataCleaner(Connection con){
    this.conn = con;
  }
  
  @Override
  public void run() {
    System.out.println("-----------------------clean data---------------------");
    // TODO Auto-generated method stub
    String sql = "DELETE i.*, t.*  FROM info i, twit_sent t  "
        + "WHERE t.twid = i.twid  "
        + "AND i.time < CURRENT_TIMESTAMP - INTERVAL 30 MINUTE;";
    
    DBConn.doDelete(sql, conn);
         
  }
  
  
}
