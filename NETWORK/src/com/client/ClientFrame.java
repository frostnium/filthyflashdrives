package com.client;


import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import com.mp1.CustomFilter;
import com.mp1.Global;
import com.mp1.UDPCommands;

public class ClientFrame extends JFrame implements ActionListener, PropertyChangeListener, MouseMotionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private UDPClient client;
	
	//global components
	private JLabel frameHeader;
	private JLayeredPane lpane;
	private JLabel background;
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

	protected Point initialClick;
	
	public ClientFrame() throws Exception {
		
		this.client=new UDPClient();
	
		this.background = new JLabel();
		
		this.lpane = new JLayeredPane();
		
		this.frameHeader = new JLabel();
		
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
		this.playStopIcons[1]=new ImageIcon("icons/pause.png");
		
		this.ipLabel = new JLabel();
		this.ipTextArea = new JTextArea();
		this.connect = new JButton();
		
		this.worker = new ClientSwingWorker(client);
		worker.execute();

		this.initComponents();
	}
	
	private void initComponents() {
		this.setLayout(null);
		this.setBounds(100, 100, 400, 500);
		this.setUndecorated(true);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		lpane.setBounds(0, 0, 400, 500);
		lpane.setVisible(true);
		this.add(lpane);
		
		fileName.setBounds(50, 50, 150, 20);
		fileName.setText("FILENAME: ");
		this.lpane.add(fileName);
		
		ipLabel.setBounds(31, 105, 140, 10);
		ipLabel.setText("Enter Server IP Address:");
		this.lpane.add(ipLabel);
		
		ipTextArea.setBounds(176, 100, 100, 20);
		this.lpane.add(ipTextArea);
		
		connect.setBounds(281, 98, 70, 25);
		connect.setIcon(new ImageIcon("icons/connect.png"));
		connect.setActionCommand("connect");
		connect.addActionListener(this);
		this.lpane.add(connect);
		
		
		fileType[0].setBounds(101, 143, 60, 15);
		fileType[0].setOpaque(false);
		fileType[0].setActionCommand("imagetype");
		fileType[0].addActionListener(this);
		fileTypeGroup.add(fileType[0]);
		this.lpane.add(fileType[0]);
		
		fileType[1].setBounds(191, 143, 100, 15);
		fileType[1].setOpaque(false);
		fileType[1].setActionCommand("vatype");
		fileType[1].addActionListener(this);
		fileTypeGroup.add(fileType[1]);
		this.lpane.add(fileType[1]);
		
		next.setBounds(230, 170, 70, 50);
		next.setIcon(new ImageIcon("icons/next.png"));
		next.setActionCommand("next");
		next.addActionListener(this);
		this.lpane.add(next);
		
		prev.setBounds(80, 170, 70, 50);
		prev.setIcon(new ImageIcon("icons/prev.png"));
		prev.setActionCommand("prev");
		prev.addActionListener(this);
		this.lpane.add(prev);
		
		exit.setBounds(370, 0, 24, 24);
		exit.setContentAreaFilled(false);
		exit.setBorderPainted(false);
		exit.setIcon(new ImageIcon("icons/exit.png"));
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		this.lpane.add(exit);
		
		browsebtn.setBounds(220, 45, 30, 30);
		browsebtn.setIcon(new ImageIcon("icons/browse.png"));
		browsebtn.setActionCommand("browse");
		browsebtn.addActionListener(this);
		this.lpane.add(browsebtn);
		
		fileUploadName.setBounds(254,45,100,30);
		fileUploadName.setFont(new Font("Serif",Font.PLAIN,10));
		fileUploadName.setText("NONE");
		this.lpane.add(fileUploadName);
		
		uploadbtn.setBounds(345, 45, 30, 30);
		uploadbtn.setIcon(new ImageIcon("icons/upload.png"));
		uploadbtn.setActionCommand("upload");
		uploadbtn.addActionListener(this);
		this.lpane.add(uploadbtn);
		
		slideshow.setBounds(165, 170, 50, 50);
		slideshow.setIcon(new ImageIcon("icons/slideshow.png"));
		slideshow.setActionCommand("slideshow");
		slideshow.addActionListener(this);
		this.lpane.add(slideshow);
		
		ssInterval.setBounds(165, 225, 50, 20);
		ssInterval.setText("1000");
		this.lpane.add(ssInterval);

		playStop.setBounds(165,170,50,50);
		playStop.setActionCommand("play/stop");
		playStop.addActionListener(this);
		playStop.setVisible(false);
		this.lpane.add(playStop);
		
		
		
		image.setBounds(0, 275, 400, 200);
		this.lpane.add(image);
		//this.repaint();
		
		background.setBounds(0,0,400,500);
		background.setIcon(new ImageIcon("icons/bg.png"));
		this.lpane.add(background);
		
		frameHeader.setBounds(0,0,370,40);
		frameHeader.addMouseListener(new MouseAdapter() {
	        public void mousePressed(MouseEvent e) {
	            initialClick = e.getPoint();
	            getComponentAt(initialClick);
	        }
	    });
		frameHeader.addMouseMotionListener(this);
		this.lpane.add(frameHeader);
		
		this.worker.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if("fileName".equals(arg0.getPropertyName())){
			this.fileName.setText("FILE NAME: "+((String) arg0.getNewValue()).substring(9));
		}
		else if("ssInterval".equals(arg0.getPropertyName())) {
			try {
				client.sendCommand(ssInterval.getText());
			} catch (Exception e) {}
		}
		
		else if("complete".equals(arg0.getPropertyName())) {
			System.out.println("IMAGE TRANSFER COMPLETE");
			InputStream in = new ByteArrayInputStream(client.imageData);
			BufferedImage bImageFromConvert = null;
			try {
				bImageFromConvert = ImageIO.read(in);
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.image.setIcon(new ImageIcon(bImageFromConvert.getScaledInstance(400, 200, Image.SCALE_DEFAULT)));
			this.repaint();
		}
		
		else if("goupload".equals(arg0.getPropertyName())) {
			System.out.println("upload time");
			Thread t1=new Thread(){
				public void run(){
					byte[] bytes = null;
					try {
						bytes = client.getImageBytes(uploadFile);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					byte[] sendData;
					
					int interval = 0, chunksReceived = 0;
					int addend = 1500;
					
					int seqNum=0;
					
					do { //TODO: addhere
						if(client.window.size()<5){
							byte[] dataChunk = Arrays.copyOfRange(bytes, interval, interval+addend);
							sendData = new byte[0];
						//	sendData = Global.concat(sendData, ByteBuffer.allocate(4).putInt(seqNum+dataChunk.length).array());
							sendData = Global.concat(sendData, ByteBuffer.allocate(4).putInt(seqNum).array());
							sendData = Global.concat(sendData, ByteBuffer.allocate(4).putInt(dataChunk.length).array());
							sendData = Global.concat(sendData, dataChunk);
							client.window.add(sendData);
							try {
								client.sendData(sendData);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							seqNum+=dataChunk.length;
							if(interval + addend > bytes.length) 
								addend = bytes.length - interval;
							else
								interval += addend;
						} else
							try {
								wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}while(interval < bytes.length);
					
					System.out.println("DONE");
					String message = "TRCOMPLETE"+image.getName();
					sendData = message.getBytes();    
					client.window.clear();
					try {
						client.sendData(sendData);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			Thread t2=new Thread(){
				public void run(){
					while(true){
						DatagramPacket receivePacket = null;
						try {
							receivePacket = client.receive();
						} catch (Exception e) {
							e.printStackTrace();
						}
						String data = new String(receivePacket.getData()).trim();
						System.out.println("PACKET RECEIVED: "+receivePacket.getData().length);
						if("ACK".equals(new String(Arrays.copyOfRange(receivePacket.getData(), 7, receivePacket.getData().length)).trim())){
							System.out.println("asd");
							byte[] ackBytes = Arrays.copyOfRange(receivePacket.getData(), 0, 4);
							int ackNum=ByteBuffer.wrap(ackBytes).getInt();
							System.out.println("PACKET ACK: "+ackNum);
							byte[] seqBytes = Arrays.copyOfRange(client.window.peek(), 0, 4);
							byte[] lengthBytes = Arrays.copyOfRange(client.window.peek(), 4, 8);
							int seqNum = ByteBuffer.wrap(seqBytes).getInt();
							int lengthNum = ByteBuffer.wrap(lengthBytes).getInt();
							if(seqNum==ackNum){
								System.out.println("asd");
								client.window.remove();	
								notify();
								try {
									wait();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			};
			
			t1.start();
			t2.start();
			
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
							try {
								client.sendCommand(UDPCommands.STARTUPLOAD);
							}catch(Exception ex){} 
							break;
			case "imagetype": slideshow.setVisible(true);  
							  ssInterval.setVisible(true);
							  playStop.setVisible(false);
								try {
									client.sendCommand(UDPCommands.IMODE);
								} catch (Exception e1) {}
							  break;
				
			case "vatype": slideshow.setVisible(false); 
						   ssInterval.setVisible(false);
						   playStop.setVisible(true);
						   playStop.setIcon(playStopIcons[0]);
							try {
								client.sendCommand(UDPCommands.VMODE);
							} catch (Exception e1) {}
						   break;
		
			case "play/stop": if(playStop.getIcon().equals(playStopIcons[0])) { //TODO: send ping shit
								playStop.setIcon(playStopIcons[1]);
								 try {
										client.sendCommand(UDPCommands.PLAY);
									} catch (Exception e1) {}
								}
							  else {
								  playStop.setIcon(playStopIcons[0]);
								  try {
										client.sendCommand(UDPCommands.PAUSE);
									} catch (Exception e1) {}
							  }
							  
							  break;
				
			case "next": if(fileType[1].isSelected())
							playStop.setIcon(playStopIcons[0]);
						 try {
							 client.sendCommand(UDPCommands.NEXT);
						 }catch(Exception ex){} break;
			case "prev": if(fileType[1].isSelected())
						 	playStop.setIcon(playStopIcons[0]);
						 try {
							 client.sendCommand(UDPCommands.PREV);
						 }catch(Exception ex){} break;
			case "exit": try {
							 client.sendCommand(UDPCommands.EXIT);
						 } catch (Exception e) {}
						 client.close();
						 System.exit(1); break;
			case "slideshow": try {
								Integer.parseInt(ssInterval.getText());
							}catch(NumberFormatException ex) {
								JOptionPane.showMessageDialog(this, "Invalid input!");
								return;
							}
							try {
								client.sendCommand(UDPCommands.SSHOW);
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

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// get location of Window
        int thisX = this.getLocation().x;
        int thisY = this.getLocation().y;

        // Determine how much the mouse moved since the initial click
        int xMoved = (thisX + arg0.getX()) - (thisX + initialClick.x);
        int yMoved = (thisY + arg0.getY()) - (thisY + initialClick.y);
        // Move window to this position
        int X = thisX + xMoved;
        int Y = thisY + yMoved;
        this.setLocation(X, Y);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		//NO IMPLEMENTATION HEHEIHUH
	}


}
