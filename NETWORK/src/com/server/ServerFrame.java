package com.server;

import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.mp1.SlideShow;

public class ServerFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<JLabel> storedImages;
	public ArrayList<ImageIcon> images;
	public int imageIndex;
	private SlideShow sshow;

	public ServerFrame() {
		this.storedImages = new ArrayList<JLabel>();
		this.images = new ArrayList<ImageIcon>();
		this.imageIndex = 0;
		this.initComponents();
	}
	
	private void initComponents() {
		this.setLayout(null);
		this.setBounds(300, 100, 1000, 600);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		File myDir = new File("images");
		File[] files = myDir.listFiles();
		if(myDir.exists() && myDir.isDirectory()) 
			files = myDir.listFiles(); 
		
		for(int i = 0; i < files.length; i++) {
			images.add(new ImageIcon(files[i].getPath(),files[i].getPath().substring(7)));
		}
		
		for(int i = 0; i < images.size(); i++) {
			storedImages.add(new JLabel(images.get(i)));
			storedImages.get(i).setBounds(0, 0, 1000, 600);
			storedImages.get(i).setVisible(false);
			this.getContentPane().add(storedImages.get(i));
		}
		
		storedImages.get(imageIndex).setVisible(true);
		
	}
	
	public void nextImage() {
		if(this.imageIndex + 1 < storedImages.size()) {
			this.storedImages.get(imageIndex).setVisible(false);
			this.imageIndex++;
			this.storedImages.get(imageIndex).setVisible(true);
			this.repaint();
		}
		
	}
	
	public void prevImage() {
		if(this.imageIndex - 1 >= 0) {
			this.storedImages.get(imageIndex).setVisible(false);
			this.imageIndex--;
			this.storedImages.get(imageIndex).setVisible(true);
			this.repaint();
		}
	}
	
	public void displayImage(int index) {
		this.storedImages.get(imageIndex).setVisible(false);
		this.storedImages.get(index).setVisible(true);
		this.repaint();
	}

	public SlideShow getSshow() {
		return sshow;
	}

	public void setSshow(SlideShow sshow) {
		this.sshow = sshow;
	}
	
	
}
