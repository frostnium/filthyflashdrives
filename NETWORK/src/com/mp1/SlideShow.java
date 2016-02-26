package com.mp1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Timer;

import com.server.ServerFrame;
import com.server.UDPServer;

public class SlideShow implements ActionListener{
	
	public ServerFrame frame;
	public Timer timer;
	public int interval;
	public boolean isActive;
	private UDPServer server;
	
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
				server.sendData();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			
			timer.stop();
			this.isActive = false;
		}
	}
}
