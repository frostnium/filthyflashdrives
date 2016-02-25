package com.client;


import java.io.*; 
import java.net.*; 
public class UDPClient {    
	public static final String NEXT = "next";
	public static final String PREV = "prev";
	public static final String EXIT = "exit";
	
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
	
	public void receive() throws Exception {
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);       
		clientSocket.receive(receivePacket);       
		String modifiedSentence = new String(receivePacket.getData());  
		System.out.println("FROM SERVER:" + modifiedSentence);   

	}
	
	public void close() {
		clientSocket.close();
	}
}
