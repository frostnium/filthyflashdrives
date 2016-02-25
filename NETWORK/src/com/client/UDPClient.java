package com.client;


import java.io.*; 
import java.net.*; 
public class UDPClient {    
	public static final String NEXT = "next";
	public static final String PREV = "prev";
	public static final String EXIT = "exit";
	public static final String SSHOW = "slideshow";

	
	public byte[] sendData;
	public byte[] receiveData;
	public String sentence;
	public DatagramSocket clientSocket;       
	public InetAddress IPAddress;
	private String fileName;
	private ClientFrame frame;
	
	public UDPClient(ClientFrame frame) throws Exception{
		this.frame=frame;
		this.sendData = new byte[1024];
		this.receiveData = new byte[1024];
		this.clientSocket = new DatagramSocket();
		this.IPAddress = InetAddress.getByName("localhost");
		this.sentence = "";
	}
	
	public void send() throws Exception {
		sendData = sentence.getBytes();       
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);       
		clientSocket.send(sendPacket);       
	}
	
	public void receive() throws Exception {
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);       
		clientSocket.receive(receivePacket);       
		setFileName(new String(receivePacket.getData()));  
		System.out.println("FROM SERVER: " + fileName);   
		this.receiveData = new byte[1024];
	}
	
	public boolean receiveSShow() throws Exception {
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);       
		clientSocket.receive(receivePacket); 
		String fileName=new String(receivePacket.getData()).trim();
		String status=fileName.substring(0, 1);
		fileName=fileName.substring(1);
		setFileName(fileName);  
		System.out.println("FROM SERVER: " + fileName);   
		this.receiveData = new byte[1024];
		if(status.equals("O"))
			return false;
		else
			return true;
	}
	
	public void close() {
		clientSocket.close();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		frame.fileName.setText("FILE NAME: "+getFileName());
		frame.repaint();
	}
}
