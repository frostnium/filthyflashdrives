package com.client;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mp1.CustomFilter;
import com.mp1.Global;

public class ClientFrame extends JFrame implements ActionListener, PropertyChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private UDPClient client;
	
	//global components
	private JButton next;
	private JButton prev;
	private JLabel fileName;
	private JButton exit;
	
	//components for toggling
	private ButtonGroup fileTypeGroup;
	private JRadioButton[] fileType;
	
	//image-related components
	private JButton slideshow;
	private JTextArea ssInterval;
	private JLabel image;
	
	//video- and audio-related components
	private JButton playStop;
	private ImageIcon[] playStopIcons;
	
	//connectivity components
	private JLabel ipLabel;
	private JTextArea ipTextArea;
	private JButton connect;
	
	
	//file uploading components
	private JButton browsebtn;
	private JLabel fileUploadName;
	private JButton uploadbtn;
	private File uploadFile;
	
	private SwingWorker<Void, Void> worker;
	
	public ClientFrame() throws Exception {
		this.client = new UDPClient();
		
		this.browsebtn = new JButton();
		this.fileUploadName = new JLabel();
		this.uploadbtn = new JButton();
		
		this.next = new JButton();
		this.prev = new JButton();
		this.exit = new JButton();
		this.fileName = new JLabel();
		
		this.fileTypeGroup = new ButtonGroup();
		this.fileType=new JRadioButton[2];
		this.fileType[0] = new JRadioButton("Image",true);
		this.fileType[1] = new JRadioButton("Video/Audio");
		
		
		this.slideshow = new JButton();
		this.ssInterval = new JTextArea();
		this.image = new JLabel();
		
		this.playStop = new JButton();
		this.playStopIcons=new ImageIcon[2];
		this.playStopIcons[0]=new ImageIcon("icons/play.png");
		this.playStopIcons[1]=new ImageIcon("icons/stop.png");
		
		this.ipLabel = new JLabel();
		this.ipTextArea = new JTextArea();
		this.connect = new JButton();
		
		this.worker=new ClientSwingWorker(this.client);
		
		this.initComponents();
	}
	
	private void initComponents() {
		this.setLayout(null);
		this.setBounds(0, 0, 400, 500);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		fileName.setBounds(100, 25, 200, 20);
		fileName.setText("FILE NAME: ");
		this.getContentPane().add(fileName);
		
		ipLabel.setBounds(31, 50, 140, 10);
		ipLabel.setText("Enter Server IP Address:");
		this.getContentPane().add(ipLabel);
		
		ipTextArea.setBounds(176, 48, 100, 20);
		this.getContentPane().add(ipTextArea);
		
		connect.setBounds(281, 46, 70, 25);
		connect.setText("connect");
		connect.setFont(new Font("Lucida Grande",Font.BOLD,9));
		connect.setActionCommand("connect");
		connect.addActionListener(this);
		this.getContentPane().add(connect);
		
		
		fileType[0].setBounds(101, 75, 60, 15);
		fileType[0].setActionCommand("imagetype");
		fileType[0].addActionListener(this);
		fileTypeGroup.add(fileType[0]);
		this.getContentPane().add(fileType[0]);
		
		fileType[1].setBounds(191, 75, 100, 15);
		fileType[1].setActionCommand("vatype");
		fileType[1].addActionListener(this);
		fileTypeGroup.add(fileType[1]);
		this.getContentPane().add(fileType[1]);
		
		next.setBounds(140, 100, 100, 50);
		next.setText("NEXT");
		next.setActionCommand("next");
		next.addActionListener(this);
		this.getContentPane().add(next);
		
		prev.setBounds(40, 100, 100, 50);
		prev.setText("PREVIOUS");
		prev.setActionCommand("prev");
		prev.addActionListener(this);
		this.getContentPane().add(prev);
		
		exit.setBounds(240, 100, 100, 50);
		exit.setText("EXIT");
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		this.getContentPane().add(exit);
		
		slideshow.setBounds(40, 160, 20, 20);
		slideshow.setIcon(new ImageIcon("icons/slideshow.png"));
		slideshow.setActionCommand("slideshow");
		slideshow.addActionListener(this);
		this.getContentPane().add(slideshow);
		
		ssInterval.setBounds(70, 160, 100, 20);
		ssInterval.setText("1000");
		this.getContentPane().add(ssInterval);

		playStop.setBounds(170,160,40,40);
		playStop.setActionCommand("play/stop");
		playStop.addActionListener(this);
		playStop.setVisible(false);
		this.getContentPane().add(playStop);
		
		browsebtn.setBounds(40, 190, 80, 30);
		browsebtn.setText("Browse");
		browsebtn.setActionCommand("browse");
		browsebtn.addActionListener(this);
		this.getContentPane().add(browsebtn);
		
		fileUploadName.setBounds(130,190,120,30);
		fileUploadName.setFont(new Font("Serif",Font.PLAIN,10));
		fileUploadName.setText("NONE");
		this.getContentPane().add(fileUploadName);
		
		uploadbtn.setBounds(260, 190, 80, 30);
		uploadbtn.setText("Upload");
		uploadbtn.setActionCommand("upload");
		uploadbtn.addActionListener(this);
		this.getContentPane().add(uploadbtn);
		
		image.setBounds(0, 250, 400, 200);
		this.getContentPane().add(image);
		this.repaint();
		
		this.worker.addPropertyChangeListener(this);
		this.worker.execute();
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if("fileName".equals(arg0.getPropertyName())){
			this.fileName.setText("FILE NAME: "+((String) arg0.getNewValue()).substring(9));
			repaint();
		}
		else if("ssInterval".equals(arg0.getPropertyName())) {
			client.sentence = ssInterval.getText();
			try {
				client.send();
			} catch (Exception e) {}
		}
		else if("complete".equals(arg0.getPropertyName())) {
			InputStream in = new ByteArrayInputStream(client.imageData);
			BufferedImage bImageFromConvert = null;
			try {
				bImageFromConvert = ImageIO.read(in);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.image.setIcon(new ImageIcon(bImageFromConvert.getScaledInstance(400, 200, Image.SCALE_DEFAULT)));
			this.repaint();
		}
		else if("goupload".equals(arg0.getPropertyName())) {
			System.out.println("upload time");
			try {
				client.sendImage(uploadFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	public void actionPerformed(ActionEvent arg0) {
		switch(arg0.getActionCommand()){
			case "browse":  JFileChooser chooser = new JFileChooser();
		            		chooser.setCurrentDirectory(new java.io.File("."));
		            		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				            chooser.setDialogTitle("Choose file to upload");
				            chooser.setFileFilter(new CustomFilter());
				            chooser.setAcceptAllFileFilterUsed(false);
				
				            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				            	uploadFile=chooser.getSelectedFile();
				                fileUploadName.setText(uploadFile.getName());
				                System.out.println("File Name: "+ uploadFile.getName());
				            } else {
				            	fileUploadName.setText("NONE");
				                System.out.println("No Selection ");
				            }
							break;
			case "upload": if(fileUploadName.getText().equals("NONE")) {
						JOptionPane.showMessageDialog(this, "No image selected!");
						return;
					}
					client.sentence = UDPClient.STARTUPLOAD;
					try {
						client.send();
					}catch(Exception ex){} break;
			case "imagetype": slideshow.setVisible(true);  //TODO: send ping shit
							  ssInterval.setVisible(true);
							  playStop.setVisible(false);
							  client.sentence = UDPClient.IMODE;
								try {
									client.send();
								} catch (Exception e1) {}
							  break;
				
			case "vatype": slideshow.setVisible(false);  //TODO: send ping shit
						   ssInterval.setVisible(false);
						   playStop.setVisible(true);
						   playStop.setIcon(playStopIcons[0]);
						   client.sentence = UDPClient.VMODE;
							try {
								client.send();
							} catch (Exception e1) {}
						   break;
		
			case "play/stop": if(playStop.getIcon().equals(playStopIcons[0])) { //TODO: send ping shit
								playStop.setIcon(playStopIcons[1]);
								client.sentence = UDPClient.PLAY;
						}
							  else {
								  playStop.setIcon(playStopIcons[0]);
								  client.sentence = UDPClient.PAUSE;
							  }
							  try {
								client.send();
							} catch (Exception e1) {}
							  break;
				
			case "next": client.sentence = UDPClient.NEXT;
							if(fileType[1].isSelected())
								playStop.setIcon(playStopIcons[0]);
							try {
								client.send();
							}catch(Exception ex){} break;
			case "prev": client.sentence = UDPClient.PREV;
							if(fileType[1].isSelected())
								playStop.setIcon(playStopIcons[0]);
							try {
								client.send();
							}catch(Exception ex){} break;
			case "exit": client.sentence = UDPClient.EXIT;
							try {
								client.send();
							} catch (Exception e) {}
							client.close();
							System.exit(1); break;
			case "slideshow": try {
								Integer.parseInt(ssInterval.getText());
							}catch(NumberFormatException ex) {
								JOptionPane.showMessageDialog(this, "Invalid input!");
								return;
							}
							client.sentence = UDPClient.SSHOW;
							try {
								client.send();
							}catch(Exception ex){} break;
			case "connect": try {
							client.IPAddress=InetAddress.getByName(ipTextArea.getText());
								try {
									if(client.IPAddress.isReachable(3000)){
										ipTextArea.setBackground(Color.GREEN);
									}else
										ipTextArea.setBackground(Color.RED);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							catch (UnknownHostException e) {
								ipTextArea.setBackground(Color.RED);
							}
		}
		
	}


}
