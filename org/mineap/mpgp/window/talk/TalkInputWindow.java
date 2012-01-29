package org.mineap.mpgp.window.talk;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.mineap.mpgp.MGP;
import org.mineap.mpgp.window.UpdateControlableWindow;

public class TalkInputWindow implements UpdateControlableWindow{

	private JFrame jFrame = null;
	private JPanel jContentPane = null;
	private JTextField jTextField = null;
	private MGP mgp = null;
	private String name;
	private boolean isOnFocus;
	

	public TalkInputWindow(String string, MGP mgp) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.jFrame = getJFrame();
		this.name = string;
		this.mgp = mgp;
	}

	public TalkInputWindow() {
		// TODO 自動生成されたコンストラクター・スタブ
		this.jFrame = getJFrame();
		this.name = "test";
	}

	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	public JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setSize(new Dimension(263, 60));
			jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			jFrame.setResizable(false);
			jFrame.setTitle("Comment");
			jFrame.setContentPane(getJContentPane());
			jFrame.setLocationRelativeTo(null);
			jFrame.setVisible(true);
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJTextField(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBounds(new Rectangle(0, 0, 257, 33));
			jTextField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String str = jTextField.getText();
					if(str.equals("exit")){
						System.exit(1);
					}
					
					Matcher m = Pattern.compile("ss:(.*)").matcher(str);
					if(m.find()){
						mgp.getSakuraThread(name).ssPlayer(m.group(1));
					}else{
						mgp.getResponder(name).responseOnCommunicate("user", str, "");
					}
					jTextField.setText("");
				}
			});
			
		}
		return jTextField;
	}
	
	public void delWindow(){
		mgp = null;
		jFrame.setVisible(false);
		jFrame = null;
	}
	
	public static void main(String[] args){
		new TalkInputWindow();
	}

	public void windowGainedFocus(WindowEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		this.isOnFocus = true;
	}

	public void windowLostFocus(WindowEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		this.isOnFocus = false;
	}


	public boolean getIsOnFocus() {
		// TODO 自動生成されたメソッド・スタブ
		return isOnFocus;
	}

}
