package org.mineap.mpgp.sstp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mineap.mpgp.MGP;

public class SSTPserver {

	private ServerSocket serverSocket;
	private Socket socket;
	private String filename;
	private String server;
	private MGP mgp;

	public SSTPserver(MGP mgp){

		this.mgp = mgp;
		
		JFrame f = new JFrame("SSTP Server");
		JLabel lb1 = new JLabel("-報告-");
		JLabel lb2 = new JLabel("SSTP Serverが起動しています。");
		JButton bt = new JButton("終了");
		
		//ボタンにアクションリスナを登録
		bt.addActionListener(new PushButtonActionListener());
		JPanel p = new JPanel();
		p.add(lb1);
		p.add(lb2);
		//p.add(bt);
		f.getContentPane().add(p,BorderLayout.CENTER);
		f.setDefaultCloseOperation(f.DISPOSE_ON_CLOSE);
		f.setSize(200,150);
		f.setLocation(50, 50);
		f.setVisible(true);
		
		this.startServer();

	}

	

	class PushButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			// TODO 自動生成されたメソッド・スタブ
			System.exit(0);
			
		}
	}

	public void startServer() {
		
		try {
			
			//サーバーソケットの生成
			serverSocket = new ServerSocket(9801);
			//メインループ
			
			while(true){
				
				//クライアントからの接続待ち
				socket = serverSocket.accept();
				//処理をスレッドに投げる
				new GetConnect(socket,mgp);
			}

		} catch (IOException e){
			e.printStackTrace();
		}
	}
}