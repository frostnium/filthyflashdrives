package com.server;

import java.io.File;
import javax.swing.JFrame;

public abstract class ServerMedia extends JFrame{
	
	protected File[] mediaFiles;
	
	public abstract void next();
	public abstract void prev();
	public abstract void displayIndex(int index);
	
	public int mediaIndex;

}
