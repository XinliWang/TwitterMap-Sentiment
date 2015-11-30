package appserver;

import java.util.EventObject;

public class AddSentEvent extends EventObject{
	private static final long serialVersionUID = -4567289475117118779L;
	private String id;
	private String sentiment;
	
	public AddSentEvent(Object source, String id ,String jsondata) {
		super(source);
		// TODO Auto-generated constructor stub
		this.id= id;
		this.sentiment= jsondata;
		
	}
	public String getTwitId(){
		return this.id;
	}
	public String getSentiment(){
		return this.sentiment;
	}	

}
