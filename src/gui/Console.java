package gui;

import java.io.IOException;

import server.MainServer;

public class Console {

	public static void main(String[] args) throws Exception {
		MainServer s = new MainServer(9090);
		s.start();
		
	}

}
