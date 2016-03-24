package com.client;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.mp1.FileType;
import com.mp1.Global; 
public class UDPClient {    
	public static final String NEXT = "next";
	public static final String PREV = "prev";
	public static final String EXIT = "exit";
	public static final String SSHOW = "slideshow";
	public static final String IMODE = "imode";
	public static final String VMODE = "vmode";
	public static final String PLAY = "play";
	public static final String PAUSE = "pause";
	public static final String STARTUPLOAD = "startupload";

	
	public byte[] sendData;
	public byte[] receiveData;
	public String sentence;
	public DatagramSocket clientSocket;       
	public InetAddress IPAddress;
	public byte[] imageData;
		
	public UDPClient() throws Exception{
		this.sendData = new byte[1512];
		this.receiveData = new byte[1512];
		this.clientSocket = new DatagramSocket();
		this.IPAddress = InetAddress.getByName("localhost");
		this.sentence = "";
		System.out.println(clientSocket.getSendBufferSize());
		System.out.println(clientSocket.getReceiveBufferSize());

	}
	
	public void send() throws Exception {
		this.sendData = new byte[1512];
		sendData = sentence.getBytes();       
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);       
		clientSocket.send(sendPacket);       
	}
	
	public void sendImage(File image) throws IOException {
		BufferedImage img = ImageIO.read(image);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();        
		ImageIO.write(img, FileType.getExtension(image.getName()), baos);
		baos.flush();
		byte[] bytes = baos.toByteArray();
		int interval = 0;
		int addend = 1500;
		DatagramPacket sendPacket;
		System.out.println("IMAGE LENgTH: "+bytes.length);
		int chunksReceived = 0;
		int seqNum = 0;
		int ackNum = 0;
		do {
			sendData = new byte[0];
			byte[] dataChunk = Arrays.copyOfRange(bytes, interval, interval+addend);
			ackNum = seqNum+dataChunk.length;
			sendData = Global.concat(sendData, ByteBuffer.allocate(4).putInt(ackNum).array());
			sendData = Global.concat(sendData, ByteBuffer.allocate(4).putInt(seqNum).array());
			byte[] dataChunk = Arrays.copyOfRange(bytes, interval, interval+addend);
			sendData = Global.concat(sendData, ByteBuffer.allocate(4).putInt(dataChunk.length).array());
			sendData = Global.concat(sendData, dataChunk);
			seqNum++;
			ackNum++;
			sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);
			clientSocket.send(sendPacket);
			System.out.println("SENT! "+sendData.length);
			chunksReceived++;
			if(interval + addend > bytes.length) 
				addend = bytes.length - interval;
			else
				interval += addend;
			if(chunksReceived==Global.nChunksBeforeBuffer){
				try {
					Thread.sleep(Global.millsToBuffer);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				chunksReceived=0;
			}
			System.out.println("INTERVAL: "+interval);
		}while(interval < bytes.length);
		
		System.out.println("DONE");
		String message = "TRCOMPLETE"+image.getName();
		sendData = message.getBytes();                   
		sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);                   		
		clientSocket.send(sendPacket); 
	}
	
	public DatagramPacket receive() throws Exception {
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);       
		clientSocket.receive(receivePacket); 
		this.receiveData = new byte[1512];
		return receivePacket;
	}
	
	public void close() {
		clientSocket.close();
	}

}
