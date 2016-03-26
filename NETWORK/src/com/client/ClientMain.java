package com.client;

import java.io.UnsupportedEncodingException;



public class ClientMain {

	public static void main(String[] args) throws UnsupportedEncodingException {
		try {
			new ClientFrame();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
