package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import appserver.AddSentEvent;
import appserver.SNSEvent;
import appserver.SNSchannel;
import appserver.StreamDaemon;
import appserver.StreamEvent;

import com.amazonaws.services.sns.AmazonSNSClient;

import twitter4j.Status;

public class DataUpListener implements DataListener{
	private Connection conn = DBConn.getConnection();
		
	private String[] getGPS(String raw){
		raw = raw.substring("GeoLocation".length());
		Pattern p=Pattern.compile("\\{|\\}");
		Matcher m=p.matcher(raw);                			
		raw=m.replaceAll("");	
		Pattern p1=Pattern.compile("longitude=|latitude=");
		m=p1.matcher(raw);        			
		String location=m.replaceAll("");
		String[] l = location.split(", ");
		return l;		
	}
	
	@Override
	public void handleEvent(UpdateEvent ue) {
		// TODO Auto-generated method stub
		
		if(conn!=null){
			Status status = ue.getStatus();
			String[] gps = getGPS(""+status.getGeoLocation());
			String id = status.getId()+"";
				
			String sql = "INSERT INTO info (twid, lati, longi) VALUES ("
					+ "'"+ id+"', '"+gps[0]+"', '"+gps[1]+"');";
			//System.out.println(sql);
			DBConn.doInsert(sql, conn);	
		}		
	}

	
	
	@Override
	public void handleEvent(AddSentEvent ase) {
		// TODO Auto-generated method stub
		if (conn!=null){
			String id = ase.getTwitId();
			String sentiment =JsonParser.Parse(ase.getSentiment()) ;
			
			if (sentiment.equals(null))
				return;
			String sql = "INSERT INTO twit_sent(twid, sent) VALUES ("
					+ "'"+id+"', '"+sentiment+"');";
			//System.out.println("sql:"+sql);
			DBConn.doInsert(sql, conn);
		}
	}

	@Override
	public void handleEvent(SNSEvent se) {
		// TODO Auto-generated method stub
		String id = se.getId();
		
		String sentiment = JsonParser.Parse(se.getSentiment());
		if (sentiment.equals(null))
			return;
		
		String sql = "SELECT lati, longi FROM info WHERE twid = '"+id+"';";
		ResultSet rs; 
		Statement stmt = null;
		rs = DBConn.doSelect(sql, conn, stmt);
		String lat ;
		String lng ;
		String strSNS = null;
		try {
			rs.next();
			lat = rs.getString("lati");
			lng = rs.getString("longi");
			strSNS = JsonParser.Build(lat, lng, sentiment);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}finally{
			DBConn.closeResultSet(rs, stmt);
		}
		

		System.out.println(strSNS);
		
		//send the new SNS message to subscribed endpoint
		AmazonSNSClient snsClient = SNSchannel.newSNSClient();
		SNSchannel.sendMsg(snsClient, SNSchannel.topicArn, strSNS);
		
	}

	@Override
	public void handleEvent(StreamEvent sme) {
		// TODO Auto-generated method stub
		System.out.println("------------------------streaming restart-----------------------");
		StreamDaemon input = new StreamDaemon(sme.getSQS(), sme.getURL(), sme.getDataSource());
		input.setDaemon(true);
		input.run();
	}

}
