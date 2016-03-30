package com.mp1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

//SINGLETON
public class Logger { 
	private static Logger instance;
	private static BufferedWriter writer;
	
	private Logger(){
		try {
			writer= new BufferedWriter( new FileWriter("log.txt", false));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logSent(int data){
		Date date=new Date();
		String line = "SENT:: SEQ: "+data+" || "+new Timestamp(date.getTime());
		System.out.println(line);
		try {
			writer.write(line);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logLost(int data,boolean isSeq){
		Date date=new Date();
		String line;
		if(isSeq)
			line = "LOST:: SEQ: "+data+" || "+new Timestamp(date.getTime());
		else
			line = "LOST:: ACK: "+data+" || "+new Timestamp(date.getTime());
		System.out.println(line);
		try {
			writer.write(line);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logDiscarded(int data){
		Date date=new Date();
		String line = "DISCARDED:: SEQ: "+data+" || "+new Timestamp(date.getTime());
		System.out.println(line);
		try {
			writer.write(line);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logReceived(int data){
		Date date=new Date();
		String line = "RECEIVED:: ACK: "+data+" || "+new Timestamp(date.getTime());
		System.out.println(line);
		try {
			writer.write(line);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logSaved(int data){
		Date date=new Date();
		String line = "SAVED:: SEQ: "+data+" || "+new Timestamp(date.getTime());
		System.out.println(line);
		try {
			writer.write(line);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Logger getInstance(){
		if(instance==null)
			instance=new Logger();
		return instance;
	}
	

}
