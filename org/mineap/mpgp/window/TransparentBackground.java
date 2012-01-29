package org.mineap.mpgp.window;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JFrame;


/**
 * 透明なウィンドウを偽装するためのバックグラウンドの絵を提供する
 * TransparentBackgroundクラス
 * 
 * @author shiraminekeisuke
 *
 */
public class TransparentBackground extends JComponent 
		implements ComponentListener, UpdateControlableWindow, Runnable{

	private JFrame jFrame;
	private Image background;
	private boolean refreshRequested;
	private long lastupdate;
	//複数のウィンドウを持つ場合にフォーカスをアプリケーション単位で管理するためのもの
	private UpdateControl uc;
	//上書きタイミングの取得にUpdateControlを使うかどうか
	private boolean isUseUC;
	private boolean isOnFocus;
	private int updateExceptionFlag;

	/**
	 * コンストラクタ。透明に見せたいフレームを渡す。
	 * @param jframe　透明に見せたいフレーム
	 */
	public TransparentBackground(JFrame jFrame){
		this.jFrame = jFrame;
		this.isUseUC = false;
		updateBackground();
		jFrame.addComponentListener(this);
		jFrame.addWindowFocusListener(this);
		new Thread(this).start();
	}

	/**
	 * コンストラクタ。透明に見せたいフレームと、ウィンドウそれを管理するUpdateControlを渡す。
	 * @param jFrame
	 * @param uc
	 */
	public TransparentBackground(JFrame jFrame, UpdateControl uc){
		this.jFrame = jFrame;
		this.uc = uc;
		this.isUseUC = true;
		updateBackground();
		jFrame.addComponentListener(this);
		jFrame.addWindowFocusListener(this);
		new Thread(this).start();
	}
	
	/**
	 * 背景を更新する
	 * Robotクラスを使ってキャプチャした後、backgroundに保存する。
	 */
	private void updateBackground() {
		// TODO 自動生成されたメソッド・スタブ
		try{
			Robot rbt = new Robot();
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension dim = tk.getScreenSize();
			background = rbt.createScreenCapture(new Rectangle(0,0,(int)dim.getWidth(),
					(int)dim.getHeight()));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * このコンポーネントの描画が必要になったときにJavaシステムから呼び出される。
	 * （ たとえば最初に表示する時、最小化した後の再表示、 他のウィンドウの下に隠れた後に
	 * 再度アクティブになった時、 コンポーネントのサイズが変更された時 等）
	 * コンポーネントの位置を取得し、背景を描画する。
	 */
	public void paintComponent(Graphics g){
		Point pos = this.getLocationOnScreen();
		Point offset = new Point(-pos.x,-pos.y);
		g.drawImage(background, offset.x, offset.y, null);
		
	}

	public void componentHidden(ComponentEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	public void componentMoved(ComponentEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
		repaint();
		
	}

	public void componentResized(ComponentEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
		repaint();
		
	}

	public void componentShown(ComponentEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
		repaint();
		
	}

	public void windowGainedFocus(WindowEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		if(updateExceptionFlag == 0){
			if(isUseUC){
				System.out.println("wgf");
				if(!uc.isAppOnFocus()){
					refresh();
					System.out.println("call refresh()");
				}else{
				}
			}else{
				refresh();
			}
			
		}
		isOnFocus = true;
	}

	public void windowLostFocus(WindowEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		if(updateExceptionFlag == 0){
			if(isUseUC){
				System.out.println("wlf");
				if(!uc.isAppOnFocus()){
					refresh();
					System.out.println("call refresh()");
				}else{
				}
			}else{
				refresh();
			}
			
		}
		isOnFocus = false;
	}

	/**
	 * 1/4秒ごとにリフレッシュが要求されていないかどうか調べる。
	 * また、前回のリフレッシュから１秒以上経過していて、かつフレームが可視であれば
	 * 背景を取得し直してリフレッシュを行う。
	 */
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		try {
			while(true){
				Thread.sleep(250);
				System.out.println();
				System.out.println("check!:"+refreshRequested);
				System.out.println();
				
				long now = new Date().getTime();
				if(refreshRequested && ((now - lastupdate)>1000)){
					//if(this.isUseUC){
						
					//}else{
						if(jFrame.isVisible()){
							Point location = jFrame.getLocation();
							updateExceptionFlag = 1;
							jFrame.setVisible(false);
							isOnFocus = true;
							updateBackground();
							jFrame.setVisible(true);
							updateExceptionFlag = 0;
							jFrame.setLocation(location);
							refresh();
						}
						lastupdate = now;
						refreshRequested = false;
					//}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * ウィンドウが可視である事を確認してからrepaint()を実行
	 */
	private void refresh() {
		// TODO 自動生成されたメソッド・スタブ
		
		if(jFrame.isVisible()){
			repaint();
			refreshRequested = true;
			lastupdate = new Date().getTime();
		}
		
		
	}

	public boolean getIsOnFocus() {
		// TODO 自動生成されたメソッド・スタブ
		return isOnFocus;
	}
	
}
