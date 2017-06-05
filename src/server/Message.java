package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message {

	public static int SERVER_ASK = 0; // servidor consulta
	public static int STATUS_INFO = 1; // servidor actualiza status
	public static int CLIENT_DATA = 2; // cliente actualiza datos
	public static int USR_MSJ = 3; // ciente envia mensaje a otro usuario
	public static int SERVER_FATAL = 4; // error fatal
			
	private int type;
	private String destination;
	private String text;
	private String source;
	
	public Message(String m){
		Gson gson = new GsonBuilder().create();
		copy((Message)gson.fromJson(m, Message.class));
	}
	
	
	public Message(){}
	
	
	public void copy(Message m){
		this.type = m.type;
		this.destination= m.destination;
		this.text = m.text;
		this.source = m.source;
	}
	
	public String getDestination(){
		return this.destination;
	}
	
	public void setDestination(String d){
		this.destination = d;
	}
	
	public int getType(){
		return this.type;
	}
	
	
	public String getText(){
		return this.text;
	}
		
	public void setType(int t){
		this.type = t;
	}
	
	
	public void setText(String m){
			this.text = m;
	}

	@Override
	public String toString() {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(this);
	}
	
	public String getSource(){
		return this.source;
	}

	public void setSource(String s){
		this.source = s;
	}
	
}
