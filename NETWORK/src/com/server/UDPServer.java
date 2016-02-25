package com.server;

import java.io.*; 
import java.net.*;


public class UDPServer {   
	
	public ServerFrame frame;
	public DatagramSocket serverSocket;
	public byte[] receiveData;
	public byte[] sendData;
	
	public UDPServer() throws Exception{
		this.frame = new ServerFrame();
		this.serverSocket = new DatagramSocket(9999);
		this.receiveData = new byte[1024];
		this.sendData = new byte[1024];
		while(true) {
			this.receive();
		}
	}
	
	
	public void receive() throws Exception{
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);                   			
		serverSocket.receive(receivePacket);                   
		String sentence = new String( receivePacket.getData());                   
		System.out.println("RECEIVED: " + sentence);
		this.initCommand(sentence);
		InetAddress IPAddress = receivePacket.getAddress();                   
		int port = receivePacket.getPort();                   
		String filename = frame.images.get(frame.imageIndex).toString();                   
		sendData = filename.getBytes();                     
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);                   		
		serverSocket.send(sendPacket);      
	}
	
	public void initCommand(String command) {
		System.out.println("COMMAND: "+command);
		if(command.trim().equals("next")) 
			frame.nextImage();
		else if(command.trim().equals("prev"))
			frame.prevImage();
	}
}
