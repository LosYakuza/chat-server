package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class MainServer extends ServerSocket {

	HashMap<String, Thread> clients;	
	
	public MainServer(int port) throws IOException {
		super(port);
		
	}
	
	public void start(){
		Socket client;
		try {
			while( (client=this.accept()) != null){
				ChatHandler c = new ChatHandler(client);
				c.start();
			}
		} catch (IOException e) {
			// server stopeado
		}
	}

}
