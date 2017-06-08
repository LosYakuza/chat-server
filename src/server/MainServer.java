package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class MainServer extends Thread {

	private ServerSocket server;
	private Logger log;

	
	public MainServer(int port, Logger log) throws IOException {
		ChatHandler.log=log;
		this.log = log;
		server = new ServerSocket(port);
		log.log_normal("Server conectado a "+port);
	}
	
	public void stopRequest(){
		try {
			server.close();
		} catch (IOException e) {
			log.log_all(e.getMessage());
		}
		String[] usrV = (String[]) ChatHandler.clients.keySet().toArray(
				new String[ChatHandler.clients.keySet().size()]);
		for(int i=0;i<usrV.length;i++){
			ChatHandler.kick(usrV[i]);
		}
	}
	
	@Override
	public void run() {
		Socket client;
		ChatHandler.clients = new HashMap<>();
		log.log_normal("Server iniciado");
		try {
			while( (client=this.server.accept()) != null){
				ChatHandler c = new ChatHandler(client);
				c.start();
			}
		} catch (IOException e) {
			log.log_all(e.getMessage());
		}
		log.log_normal("Server detenido");
	}

}
