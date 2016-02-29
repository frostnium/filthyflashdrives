package com.server;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class VideoPlayer extends ServerMedia{
	
	public ArrayList<String> mrl;
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	
	public VideoPlayer() {
		new NativeDiscovery().discover();
		this.mrl = new ArrayList<String>();
		this.mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		this.initComponents();
	}
	
	private void initComponents() {
		this.setLayout(new BorderLayout());
		this.setBounds(300, 100, 1000, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(false);
		
		File myDir = new File("video");
		this.mediaFiles = myDir.listFiles();
		if(myDir.exists() && myDir.isDirectory()) 
			mediaFiles = myDir.listFiles(); 
		
		for(int i = 0; i < this.mediaFiles.length; i++) {
			mrl.add(new String("/"+mediaFiles[i].getPath()));
		}
		
		this.mediaIndex = 0;
		this.getContentPane().add(mediaPlayerComponent, BorderLayout.CENTER);
		mediaPlayerComponent.getMediaPlayer().prepareMedia(mrl.get(mediaIndex));
	}

	@Override
	public void next() {
		if(this.mediaIndex + 1 < mrl.size()) {
			mediaPlayerComponent.getMediaPlayer().stop();
			this.mediaIndex++;
			mediaPlayerComponent.getMediaPlayer().prepareMedia(mrl.get(mediaIndex));
		}
		
	}

	@Override
	public void prev() {
		if(this.mediaIndex - 1 >= 0) {
			mediaPlayerComponent.getMediaPlayer().stop();
			this.mediaIndex--;
			mediaPlayerComponent.getMediaPlayer().prepareMedia(mrl.get(mediaIndex));
		}
		
	}

	@Override
	public void displayIndex(int index) {
		// TODO Auto-generated method stub
		
	}
	
	public void play() {
		mediaPlayerComponent.getMediaPlayer().start();
	}
	
	public void pause() {
		mediaPlayerComponent.getMediaPlayer().pause();
	}
	
	public void stop() {
		mediaPlayerComponent.getMediaPlayer().stop();
	}

}
