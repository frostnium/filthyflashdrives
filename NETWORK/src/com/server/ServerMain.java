package com.server;


public class ServerMain {

	public static void main(String[] args) {
		try {
			new UDPServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

