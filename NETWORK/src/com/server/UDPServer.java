package com.server;

import java.io.*; 
import java.net.*;

import com.mp1.SlideShow;


public class UDPServer {   
	
	public ServerFrame frame;
	public DatagramSocket serverSocket;
	public byte[] receiveData;
	public byte[] sendData;
	
	private int port;
	private InetAddress tempIP;
	
	public UDPServer() throws Exception{
		this.frame = new ServerFrame();
		this.serverSocket = new DatagramSocket(9999);
		this.receiveData = new byte[1024];
		this.sendData = new byte[1024];
		while(true) {
			this.receiveData = new byte[1024];
			this.sendData = new byte[1024];
			this.receive();
		}
	}
	
	
	public void receive() throws Exception{
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);                   			
		serverSocket.receive(receivePacket);                   
		String sentence = new String( receivePacket.getData()).trim();                   
		System.out.println("RECEIVED: " + sentence);
		tempIP = receivePacket.getAddress();                   
		port = receivePacket.getPort(); 
		this.initCommand(sentence);
		if(!sentence.equals("slideshow"))
			sendData();
	}
	
	public void sendData() throws IOException{
		
		String filename = frame.images.get(frame.imageIndex).toString();  
		System.out.println(filename+tempIP+port);
		sendData = filename.getBytes();                   
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, tempIP, port);                   		
		serverSocket.send(sendPacket); 
	}
	
	public void initCommand(String command) throws IOException {
		System.out.println("COMMAND: "+command);
		if(command.trim().equals("next")) {
			if(frame.getSshow() != null && frame.getSshow().isActive) {
				System.out.println("STOP SS NEXT press");
				frame.getSshow().timer.stop();
			}
			frame.nextImage();
		}
		else if(command.trim().equals("prev")) {
			if(frame.getSshow() != null && frame.getSshow().isActive) {
				System.out.println("STOP SS PREV press");
				frame.getSshow().timer.stop();
			}
			frame.prevImage();
		}
		else if(command.trim().equals("slideshow")) {
			frame.displayImage(0);
			frame.imageIndex = 0;
			frame.setSshow(new SlideShow(frame,2000,this));
		}
	}
}
