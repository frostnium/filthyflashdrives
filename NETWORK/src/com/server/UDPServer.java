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
	}
	
	public void sendData() throws IOException{
		
		String filename = frame.images.get(frame.imageIndex).toString();  
		System.out.println(filename+tempIP+port);
		sendData = filename.getBytes();                   
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, tempIP, port);                   		
		serverSocket.send(sendPacket); 
	}
	
	public void sendData(String slideShowStatus) throws IOException{
		System.out.println(slideShowStatus+tempIP+port);
		sendData = slideShowStatus.getBytes();                   
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, tempIP, port);                   		
		serverSocket.send(sendPacket); 
	}
	
	public void initCommand(String command) throws IOException {
		System.out.println("COMMAND: "+command);
		if(command.trim().equals("next")) {
			if(frame.imageIndex == frame.storedImages.size()-1) {
				this.sendData("none");
			}
			else {
				frame.nextImage();
				this.sendData();
			}
		}
		else if(command.trim().equals("prev")) {
			frame.prevImage();
			this.sendData();
		}
		else if(command.trim().equals("slideshow")) {
			frame.displayImage(0);
			frame.imageIndex = 0;
			this.sendData();
		}
	}
}
