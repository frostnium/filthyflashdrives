package com.client;


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
	
	public UDPClient() throws Exception{
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
	
	public String receive() throws Exception {
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);       
		clientSocket.receive(receivePacket); 
		String data=new String(receivePacket.getData()).trim();
		this.receiveData = new byte[1024];
		return data;
	}
	
	public void close() {
		clientSocket.close();
	}

}
