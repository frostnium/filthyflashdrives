package com.client;

import javax.swing.SwingWorker;

public class ClientSwingWorker extends SwingWorker<Void, Void> {

	private UDPClient client;
	private byte[] tempData;
	
	public ClientSwingWorker(UDPClient client){
		this.client=client;
		this.tempData = new byte[0];
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		String fileName = null;
		String ssInterval = null;
		while(true){
			DatagramPacket receivePacket = client.receive();
			String data = new String(receivePacket.getData()).trim();
			if(data.trim().equals("GOUPLOAD")) {
				firePropertyChange("goupload", fileName, data);
			}
			else if(data.trim().equals("RFINT"))
				firePropertyChange("ssInterval", ssInterval, data);
			else if(data.trim().substring(0, 9).equals("FILENAME:"))
				firePropertyChange("fileName", fileName, data);
			else if(data.trim().equals("TRCOMPLETE")) {
				client.imageData = tempData;
				tempData = new byte[0];
				System.out.println("COMPLETE");
				firePropertyChange("complete", fileName, data);
			}
			else {
				tempData = this.concat(tempData, receivePacket.getData());
				System.out.println("received packet length: "+tempData.length);
			}
		}
		
	}
}
