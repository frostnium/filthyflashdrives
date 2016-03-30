package com.server;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import com.mp1.FileType;
import com.mp1.Global;
import com.mp1.SlideShow;


public class UDPServer implements PropertyChangeListener{   
	
	
	public static final int LOSS_PROBABILITY = Global.SERVER_LOSS_PROBABILITY;
	public static final long DELAY = Global.SERVER_DELAY;
	
	private Random rand;
	
	public static final int IMAGE_MODE = 0;
	public static final int VIDEO_MODE = 1;
	
	public ArrayList<ServerMedia> media;
	public DatagramSocket serverSocket;
	public byte[] receiveData;
	public byte[] sendData;
	
	public byte[] sendImageData;
	private byte[] tempImageData;
	
	public int port;
	public InetAddress tempIP;
	
	private int mediaMode;
	private String receivedFileName;
	boolean imageReceivingMode;
	
	private int ackNum;
	private DatagramPacket ackPacket;
	
	private ServerBuffer buffer;
	
	public UDPServer() throws Exception{
		this.imageReceivingMode = false;
		this.media = new ArrayList<ServerMedia>();
		this.media.add(new ImageViewer());
		this.media.add(new VideoPlayer());
		this.serverSocket = new DatagramSocket(9999);
		this.receiveData = new byte[1508];
		this.sendData = new byte[1500];
		this.sendImageData = new byte[1500];
		this.tempImageData = new byte[0];
		this.mediaMode = UDPServer.IMAGE_MODE;
		this.ackNum=0;
		this.ackPacket=null;
		this.rand = new Random();
		this.buffer=new ServerBuffer(this);
		this.buffer.execute();
		/*
		while(true) { //to remove
			this.receiveData = new byte[1508];
			this.sendData = new byte[1500];
			this.receiveImageData = new byte[1512];
			this.sendImageData = new byte[1500];
			this.receive();
		}*/
	}
	
	
	public void receive() throws Exception{ //TODO DELAY
		
		if(imageReceivingMode) {
			DatagramPacket receivePacket = this.buffer.getBufferPacket();                   			       
			String sentence = new String(receivePacket.getData()).trim(); 
			Thread.sleep(DELAY);
			if(sentence.length()>=10&&sentence.substring(0, 10).equals("TRCOMPLETE")) {
				System.out.println("COMPLETEE");
				imageReceivingMode = false;
				InputStream in = new ByteArrayInputStream(tempImageData);
				BufferedImage bImageFromConvert = null;
				receivedFileName = sentence.substring(10);
				try {
					bImageFromConvert = ImageIO.read(in);
					File outputfile = new File("images/"+receivedFileName);
					ImageIO.write(bImageFromConvert, FileType.getExtension(receivedFileName), outputfile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				ImageViewer iViewer = (ImageViewer) media.get(mediaMode);
				iViewer.refreshImageList();
				in.close();
				tempImageData = new byte[0];
				this.ackNum=0;
			}
			else{
				if(LOSS_PROBABILITY>rand.nextInt(100)&&this.ackNum!=0){ //LOSS PROBABILITY
					Date date= new Date();
					System.out.println("LOST:: SEQ: "+retrieveSeq(receivePacket.getData())+" || "+new Timestamp(date.getTime()));
				}
				else
					this.receiveImageData(receivePacket);
			}
		}
		else{	
			DatagramPacket receivePacket = this.buffer.getPacketReceived();                   			       
			String sentence = new String(receivePacket.getData()).trim(); 
			this.initCommand(sentence);
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
		if(mediaMode == VIDEO_MODE) 
			return;
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
			sendImageData = new byte[0]; 	
			byte[] dataChunk = Arrays.copyOfRange(bytes, interval, interval+addend);
			sendImageData = Global.concat(sendImageData, dataChunk);
			sendPacket = new DatagramPacket(sendImageData, sendImageData.length, tempIP, port);
			serverSocket.send(sendPacket);
			System.out.println("SENT!! "+sendImageData.length);
			if(interval + addend > bytes.length) 
				addend = bytes.length - interval;
			else
				interval += addend;
		}while(interval < bytes.length);
		
		System.out.println("DONE");
		String message = "TRCOMPLETE";
		sendData = message.getBytes();                   
		sendPacket = new DatagramPacket(sendData, sendData.length, tempIP, port);                   		
		serverSocket.send(sendPacket); 
	}
	
	public void receiveImageData(DatagramPacket receivePacket) throws IOException {
		if(retrieveSeq(receivePacket.getData())>this.ackNum){ //receives out of order packet
			System.out.println("out of order");
			serverSocket.send(this.ackPacket);
		}
		else if(retrieveSeq(receivePacket.getData())<this.ackNum){
			serverSocket.send(this.ackPacket); //PREMATURE TIMEOUT
		}
		else{ //receives in order packet
			byte[] imageDataChunk = retrieveData(receivePacket.getData());
			tempImageData = Global.concat(tempImageData, imageDataChunk);
			this.ackPacket=produceAckPacket(receivePacket);
			serverSocket.send(this.ackPacket);
		}
	}
	
	public DatagramPacket produceAckPacket(DatagramPacket receivePacket) throws IOException{
		int seqNum = retrieveSeq(receivePacket.getData());
		System.out.println("RECEIVED PACKET SEQ: "+seqNum);
		int lengthNum = retrieveLength(receivePacket.getData());
		System.out.println("RECEIVED PACKET DATA LENGTH: "+lengthNum);
		this.ackNum = seqNum+lengthNum;
		System.out.println("SEND PACKET ACK: "+ackNum);
		System.out.println("----------------------------------------------------");
		byte[] ackData=new byte[0];
		ackData = Global.concat(ackData, ByteBuffer.allocate(4).putInt(ackNum).array());
		ackData = Global.concat(ackData, ByteBuffer.allocate(4).putInt(this.buffer.getBufferSpace()).array());
		ackData = Global.concat(ackData, new String("ACK").getBytes());
		DatagramPacket sendPacket = new DatagramPacket(ackData, ackData.length, tempIP, port);                   		
		return sendPacket;
	}
	
	private int retrieveSeq(byte[] bytes){
		byte[] seqBytes = Arrays.copyOfRange(bytes, 0, 4);
		int seqNum = ByteBuffer.wrap(seqBytes).getInt();
		return seqNum;
	}
	
	private int retrieveLength(byte[] bytes){
		byte[] lengthBytes = Arrays.copyOfRange(bytes, 4, 8);
		int lengthNum = ByteBuffer.wrap(lengthBytes).getInt();
		return lengthNum;
	}
	
	private byte[] retrieveData(byte[] bytes) {
		return Arrays.copyOfRange(bytes, 8, bytes.length);
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
		if(command.trim().equals("next")) {
			media.get(mediaMode).next();
			if(mediaMode == UDPServer.IMAGE_MODE)
				this.stopSlideshow();
			sendData();
			sendImage();
		}
		else if(command.trim().equals("prev")) {
			media.get(mediaMode).prev();
			if(mediaMode == UDPServer.IMAGE_MODE)
				this.stopSlideshow();
			sendData();
			sendImage();
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
			sendData();
		}
		else if(command.trim().equals("vmode")) {
			if(mediaMode == UDPServer.VIDEO_MODE)
				return;
			this.stopSlideshow();
			this.media.get(mediaMode).setVisible(false);
			this.mediaMode = UDPServer.VIDEO_MODE;
			this.media.get(mediaMode).setVisible(true);
			sendData();
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
			sendData = new byte[1500];
			sendData = message.getBytes();                   
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, tempIP, port);                   		
			serverSocket.send(sendPacket); 
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


	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if(arg0.getPropertyName().equals("receive")){
			this.sendImageData = new byte[1500];
			this.sendData = new byte[1500];
			try {
				this.receive();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
