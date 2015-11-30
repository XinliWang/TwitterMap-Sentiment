package appserver;

import java.util.EventObject;

import com.amazonaws.services.sqs.AmazonSQS;

import db.DataSource;

public class StreamEvent extends EventObject{
	private static final long serialVersionUID = 461704443595101488L;
	
	private AmazonSQS sqs;
	private String queueURL;
	private DataSource source;
	
	public StreamEvent(Object source, AmazonSQS sqs, String queueURL, DataSource s) {
		super(source);
		// TODO Auto-generated constructor stub
		this.source =s;
		this.queueURL = queueURL;
		this.sqs = sqs;
	}
	
	public AmazonSQS getSQS(){
		return this.sqs;
	}
	public DataSource getDataSource(){
		return this.source;
	}
	public String getURL(){
		return this.queueURL;
	}

}
