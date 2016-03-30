package com.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.SwingWorker;

import com.mp1.Global;

public class ServerBuffer extends Thread{

	private static final int BUFFER_LIMIT = Global.BUFFERLIMIT;
	
	private UDPServer server;
	private Queue<DatagramPacket> buffer;
	private Queue<DatagramPacket> packetReceived;
	private byte[] receiveData;
	
	public ServerBuffer(UDPServer server) {
		this.server=server;
		this.buffer = new LinkedList<DatagramPacket>();
		this.packetReceived = new LinkedList<DatagramPacket>();
	}

	@Override
	public void run() {
		while(true){	
			this.receiveData = new byte[1508];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);                   			
			try {
				server.serverSocket.receive(receivePacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}         
			server.tempIP = receivePacket.getAddress();                   
			server.port = receivePacket.getPort();
			if(server.imageReceivingMode && buffer.size()<=BUFFER_LIMIT)
				buffer.add(receivePacket);	
			else
				packetReceived.add(receivePacket);
				
//			firePropertyChange("receive", null, null);
			server.sendImageData = new byte[1500];
			server.sendData = new byte[1500];
			try {
				server.receive();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
//	@Override
//	protected Void doInBackground() throws Exception {
//		while(true){	
//			this.receiveData = new byte[1508];
//			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);                   			
//			server.serverSocket.receive(receivePacket);         
//			server.tempIP = receivePacket.getAddress();                   
//			server.port = receivePacket.getPort();
//			if(server.imageReceivingMode && buffer.size()<=BUFFER_LIMIT)
//				buffer.add(receivePacket);	
//			else
//				packetReceived.add(receivePacket);
//				
//			firePropertyChange("receive", null, null);
//		}
//		
//	}
	
	public int getBufferSpace(){
		return BUFFER_LIMIT-buffer.size();
	}

	public DatagramPacket getBufferPacket(){
		return buffer.remove();
	}
	
	public DatagramPacket getPacketReceived(){
		return packetReceived.remove();
	}
	
	public int size(){
		return buffer.size();
	}
}
