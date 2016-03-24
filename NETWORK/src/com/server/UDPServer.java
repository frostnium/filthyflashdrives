package com.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import com.mp1.FileType;
import com.mp1.Global;
import com.mp1.SlideShow;


public class UDPServer {   
	
	public static final int IMAGE_MODE = 0;
	public static final int VIDEO_MODE = 1;
	
	public ArrayList<ServerMedia> media;
	public DatagramSocket serverSocket;
	public byte[] receiveData;

	public byte[] receiveImageData;
	byte[] tempImageData;
	
	int port;
	InetAddress tempIP;
	
	int mediaMode;
	String receivedFileName;
	boolean imageReceivingMode;
	
	private int seqNum;
	private int ackNum;
	
	private ServerThread receiverThread;
	
	public UDPServer() throws Exception{
		this.imageReceivingMode = false;
		this.media = new ArrayList<ServerMedia>();
		this.media.add(new ImageViewer());
		this.media.add(new VideoPlayer());
		this.serverSocket = new DatagramSocket(9999);
		this.receiveData = new byte[1512];
		this.receiveImageData = new byte[1512];
		this.tempImageData = new byte[0];
		this.receiverThread = new ServerThread(this);
		this.mediaMode = UDPServer.IMAGE_MODE;
		
		this.seqNum=0;
		this.ackNum=0;
		
		this.receiverThread.run();
	}
	
	public void sendData(byte[] data) throws IOException{
		data = Global.concat(ByteBuffer.allocate(4).putInt(ackNum).array(), data);
		data = Global.concat(ByteBuffer.allocate(4).putInt(seqNum).array(), data);
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, tempIP,port);
		serverSocket.send(sendPacket);
	}
	
	public void sendFileName() throws IOException{
		File file = media.get(mediaMode).mediaFiles[media.get(mediaMode).mediaIndex];
		String filename = "FILENAME:"+file.getName();
		System.out.println(filename+tempIP+port);
		byte[] sendData = filename.getBytes(); 
		sendData(sendData);
	}
	
	public void sendImage() throws IOException{
		if(mediaMode == VIDEO_MODE) 
			return;
		File imageFile = media.get(mediaMode).mediaFiles[media.get(mediaMode).mediaIndex];
		BufferedImage img = ImageIO.read(imageFile);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();        
		ImageIO.write(img, FileType.getExtension(imageFile.getName()), baos);
		baos.flush();
		byte[] imgBytes = baos.toByteArray();
		int interval = 0,addend = 1500;
		int chunksReceived=0;
		do {
			byte[] sendImageData = new byte[0];
			/*
			sendImageData = Global.concat(sendImageData, ByteBuffer.allocate(4).putInt(ackNum).array());
			sendImageData = Global.concat(sendImageData, ByteBuffer.allocate(4).putInt(seqNum).array());
			*/
			byte[] dataChunk = Arrays.copyOfRange(imgBytes, interval, interval+addend);
			sendImageData = Global.concat(sendImageData, ByteBuffer.allocate(4).putInt(dataChunk.length).array());
			sendImageData = Global.concat(sendImageData, dataChunk);
			sendData(sendImageData);
			System.out.println("SENT!! "+sendImageData.length);
			chunksReceived++;
			if(interval + addend > imgBytes.length) 
				addend = imgBytes.length - interval;
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
		}while(interval < imgBytes.length);
		
		System.out.println("DONE"); //TODO: remove this
		String message = "TRCOMPLETE";
		byte[] sendData = message.getBytes();                   
		sendData(sendData);
	}
	
	public void receiveImageData(DatagramPacket receivePacket) {
		byte[] imageDataChunk = this.parseBytes(receivePacket.getData());
		tempImageData = Global.concat(tempImageData, imageDataChunk);
	}
	
	public byte[] parseBytes(byte[] bytes) {
		byte[] ackBytes = Arrays.copyOfRange(bytes, 0, 4);
		byte[] seqBytes = Arrays.copyOfRange(bytes, 4, 8);
		byte[] lengthBytes = Arrays.copyOfRange(bytes, 8, 12);
		int ackNum = ByteBuffer.wrap(ackBytes).getInt();
		System.out.println("PACKET ACK: "+ackNum);
		int seqNum = ByteBuffer.wrap(seqBytes).getInt();
		System.out.println("PACKET SEQ: "+seqNum);
		int lengthNum = ByteBuffer.wrap(lengthBytes).getInt();
		System.out.println("PACKET DATA LENGTH: "+lengthNum);
		return Arrays.copyOfRange(bytes, 12, bytes.length);
	}
	
	public int reqandgetInterval() throws IOException {
		this.receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		
		String message = "RFINT";
		sendData = message.getBytes();                   
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, tempIP, port);                   		
		serverSocket.send(sendPacket); 
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);                   			
		serverSocket.receive(receivePacket);  
		String interval = new String(receivePacket.getData()).trim();
		
		if(interval.equals(""))
			return 1000;
		else
			return Integer.parseInt(interval);
	}
	
	public void initCommand(String command) throws IOException {
		if(command.trim().equals("next")) {
			media.get(mediaMode).next();
			if(mediaMode == UDPServer.IMAGE_MODE)
				this.stopSlideshow();
			sendFileName();
			sendImage();
		}
		else if(command.trim().equals("prev")) {
			media.get(mediaMode).prev();
			if(mediaMode == UDPServer.IMAGE_MODE)
				this.stopSlideshow();
			sendFileName();
			sendImage();
		}
		else if(command.trim().equals("slideshow")) {
			ImageViewer iViewer = (ImageViewer) media.get(mediaMode);
			if(iViewer.getSshow() != null && iViewer.getSshow().timer.isRunning())
				return;
			int interval = this.reqandgetInterval();
			iViewer.displayIndex(0);
			iViewer.mediaIndex = 0;
			sendFileName();
			iViewer.setSshow(new SlideShow(iViewer,interval,this));
		}
		else if(command.trim().equals("imode")) {
			if(mediaMode == UDPServer.IMAGE_MODE)
				return;
			this.stopVideo();
			this.media.get(mediaMode).setVisible(false);
			this.mediaMode = UDPServer.IMAGE_MODE;
			this.media.get(mediaMode).setVisible(true);
			sendFileName();
		}
		else if(command.trim().equals("vmode")) {
			if(mediaMode == UDPServer.VIDEO_MODE)
				return;
			this.stopSlideshow();
			this.media.get(mediaMode).setVisible(false);
			this.mediaMode = UDPServer.VIDEO_MODE;
			this.media.get(mediaMode).setVisible(true);
			sendFileName();
		}
		else if(command.trim().equals("play")) {
			VideoPlayer vPlayer = (VideoPlayer) media.get(mediaMode);
			vPlayer.play();
		}
		else if(command.trim().equals("pause")) {
			VideoPlayer vPlayer = (VideoPlayer) media.get(mediaMode);
			vPlayer.pause();
		}
		else if(command.trim().equals("exit")) {
			if(mediaMode == UDPServer.IMAGE_MODE)
				this.stopSlideshow();
			else
				this.stopVideo();
		}
		else if(command.trim().equals("startupload")) {
			String message = "GOUPLOAD";
			System.out.println(message);
			byte[] sendData = new byte[1500];
			sendData = message.getBytes();                   
			sendData(sendData);                  		
			this.imageReceivingMode = true;
		}
	}
	
	public void stopSlideshow() {
		ImageViewer iViewer = (ImageViewer) media.get(mediaMode);
		if(iViewer.getSshow() != null && iViewer.getSshow().timer.isRunning())
			iViewer.getSshow().timer.stop();
	}
	
	public void stopVideo() {
		VideoPlayer vPlayer = (VideoPlayer) media.get(mediaMode);
		vPlayer.stop();
	}
	
}
