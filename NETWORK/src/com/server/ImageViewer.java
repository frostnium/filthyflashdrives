package com.server;

import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.mp1.SlideShow;

public class ImageViewer extends ServerMedia{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<JLabel> storedImages;
	private SlideShow sshow;

	public ImageViewer() {
		this.storedImages = new ArrayList<JLabel>();
		this.mediaIndex = 0;
		this.initComponents();
	}
	
	private void initComponents() {
		this.setLayout(null);
		this.setBounds(300, 100, 1000, 600);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		File myDir = new File("images");
		this.mediaFiles = myDir.listFiles();
		if(myDir.exists() && myDir.isDirectory()) 
			mediaFiles = myDir.listFiles(); 
		
		for(int i = 0; i < mediaFiles.length; i++) {
			storedImages.add(new JLabel(new ImageIcon(mediaFiles[i].getPath())));
			storedImages.get(i).setBounds(0, 0, 1000, 600);
			storedImages.get(i).setVisible(false);
			this.getContentPane().add(storedImages.get(i));
		}
		
		storedImages.get(mediaIndex).setVisible(true);
		
	}
	
	public void next() {
		if(this.mediaIndex + 1 < storedImages.size()) {
			this.storedImages.get(mediaIndex).setVisible(false);
			this.mediaIndex++;
			this.storedImages.get(mediaIndex).setVisible(true);
			this.repaint();
		}
		
	}
	
	public void prev() {
		if(this.mediaIndex - 1 >= 0) {
			this.storedImages.get(mediaIndex).setVisible(false);
			this.mediaIndex--;
			this.storedImages.get(mediaIndex).setVisible(true);
			this.repaint();
		}
	}
	
	public void displayIndex(int index) {
		this.storedImages.get(mediaIndex).setVisible(false);
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
