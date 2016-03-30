package com.client;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.Timer;

import javax.swing.SwingWorker;

import com.mp1.Global;

public class ClientSwingWorker extends SwingWorker<Void, Void> {

	public static final int LOSS_PROBABILITY = Global.CLIENT_LOSS_PROBABILITY;
	public static final long DELAY = Global.CLIENT_DELAY;
	
	private Random rand;
	
	private UDPClient client;
	private byte[] tempData;
	private int latestAckNum;
	private int dupAckCount;
	
	public ClientSwingWorker(UDPClient client){
		this.rand = new Random();
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
				byte[] rcvBufferBytes = Arrays.copyOfRange(receivePacket.getData(), 4, 8);
				int ackNum=ByteBuffer.wrap(ackBytes).getInt();
				int rcvBuffer = ByteBuffer.wrap(rcvBufferBytes).getInt();
				if(rcvBuffer==0){
					System.out.println("CLIENT SLEEP");
					client.clientThread.sleep(DELAY);
				}
				if(LOSS_PROBABILITY>rand.nextInt(100)){
					Date date= new Date();
					System.out.println("LOST:: ACK: "+ackNum+" || "+new Timestamp(date.getTime()));
				}
				else{	
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
			
							while(seqNum+lengthNum<=ackNum&&!client.window.isEmpty()){	//CUMULATIVE ACKS
								System.out.println("WINDOW SHIFTED");
								try{
								client.t.cancel();
								}
								catch(IllegalStateException e){}
								client.t = new Timer();
								client.t.schedule(new TimeoutTask(client.window, client),Global.TIMEOUT);
								client.window.poll();
								if(!client.window.isEmpty()){
									seqBytes = Arrays.copyOfRange(client.window.peek(), 0, 4);	
									lengthBytes = Arrays.copyOfRange(client.window.peek(), 4, 8);	
									seqNum = ByteBuffer.wrap(seqBytes).getInt();	
									lengthNum = ByteBuffer.wrap(lengthBytes).getInt();
								}
							}
						}
					}
					else{
						this.dupAckCount++;
						System.out.println("DuplicateAckCount :"+dupAckCount);
						if(dupAckCount>3&&!client.window.isEmpty()){ //TODO change this value for fast retransmit
							client.sendData(client.window.peek()); 
							this.dupAckCount=0;
							System.out.println("retransmit finish");
						}
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
		return Arrays.copyOfRange(bytes, 8, bytes.length);
	}
}
