package com.server;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import com.mp1.SlideShow;


public class UDPServer {   
	
	public static final int IMAGE_MODE = 0;
	public static final int VIDEO_MODE = 1;
	
	public ArrayList<ServerMedia> media;
	public DatagramSocket serverSocket;
	public byte[] receiveData;
	public byte[] sendData;
	
	public byte[] sendImageData;
	public byte[] receiveImageData;
	
	private int port;
	private InetAddress tempIP;
	
	private int mediaMode;
	
	public UDPServer() throws Exception{
		this.media = new ArrayList<ServerMedia>();
		this.media.add(new ImageViewer());
		this.media.add(new VideoPlayer());
		this.serverSocket = new DatagramSocket(9999);
		this.receiveData = new byte[1024];
		this.sendData = new byte[1024];
		this.receiveImageData = new byte[1500];
		this.sendImageData = new byte[1500];
		this.mediaMode = UDPServer.IMAGE_MODE;
		while(true) {
			this.receiveData = new byte[1024];
			this.sendData = new byte[1024];
			this.receiveImageData = new byte[1500];
			this.sendImageData = new byte[1500];
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
		if(!sentence.equals("slideshow") || !sentence.equals("exit")) {
			sendData();
			sendImage();
		}
	}
	
	public void sendData() throws IOException{
		File file = media.get(mediaMode).mediaFiles[media.get(mediaMode).mediaIndex];
		String filename = "FILENAME:"+file.getName();
		System.out.println(filename+tempIP+port);
		sendData = filename.getBytes();                   
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, tempIP, port);                   		
		serverSocket.send(sendPacket); 
	}
	
	public void sendImage() throws IOException {
		File imageFile = media.get(mediaMode).mediaFiles[media.get(mediaMode).mediaIndex];
		BufferedImage img = ImageIO.read(imageFile);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();        
		ImageIO.write(img, FileType.getExtension(imageFile.getName()), baos);
		baos.flush();
		byte[] bytes = baos.toByteArray();
		int interval = 0;
		int addend = 1500;
		DatagramPacket sendPacket;
		System.out.println("IMAGE LENgTH: "+bytes.length);
		do {
			sendImageData = new byte[1500];
			sendImageData = Arrays.copyOfRange(bytes, interval, interval+addend);
			sendPacket = new DatagramPacket(sendImageData, sendImageData.length, tempIP, port);
			serverSocket.send(sendPacket);
			if(interval + addend > bytes.length) 
				addend = bytes.length - interval;
			else
				interval += addend;
			
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);                   			
			serverSocket.receive(receivePacket);
			String ack = new String(receivePacket.getData()).trim();
			System.out.println(ack+" for interval:"+interval);
		}while(interval < bytes.length);
		
		System.out.println("DONE");
		String message = "TRCOMPLETE";
		sendData = message.getBytes();                   
		sendPacket = new DatagramPacket(sendData, sendData.length, tempIP, port);                   		
		serverSocket.send(sendPacket); 
	}
	
	
	public int reqandgetInterval() throws IOException {
		this.receiveData = new byte[1024];
		this.sendData = new byte[1024];
		
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
		System.out.println("COMMAND: "+command);
		if(command.trim().equals("next")) {
			media.get(mediaMode).next();
			if(mediaMode == UDPServer.IMAGE_MODE)
				this.stopSlideshow();
		}
		else if(command.trim().equals("prev")) {
			media.get(mediaMode).prev();
			if(mediaMode == UDPServer.IMAGE_MODE)
				this.stopSlideshow();
		}
		else if(command.trim().equals("slideshow")) {
			ImageViewer iViewer = (ImageViewer) media.get(mediaMode);
			if(iViewer.getSshow() != null && iViewer.getSshow().timer.isRunning())
				return;
			int interval = this.reqandgetInterval();
			iViewer.displayIndex(0);
			iViewer.mediaIndex = 0;
			this.sendData();
			iViewer.setSshow(new SlideShow(iViewer,interval,this));
		}
		else if(command.trim().equals("imode")) {
			if(mediaMode == UDPServer.IMAGE_MODE)
				return;
			this.stopVideo();
			this.media.get(mediaMode).setVisible(false);
			this.mediaMode = UDPServer.IMAGE_MODE;
			this.media.get(mediaMode).setVisible(true);

		}
		else if(command.trim().equals("vmode")) {
			if(mediaMode == UDPServer.VIDEO_MODE)
				return;
			this.stopSlideshow();
			this.media.get(mediaMode).setVisible(false);
			this.mediaMode = UDPServer.VIDEO_MODE;
			this.media.get(mediaMode).setVisible(true);
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
