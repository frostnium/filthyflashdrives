package com.mp1;

import java.io.Serializable;

public class Data implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public String command;
	public String fileName;
	
	public Data(String command) {
		this.command = command;
	}
	
	public Data(String command, String fileName) {
		this.command = command;
		this.fileName = fileName;
	}
}
