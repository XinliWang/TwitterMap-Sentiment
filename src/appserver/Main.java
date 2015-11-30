package appserver;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import config.DataBase;
import db.DBConn;
import db.DataCleaner;
import db.DataSource;
import db.DataUpListener;

public class Main {
	
	private static AmazonSQS sqs;
	private static String queueURL = "";
	
	private static class Holder{
		private static final Main main = new Main();
	}
	
	public static Main getInstance(){
		return Holder.main;
	}
	
	
	public void execute(){
		// TODO Auto-generated method stub
		checkTableExist();
		System.out.println("running first");
		
		AWSCredentials credentials = null;
        try {
            credentials = new PropertiesCredentials(
              Main.class.getResourceAsStream("AwsCredentials.properties"));
        } catch (IOException e2) {
        // TODO Auto-generated catch block
            e2.printStackTrace();
        }
		sqs = new AmazonSQSClient(credentials);
		
		DataSource datasource = new DataSource();
		DataUpListener listener = new DataUpListener();
		datasource.addDataListener(listener);
		
		StreamDaemon input = new StreamDaemon(sqs, queueURL, datasource);
		input.setDaemon(true);
		input.run();
		
		
		Connection con = DBConn.getConnection();
		
		//set timer to delete tweets from database
		Timer t = new Timer();
        t.schedule(new DataCleaner(con), 60*1000);
		       
		ExecutorService e = Executors.newFixedThreadPool(15);
        
		while(true){
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueURL);
			//Message m = sqsClient.receiveMessage(receiveMessageRequest);
			
			List<Message> messages  = sqs.receiveMessage(receiveMessageRequest
					.withMessageAttributeNames("twit_id"))
						.getMessages();
			if(messages.isEmpty()){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else{
				Future<?> task = e.submit(new Worker(messages,  queueURL,  sqs, datasource));
				e.execute(new Helper(task, queueURL,  sqs, messages));				
			}
			
		}
		
	}
	private void checkTableExist(){
		try {
			DataBase dbBase = new DataBase();
			dbBase.checkAndCreateTable();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
