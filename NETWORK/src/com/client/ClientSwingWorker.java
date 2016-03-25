package com.client;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.swing.SwingWorker;

import com.mp1.Global;

public class ClientSwingWorker extends SwingWorker<Void, Void> {

	private UDPClient client;
	private byte[] tempData;
	private int latestAckNum;
	private int dupAckCount;
	
	public ClientSwingWorker(UDPClient client){
		this.client=client;
		this.tempData = new byte[0];
		this.latestAckNum=0;
		this.dupAckCount=0;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		String fileName = null;
		boolean isGibberish;
		while(true){
			System.out.println("-----------------------------");
			System.out.println("waiting for packet");
			DatagramPacket receivePacket = client.receive();
			String data = new String(receivePacket.getData()).trim();
			if(data.length() > 256) {
				isGibberish = true;
				System.out.println("image packet");
			}
			else {
				isGibberish = false;
				System.out.println("string packet");
			}
			if((new String(parseBytes(receivePacket.getData())).trim()).equals("ACK")){
				byte[] ackBytes = Arrays.copyOfRange(receivePacket.getData(), 0, 4);
				int ackNum=ByteBuffer.wrap(ackBytes).getInt();
				if(ackNum>latestAckNum){
					this.latestAckNum=ackNum;
					this.dupAckCount=0;
					System.out.println("PACKET ACK: "+ackNum);
					System.out.println("WINDOW SIZE: "+client.window.size());
					if(!client.window.isEmpty()) {
						byte[] seqBytes = Arrays.copyOfRange(client.window.peek(), 0, 4);	
						byte[] lengthBytes = Arrays.copyOfRange(client.window.peek(), 4, 8);	
						int seqNum = ByteBuffer.wrap(seqBytes).getInt();	
						int lengthNum = ByteBuffer.wrap(lengthBytes).getInt();
		
						if(seqNum+lengthNum==ackNum){	
							System.out.println("WINDOW SHIFTED");
							client.window.remove();	
							
						}
					}
				}
				else{
					this.dupAckCount++;
					System.out.println("DuplicateAckCount :"+dupAckCount);
					if(dupAckCount>3){ //TODO change this value for fast retransmit
						client.sendData(client.window.peek()); 
						this.dupAckCount=0;
						System.out.println("retransmit finish");
					}
				}
			}
			else if(!isGibberish) {
				if(data.trim().equals("GOUPLOAD"))  {
					this.latestAckNum=0;
					firePropertyChange("goupload", null, null);
				}
				else if(data.trim().equals("RFINT"))
					firePropertyChange("ssInterval", null, null);
				else if(data.trim().substring(0, 9).equals("FILENAME:"))
					firePropertyChange("fileName", fileName, data);
				else if(data.trim().equals("TRCOMPLETE")) {
					client.imageData = tempData;
					tempData = new byte[0];
					System.out.println("COMPLETE");
					firePropertyChange("complete", null, null);
				}
			}
			else {
				byte[] imageDataChunk = receivePacket.getData();
				tempData = Global.concat(tempData, imageDataChunk);
			}
		
		}
		
	}
	
	public byte[] parseBytes(byte[] bytes) {
		/*byte[] ackBytes = Arrays.copyOfRange(bytes, 0, 4);
		byte[] seqBytes = Arrays.copyOfRange(bytes, 4, 8);
		byte[] lengthBytes = Arrays.copyOfRange(bytes, 8, 12);
		int ackNum = ByteBuffer.wrap(ackBytes).getInt();
		System.out.println("PACKET ACK: "+ackNum);
		int seqNum = ByteBuffer.wrap(seqBytes).getInt();
		System.out.println("PACKET SEQ: "+seqNum);
		int lengthNum = ByteBuffer.wrap(lengthBytes).getInt();
		System.out.println("PACKET DATA LENGTH: "+lengthNum);*/
		return Arrays.copyOfRange(bytes, 4, bytes.length);
	}
}
