package db;

import appserver.AddSentEvent;
import appserver.SNSEvent;
import appserver.StreamEvent;

import com.amazonaws.services.sqs.AmazonSQS;

import twitter4j.Status;


public class DataSource {
	private DataUpListener listener;
	
	public void addDataListener(DataUpListener dl){
		this.listener= dl;	
	}
	
	public void notifyUpdateListener(Status status) {		
		UpdateEvent ue = new UpdateEvent(this, status);
		listener.handleEvent(ue);
	}
	public void notifyAddSentListener(String id, String json){
		AddSentEvent ase = new AddSentEvent(this, id, json);
		listener.handleEvent(ase);
	}
	
	public void notifySNSListener(String id, String json){
		SNSEvent se = new SNSEvent(this, id, json);
		listener.handleEvent(se);
	}
	
	
	public void notifyStreamListener(AmazonSQS sqs, String queueURL, DataSource source){
		StreamEvent sme = new StreamEvent(this, sqs, queueURL, source);
		listener.handleEvent(sme);
	}
	
}
