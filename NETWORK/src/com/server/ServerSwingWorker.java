package com.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import com.mp1.FileType;

public class ServerSwingWorker extends SwingWorker<Void, Void> {

	private UDPServer server;
	
	public ServerSwingWorker(UDPServer server) {
		this.server=server;
	}
	@Override
	protected Void doInBackground() throws Exception {
		while(true){
			DatagramPacket receivePacket = new DatagramPacket(server.receiveData, server.receiveData.length);                   			
			server.serverSocket.receive(receivePacket);                   
			String sentence = new String( receivePacket.getData()).trim();                   
			server.tempIP = receivePacket.getAddress();                   
			server.port = receivePacket.getPort(); 
			
			if(server.imageReceivingMode) {
				if(sentence.substring(0, 10).equals("TRCOMPLETE")) {
					System.out.println("COMPLETE");
					server.imageReceivingMode = false;
					InputStream in = new ByteArrayInputStream(server.tempImageData);
					BufferedImage bImageFromConvert = null;
					server.receivedFileName = sentence.substring(10);
					try {
						bImageFromConvert = ImageIO.read(in);
						File outputfile = new File("images/"+server.receivedFileName);
						ImageIO.write(bImageFromConvert, FileType.getExtension(server.receivedFileName), outputfile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ImageViewer iViewer = (ImageViewer) server.media.get(server.mediaMode);
					iViewer.refreshImageList();
				}
				else
					server.receiveImageData(receivePacket);
			}
			else		
				server.initCommand(sentence);
			
		}

	}

}
