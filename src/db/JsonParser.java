package db;


import twitter4j.JSONException;
import twitter4j.JSONObject;

public class JsonParser {
	
	public static String Parse(String content){
		String sent = null;
		try {
			JSONObject pkg = new JSONObject(content);
			String status = pkg.getString("status");
			
			System.out.println("this is the status"+status);
			if(!status.equals("OK"))
				return null;
						
			JSONObject docSen =pkg.getJSONObject("docSentiment"); 
			
			System.out.println();
			if (docSen.has("score"))
				sent = docSen.getString("score");
			else
				sent = "0";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sent;
	}

	public static String Build(String lat, String lng, String sen){
		
		JSONObject ob = new JSONObject();
		
		try {
			ob.put("lat", lat);
			ob.put("lng", lng);
			ob.put("sentiment", sen);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return ob.toString();
		
	}
	
}
