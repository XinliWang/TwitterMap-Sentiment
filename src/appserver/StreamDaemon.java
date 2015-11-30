package appserver;


import java.util.HashMap;
import java.util.Map;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import config.Global;
import db.DataSource;

public class StreamDaemon extends Thread{
	private AmazonSQS sqs;
	private String queueURL;
	private DataSource source;
	
	public StreamDaemon(AmazonSQS sqs, String queueURL, DataSource source){
		this.sqs=sqs;
		this.queueURL=queueURL;
		this.source = source;
	}
	
	
	@Override
	public void run(){
		ConfigurationBuilder cb = new ConfigurationBuilder();    	 
		//System.out.println(test);
		
		
				
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey(Global.TwitterConsumerKey)
          .setOAuthConsumerSecret(Global.TwitterConsumerSecret)
          .setOAuthAccessToken(Global.TwitterAccessToken)
          .setOAuthAccessTokenSecret(Global.TwitterAccessTokenSecret);
                      
       TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
       
       StatusListener listener = new StatusListener() {
           @Override
           public void onStatus(Status status) {
           	if(status.getGeoLocation()!=null && status.getLang().equals("en")){
           		System.out.println("ID:"+status.getGeoLocation()+ status.getCreatedAt()+ status.getId());
           		
           			String temp =""+status.getGeoLocation();
               		temp = temp.substring("GeoLocation".length());
           			System.out.println("Text:"+ status.getText());
           			System.out.println();
           			
           			// Send message to SQS
           			//sqs.setQueueAttributes
           			SendMessageRequest smr = new SendMessageRequest();
           			smr.setQueueUrl(queueURL);
           			smr.setMessageBody(status.getText());
           			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
           			messageAttributes.put("twit_id", 
           					new MessageAttributeValue()
           						.withDataType("String")
           						.withStringValue(status.getId()+""));
           			smr.withMessageAttributes(messageAttributes);
           			
           			sqs.sendMessage(smr);
           			
           			// make a notification to DB
           			source.notifyUpdateListener(status);
           		}
           }
                       
           @Override
           public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
               //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
           }

           @Override
           public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
               //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
           }

           @Override
           public void onScrubGeo(long userId, long upToStatusId) {
               //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
           }

           @Override
           public void onStallWarning(StallWarning warning) {
               //System.out.println("Got stall warning:" + warning);
           }

           @Override
           public void onException(Exception ex) {
               ex.printStackTrace();
               try {
				Thread.currentThread().join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           }
       };
       twitterStream.addListener(listener);
       twitterStream.sample();
	}
}
