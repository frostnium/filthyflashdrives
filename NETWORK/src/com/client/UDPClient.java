package com.client;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

import javax.imageio.ImageIO;

import com.mp1.FileType;
import com.mp1.Global; 
public class UDPClient {    
	
	
	public byte[] receiveData;
	public DatagramSocket clientSocket;       
	public InetAddress IPAddress;
	public byte[] imageData;
	public Queue<byte[]> window;	
	public Timer t;
	public Thread clientThread;

	public UDPClient() throws Exception{
		this.window=new ConcurrentLinkedQueue<byte[]>();
		this.receiveData = new byte[1512];
		this.clientSocket = new DatagramSocket();
		this.IPAddress = InetAddress.getByName("localhost");
		this.t = new Timer();
		this.clientThread = Thread.currentThread();
		System.out.println(clientSocket.getSendBufferSize());
		System.out.println(clientSocket.getReceiveBufferSize());

	}
	
	public void sendData(byte[] data) throws IOException{
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9999);
		clientSocket.send(sendPacket);
	}
	
	public void sendCommand(String command) throws Exception {
		byte[] sendData = new byte[0];
		sendData = Global.concat(sendData, command.getBytes());
		this.sendData(sendData);
	}
	
	public synchronized void sendImage(File image) throws IOException {
		byte[] bytes = this.getImageBytes(image);
		byte[] sendData;
		
		int interval = 0;
		int addend = 1500;
		
		int seqNum=0;
		
		do { //TODO: addhere
			if(window.size()<5){
				if(interval==0){
					t = new Timer();
					t.schedule(new TimeoutTask(window, this),Global.TIMEOUT);
				}
				
				byte[] dataChunk = Arrays.copyOfRange(bytes, interval, interval+addend);
				sendData = new byte[0];
			//	sendData = Global.concat(sendData, ByteBuffer.allocate(4).putInt(seqNum+dataChunk.length).array());
				sendData = Global.concat(sendData, ByteBuffer.allocate(4).putInt(seqNum).array());
				sendData = Global.concat(sendData, ByteBuffer.allocate(4).putInt(dataChunk.length).array());
				sendData = Global.concat(sendData, dataChunk);
				window.add(sendData);
				this.sendData(sendData);
				System.out.println("====SENT SEQ: "+seqNum);
				seqNum+=dataChunk.length;
				if(interval + addend > bytes.length) 
					addend = bytes.length - interval;
				else
					interval += addend;
			}
		}while(interval < bytes.length );
		
		
		byte[] head=new byte[0];
		while(!window.isEmpty()){
			if(!head.equals(window.peek())){
				t = new Timer();
				t.schedule(new TimeoutTask(window, this),Global.TIMEOUT);
				head=window.peek();
			}	
		}
		System.out.println("FINAL WINDOW SIZE: "+window.size());
		System.out.println("DONE");
		String message = "TRCOMPLETE"+image.getName();
		sendData = message.getBytes();    
		this.sendData(sendData);
		this.window.clear();
	}
	/*
	public void timeout(){
		Queue<byte[]> winClone=new ConcurrentLinkedQueue<byte[]>(window);
		System.out.println("==PACKET TIMEOUT!");
		while(!winClone.isEmpty()){
			try {
				sendData(winClone.remove());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/
	
	public byte[] getImageBytes(File image) throws IOException { 
		BufferedImage img = ImageIO.read(image);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();        
		ImageIO.write(img, FileType.getExtension(image.getName()), baos);
		baos.flush();
		return baos.toByteArray();
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
