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
	
	public UDPClient() throws Exception{
		this.window=new ConcurrentLinkedQueue<byte[]>();
		this.receiveData = new byte[1512];
		this.clientSocket = new DatagramSocket();
		this.IPAddress = InetAddress.getByName("localhost");
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
	
	public void sendImage(File image) throws IOException {
		byte[] bytes = this.getImageBytes(image);
		byte[] sendData;
		
		int interval = 0, chunksReceived = 0;
		int addend = 1500;
		
		int seqNum=0;
		
		do { //TODO: addhere
			if(window.size()<5){
				byte[] dataChunk = Arrays.copyOfRange(bytes, interval, interval+addend);
				sendData = new byte[0];
			//	sendData = Global.concat(sendData, ByteBuffer.allocate(4).putInt(seqNum+dataChunk.length).array());
				sendData = Global.concat(sendData, ByteBuffer.allocate(4).putInt(seqNum).array());
				sendData = Global.concat(sendData, ByteBuffer.allocate(4).putInt(dataChunk.length).array());
				sendData = Global.concat(sendData, dataChunk);
				window.add(sendData);
				this.sendData(sendData);
				seqNum+=dataChunk.length;
				if(interval + addend > bytes.length) 
					addend = bytes.length - interval;
				else
					interval += addend;
			} else
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}while(interval < bytes.length);
		
		System.out.println("DONE");
		String message = "TRCOMPLETE"+image.getName();
		sendData = message.getBytes();    
		this.window.clear();
		this.sendData(sendData);
	}
	
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
