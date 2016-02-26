package com.client;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;

public class ClientFrame extends JFrame implements ActionListener, PropertyChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UDPClient client;
	private JButton next;
	private JButton prev;
	private JButton exit;
	private JButton slideshow;
	private JLabel ipLabel;
	private JTextArea ipTextArea;
	private JTextArea ssInterval;
	private JButton connect;
	public JLabel fileName;
	private SwingWorker<Void, Void> worker;
	
	public ClientFrame() throws Exception {
		this.client = new UDPClient();
		this.next = new JButton();
		this.prev = new JButton();
		this.exit = new JButton();
		this.slideshow = new JButton();
		this.ipLabel = new JLabel();
		this.ipTextArea = new JTextArea();
		this.ssInterval = new JTextArea();
		this.connect = new JButton();
		this.fileName = new JLabel();
		
		this.worker=new ClientSwingWorker(this.client);
		
		this.initComponents();
	}
	
	private void initComponents() {
		this.setLayout(null);
		this.setBounds(0, 0, 400, 300);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fileName.setBounds(100, 95, 200, 20);
		fileName.setText("FILE NAME: ");
		this.getContentPane().add(fileName);
		
		ipLabel.setBounds(31, 120, 140, 10);
		ipLabel.setText("Enter Server IP Address:");
		this.getContentPane().add(ipLabel);
		
		ipTextArea.setBounds(176, 118, 100, 20);
		this.getContentPane().add(ipTextArea);
		
		connect.setBounds(281, 116, 70, 25);
		connect.setText("connect");
		connect.setFont(new Font("Lucida Grande",Font.BOLD,9));
		connect.setActionCommand("connect");
		connect.addActionListener(this);
		this.getContentPane().add(connect);
		
		next.setBounds(140, 150, 100, 50);
		next.setText("NEXT");
		next.setActionCommand("next");
		next.addActionListener(this);
		this.getContentPane().add(next);
		
		prev.setBounds(40, 150, 100, 50);
		prev.setText("PREVIOUS");
		prev.setActionCommand("prev");
		prev.addActionListener(this);
		this.getContentPane().add(prev);
		
		exit.setBounds(240, 150, 100, 50);
		exit.setText("EXIT");
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		this.getContentPane().add(exit);
		
		slideshow.setBounds(40, 210, 20, 20);
		slideshow.setIcon(new ImageIcon("icons/slideshow.png"));
		slideshow.setActionCommand("slideshow");
		slideshow.addActionListener(this);
		this.getContentPane().add(slideshow);
		
		ssInterval.setBounds(70, 210, 100, 20);
		ssInterval.setText("1000");
		this.getContentPane().add(ssInterval);
		this.repaint();
		
		this.worker.addPropertyChangeListener(this);
		this.worker.execute();
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if("fileName".equals(arg0.getPropertyName())){
			this.fileName.setText("FILE NAME: "+(String) arg0.getNewValue());
			repaint();
		}
		else if("ssInterval".equals(arg0.getPropertyName())) {
			client.sentence = ssInterval.getText();
			try {
				client.send();
			} catch (Exception e) {}
		}
		
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if("next".equals(arg0.getActionCommand())){
			client.sentence = UDPClient.NEXT;
			try {
				client.send();
			}catch(Exception ex){
			
			}
		}
		else if("prev".equals(arg0.getActionCommand())){
			client.sentence = UDPClient.PREV;
			try {
				client.send();
			}catch(Exception ex){}
		}
		else if("exit".equals(arg0.getActionCommand())){
			client.sentence = UDPClient.EXIT;
			try {
				client.send();
			} catch (Exception e) {}
			client.close();
			System.exit(1);
		}
		else if("slideshow".equals(arg0.getActionCommand())) {
			try {
				Integer.parseInt(ssInterval.getText());
			}catch(NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Invalid input!");
				return;
			}
			client.sentence = UDPClient.SSHOW;
			try {
				client.send();
			}catch(Exception ex){}
		}
		else if("connect".equals(arg0.getActionCommand())){
			try {
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
