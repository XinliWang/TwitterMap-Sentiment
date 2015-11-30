package db;

import java.util.EventListener;

import appserver.AddSentEvent;
import appserver.SNSEvent;
import appserver.StreamEvent;

public interface DataListener extends EventListener{
	public void handleEvent(UpdateEvent ue);
	public void handleEvent(AddSentEvent ase);
	public void handleEvent(SNSEvent se);
	public void handleEvent(StreamEvent sme);
}
