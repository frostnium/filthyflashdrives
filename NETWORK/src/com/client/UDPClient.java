package com.client;


import java.net.*; 
public class UDPClient {    
	public static final String NEXT = "next";
	public static final String PREV = "prev";
	public static final String EXIT = "exit";
	public static final String SSHOW = "slideshow";
	public static final String IMODE = "imode";
	public static final String VMODE = "vmode";
	public static final String PLAY = "play";
	public static final String PAUSE = "pause";

	
	public byte[] sendData;
	public byte[] receiveData;
	public String sentence;
	public DatagramSocket clientSocket;       
	public InetAddress IPAddress;
	
	public UDPClient() throws Exception{
		this.sendData = new byte[1500];
		this.receiveData = new byte[1500];
		this.clientSocket = new DatagramSocket();
		this.IPAddress = InetAddress.getByName("localhost");
		this.sentence = "";
	}
	
	public void send() throws Exception {
		this.sendData = new byte[1500];
		sendData = sentence.getBytes();       
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);       
		clientSocket.send(sendPacket);       
	}
	
	public DatagramPacket receive() throws Exception {
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);       
		clientSocket.receive(receivePacket); 
		this.receiveData = new byte[1500];
		return receivePacket;
	}
	
	public void close() {
		clientSocket.close();
	}

}
