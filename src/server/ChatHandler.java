package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

public class ChatHandler extends Thread {

	public static int WAITING_LOGIN = 0;
	public static int LOGGED = 1;

	public static HashMap<String, ChatHandler> clients;

	private Socket s;
	private DataInputStream i;
	private DataOutputStream o;
	private int status;
	private String user;

	/**
	 * Crea instancia y buffers para enviar datos
	 * 
	 * @param s
	 * @throws IOException
	 */
	public ChatHandler(Socket s) throws IOException {
		if(ChatHandler.clients == null){
			ChatHandler.clients = new HashMap<>();
		}
		this.s = s;
		this.i = new DataInputStream(new BufferedInputStream(this.s.getInputStream()));
		this.o = new DataOutputStream(new BufferedOutputStream(this.s.getOutputStream()));
		user = "";
		handshake();
		System.out.println("cliente conectado");
	}

	/**
	 * Espera mensaje del cliente
	 */
	@Override
	public void run() {
		boolean c = true;
		while (c) {
			try {
				Message msg = new Message(i.readUTF());
				System.out.println("IN server: " + msg);
				msg.setSource(this.user);
				process(msg);
			} catch (IOException e) {
				kissoff();
				c=false;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Procesa mensaje recibido
	 * 
	 * @param m
	 * @throws IOException
	 */
	private synchronized void process(Message m) throws IOException {
		if (this.status == WAITING_LOGIN) {
			if (m.getType() == Message.CLIENT_DATA && m.getDestination().equals("user")) {
				login(m);
			} else {
				kissoff(); // cierra conexion a cliente
			}
		} else {
			m.setSource(this.user);
			if (m.getType() == Message.USR_MSJ) {
				broadcast(m);
			} else {
				// salida, o cualquier mensaje de status. Descartado por ahora
			}
		}
	}

	/**
	 * Ejecuta logueo
	 * 
	 * @param m
	 * @throws IOException
	 */
	private void login(Message m) throws IOException {
		this.user = m.getText();
		ChatHandler.clients.put(this.user, this);
		sendClientList();
		System.out.println("logueado "+this.user);
		this.status = ChatHandler.LOGGED;
	}

	/**
	 * Envia lista de clientes a cada uno de ellos
	 * 
	 * @throws IOException
	 */
	private void sendClientList() throws IOException {
		Message m = new Message();
		String ulist = "";
		m.setDestination("clientlist");
		m.setSource("server");
		m.setType(Message.STATUS_INFO);
		Iterator<String> list = ChatHandler.clients.keySet().iterator();
		while (list.hasNext()) {
			ulist = ulist + list.next() + ",";
		}
		if(!ulist.equals("")){
			ulist = ulist.substring(0, ulist.length() - 1);
		}
		m.setText(ulist);
		broadcastAll(m);
	}

	
	private void broadcastAll(Message m){
		Iterator<ChatHandler> listClients = ChatHandler.clients.values().iterator();
		while (listClients.hasNext()) {
			try {
				listClients.next().send(m);
			} catch (IOException e) {
			}
		}
	}
	
	/**
	 * Transmite mensaje a destino
	 * 
	 * @param m
	 * @throws IOException
	 */
	private void broadcast(Message m) throws IOException {
		if(m.getDestination().equals("all")){
			broadcastAll(m);
		}else{
			ChatHandler.clients.get(m.getDestination()).send(m);
		}
	}

	/**
	 * Envia mensaje a this
	 * 
	 * @param m
	 * @throws IOException
	 */
	private void send(Message m) throws IOException {
		System.out.println("enviando:" +m.toString());
		this.o.writeUTF(m.toString());
		this.o.flush();
	}

	/**
	 * Envia request para login
	 * 
	 * @throws IOException
	 */
	private void handshake() throws IOException {
		this.status = WAITING_LOGIN;
		Message m = new Message();
		m.setType(Message.SERVER_ASK);
		m.setText("login");
		send(m);

	}

	/**
	 * Cierra conexion con cliente
	 * @throws IOException 
	 */
	public void kissoff() {
		System.out.println("Cliente desconectado "+this.user);
		try {
			this.s.close();
		} catch (IOException e) {

		}
		if (!this.user.equals("")) {
			ChatHandler.clients.remove(this.user);
		}
		try{
			sendClientList();
		}catch(Exception e){}
	}
}
