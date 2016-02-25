package com.client;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;

public class ClientFrame extends JFrame implements ActionListener{
	
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
	private JButton connect;
	
	public ClientFrame() throws Exception {
		this.client = new UDPClient();
		this.next = new JButton();
		this.prev = new JButton();
		this.exit = new JButton();
		this.slideshow = new JButton();
		this.ipLabel = new JLabel();
		this.ipTextArea = new JTextArea();
		this.connect = new JButton();
		this.initComponents();
	}
	
	private void initComponents() {
		this.setLayout(null);
		this.setBounds(0, 0, 400, 300);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		
	}

	public void actionPerformed(ActionEvent arg0) {
		if("next".equals(arg0.getActionCommand())){
			client.sentence = UDPClient.NEXT;
			try {
				client.send();
				client.receive();
			}catch(Exception ex){
				System.out.println("SERVER NOT FOUND");
			}
		}
		else if("prev".equals(arg0.getActionCommand())){
			client.sentence = UDPClient.PREV;
			try {
				client.send();
				client.receive();
			}catch(Exception ex){}
		}
		else if("exit".equals(arg0.getActionCommand())){
			client.sentence = UDPClient.EXIT;
			client.close();
			System.exit(1);
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
