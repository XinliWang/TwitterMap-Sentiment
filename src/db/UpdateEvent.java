package db;

import java.util.EventObject;

import twitter4j.Status;

public class UpdateEvent extends EventObject{
	private static final long serialVersionUID = -6233686659158970497L;
	Status status;
	
	public UpdateEvent(Object source, Status status) {
		super(source);
		// TODO Auto-generated constructor stub
		this.status = status;
	}
	public Status getStatus(){
		return this.status;
	}

}
