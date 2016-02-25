package com.mp1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import com.server.ServerFrame;

public class SlideShow implements ActionListener{
	
	public ServerFrame frame;
	public Timer timer;
	public int interval;
	public boolean isActive;
	
	public SlideShow(ServerFrame frame, int interval) {
		this.frame = frame;
		this.timer = new Timer(interval, this);
		timer.start();
		this.isActive = true;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(frame.imageIndex < frame.images.size()-1) {
			frame.nextImage();
		}
		else {
			timer.stop();
			this.isActive = false;
		}
	}

}
