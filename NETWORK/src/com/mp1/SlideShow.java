package com.mp1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import com.server.ServerFrame;
import com.server.UDPServer;

public class SlideShow implements ActionListener{
	
	public ServerFrame frame;
	public Timer timer;
	public int interval;
	public boolean isActive;
	public UDPServer server;
	
	public SlideShow(ServerFrame frame, int interval,UDPServer server) {
		this.frame = frame;
		this.server=server;
		this.timer = new Timer(interval, this);
		timer.start();
		this.isActive = true;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(frame.imageIndex < frame.images.size()-1) {
			frame.nextImage();
			try {
				server.sendData("O");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			try {
				server.sendData("X");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			timer.stop();
			this.isActive = false;
		}
	}

}
