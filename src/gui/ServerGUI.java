package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import server.ChatHandler;
import server.Logger;
import server.MainServer;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class ServerGUI implements Logger {

	private JFrame frmChatServer;
	private JTextField txtPort;
	private JTextArea txtLog;
	private JComboBox cmbUsrs;
	
	private MainServer server;
	private int loglevel;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI window = new ServerGUI();
					window.frmChatServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void writeLog(String s){
		txtLog.setCaretPosition(txtLog.getText().length());
		txtLog.append(s + "\n");
		
	}
	
	public void setLogLevel(int ll){
		this.loglevel = ll;
	}
	
	public void log_normal(String s){
		writeLog(s);
	}
	
	public void log_debug(String s){
		if(this.loglevel>=ServerGUI.LOGLEVEL_DEBUG)
			writeLog(s);
	}
	
	public void log_all(String s){
		if(this.loglevel>=ServerGUI.LOGLEVEL_ALL)
			writeLog(s);
	}
	
	
	/**
	 * Create the application.
	 */
	public ServerGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChatServer = new JFrame();
		frmChatServer.setTitle("Chat Server");
		frmChatServer.setResizable(false);
		frmChatServer.setBounds(0, 0, 420, 550);
		frmChatServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmChatServer.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 420, 400);
		frmChatServer.getContentPane().add(scrollPane);
		
		txtLog = new JTextArea();
		scrollPane.setViewportView(txtLog);
		
		txtPort = new JTextField();
		txtPort.setText("9090");
		txtPort.setBounds(81, 426, 50, 19);
		frmChatServer.getContentPane().add(txtPort);
		txtPort.setColumns(10);
		
		JLabel lblPuerto = new JLabel("Puerto");
		lblPuerto.setBounds(12, 428, 70, 15);
		frmChatServer.getContentPane().add(lblPuerto);
		
		JButton btnStart = new JButton("Iniciar");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				startServer();
			}
		});
		btnStart.setBounds(20, 463, 117, 25);
		frmChatServer.getContentPane().add(btnStart);
		
		JButton btnStop = new JButton("Parar");
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(server != null)
					server.stopRequest();
			}
		});
		btnStop.setBounds(152, 463, 117, 25);
		frmChatServer.getContentPane().add(btnStop);
		
		cmbUsrs = new JComboBox();
		cmbUsrs.setBounds(152, 423, 115, 24);
		frmChatServer.getContentPane().add(cmbUsrs);
		
		JButton btnKick = new JButton("Eliminar");
		btnKick.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ChatHandler.kick(cmbUsrs.getSelectedItem().toString());
			}
		});
		btnKick.setBounds(283, 423, 117, 25);
		frmChatServer.getContentPane().add(btnKick);
	}
	
	private void startServer(){
		this.setLogLevel(Logger.LOGLEVEL_ALL);
		try {
			server = new MainServer(Integer.parseInt(txtPort.getText()),this);
			server.start();
		} catch (IOException e) {
			this.log_normal(e.getMessage());
		}
		
	}

	@Override
	public void userConnected(String s) {
		cmbUsrs.addItem(s);
		
	}

	@Override
	public void userDisConnected(String s) {
		try{
			cmbUsrs.removeItem(s);
		}catch(Exception e){}
	}
}
