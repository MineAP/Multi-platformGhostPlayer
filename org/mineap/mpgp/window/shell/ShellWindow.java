package org.mineap.mpgp.window.shell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.mineap.mpgp.Responder;
import org.mineap.mpgp.balloon.Balloon3;
import org.mineap.mpgp.balloon.BalloonImage;
import org.mineap.mpgp.img.ImgControler;
import org.mineap.mpgp.img.Surface;
import org.mineap.mpgp.window.TransparentBackground;

/**
 * @author shiraminekeisuke
 * ShellWindowを生成するクラス
 * Shellはこのクラスを使って表示する。
 * 人間との対話を行うためのメインになる。
 */
public class ShellWindow {

	private JFrame jFrame = null;
	private JPanel jContentPane = null;
	//自分の名前
	private String name;
	//自分が使うサーフェス
	private Surface sf;
	//自分が使うベースベースイメージ
	private BufferedImage img;
	//ベースイメージの大きさ
	private int y;
	private int x;
	//サーフェスが変わったかどうかのフラグ
	private boolean isEmotionChange;
	private Toolkit tk;
	//Shellに会話させる為のウィンドウ(第３世代)
	private Balloon3 bl;
	private BalloonImage bi;
	//タイマー
	private Timer t;
	//ウィンドウの現在位置
	private int windowPoint_x;
	private int windowPoint_y;
	private Point start_drag;
	private Point start_loc;
	//スクリーンの大きさ
	private int sSize_y;
	private int sSize_x;
	//ウィンドウの幅（描画を行う始点）
	private int w_top;
	private int w_left;
	//親であるイメージコントローラ
	private ImgControler ic;
	//透明なウィンドウを実現するクラス
	private TransparentBackground tb;
	private Insets i;
	private Responder res;
	private int surfaceNo;
	private ConcurrentLinkedQueue<Surface> stackSurface = new ConcurrentLinkedQueue<Surface>();
	private ConcurrentLinkedQueue<Integer> stackSurfaceID = new ConcurrentLinkedQueue<Integer>();

	public ShellWindow(String windowName, Surface surface, ImgControler ic,BalloonImage bi) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.ic = ic;
		name = windowName;
		sf = surface;
		this.bi = bi;
		
		//ウィンドウの生成
		getJFrame();
		
		jFrame.setTitle(name);
		
		//ベースとなるイメージを取得
		img = sf.baseImg;
		
		//イメージの大きさを取得し、それによってウィンドウの大きさを決定
		y = img.getHeight();
		x = img.getWidth();
		//ウィンドウのinsetsを取得
		i = jFrame.getInsets();
		x = x + i.left + i.right;
		y = y + i.top + i.bottom;
		w_top = i.top;
		w_left = i.left;
		jFrame.setSize(x,y);
		
		//モニタの解像度を取得
		tk = jFrame.getToolkit();
		Dimension d = tk.getScreenSize();
		sSize_y = d.height;
		sSize_x = d.width;
		
		
		//表示するShellが何なのかによってウィンドウの位置を修正
		if(name.equals("sakura")){
			x = x + 50;
		}else if(name.equals("kero")){
			x = x + 500;
		}
		
		//スクリーン上のinsetsを取得
		i = jFrame.getToolkit().getScreenInsets(jFrame.getGraphicsConfiguration());
		int bottom = i.bottom;
		int right = i.right;
		
		
		windowPoint_x = sSize_x-x-right	;
		windowPoint_y = sSize_y-y-bottom;
		
		jFrame.setLocation(windowPoint_x,windowPoint_y);
		
		jFrame.setResizable(false);
		
		ic.getMGP().getMySplashWindow().setVisible(false);
		
		jFrame.setVisible(true);
		
		bl = new Balloon3(this, bi, name);
		
		//アニメーション用スレッドスタート
		t = new Timer();
		t.schedule(new Draw(), 100, 100);
		
	}

	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setUndecorated(true);
			//tb = new TransparentBackground(jFrame, ic.getUpdateControl());
			jFrame.setSize(new Dimension(200, 200));
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setContentPane(getJContentPane());
			//this.getJContentPane().add(tb);
			jFrame.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent arg0) {
					// TODO 自動生成されたメソッド・スタブ
					int clicked_x = arg0.getX();
					int clicked_y = arg0.getY()-i.top;
					int buttonNo = arg0.getButton()-1;
					int scopeNo = 0;
					
					if(name.equals("sakura")){
						scopeNo = 0;
					}else if(name.equals("kero")){
						scopeNo = 1;
					}
					
					for(int j = 0; j<sf.collisionId.size();j++){
						Integer[] index = sf.collision.get(j);
						if(index[0].intValue() < clicked_x && index[2].intValue() >clicked_x){
							if(index[1].intValue()<clicked_y && index[3].intValue()>clicked_y){
								
								res = ic.mgp.getResponder(ic.getSName());
								
								if(arg0.getClickCount() == 2){
									res.responseOnMouseDoubleClick(clicked_x, clicked_y, scopeNo, sf.collisionId.get(j), buttonNo);
								}else{
									res.responseOnMouseClick(clicked_x, clicked_y, scopeNo, sf.collisionId.get(j), buttonNo);
								}
								
							}
						}
					}
					
					
				}

				public void mouseEntered(MouseEvent arg0) {
					// TODO 自動生成されたメソッド・スタブ
					
				}

				public void mouseExited(MouseEvent arg0) {
					// TODO 自動生成されたメソッド・スタブ
					
				}

				public void mousePressed(MouseEvent arg0) {
					// TODO 自動生成されたメソッド・スタブ
					start_drag = getScreenLocation(arg0);
					start_loc = jFrame.getLocation();
				}

				public void mouseReleased(MouseEvent arg0) {
					// TODO 自動生成されたメソッド・スタブ
					
				}
				
			});
			jFrame.addMouseMotionListener(new MouseMotionListener(){

				public void mouseDragged(MouseEvent arg0) {
					// TODO 自動生成されたメソッド・スタブ
					Point current = getScreenLocation(arg0);
					Point offset = new Point(
							(int)current.getX()-(int)start_drag.getX(),
							(int)current.getY()-(int)start_drag.getY());
					Point new_location = new Point(
							(int)(start_loc.getX()+offset.getX()),
							(int)(start_loc.getY()+offset.getY()));
					jFrame.setLocation(new_location);
				}

				public void mouseMoved(MouseEvent arg0) {
					// TODO 自動生成されたメソッド・スタブ
					int clicked_x = arg0.getX();
					int clicked_y = arg0.getY()-i.top;
					int scopeNo = 0;
					
					if(name.equals("sakura")){
						scopeNo = 0;
					}else if(name.equals("kero")){
						scopeNo = 1;
					}
					
					for(int j = 0; j<sf.collisionId.size();j++){
						Integer[] index = sf.collision.get(j);
						if(index[0].intValue() < clicked_x && index[2].intValue() >clicked_x){
							if(index[1].intValue()<clicked_y && index[3].intValue()>clicked_y){
								
								res = ic.mgp.getResponder(ic.getSName());
								res.responseOnMouseMove(clicked_x, clicked_y, scopeNo, sf.collisionId.get(j));
								
							}
						}
					}
				}
				
			});
			jFrame.addMouseWheelListener(new MouseWheelListener(){

				public void mouseWheelMoved(MouseWheelEvent arg0) {
					// TODO 自動生成されたメソッド・スタブ
					int clicked_x = arg0.getX();
					int clicked_y = arg0.getY()-i.top;
					int wr = arg0.getWheelRotation();
					int scopeNo = 0;
					
					if(name.equals("sakura")){
						scopeNo = 0;
					}else if(name.equals("kero")){
						scopeNo = 1;
					}
					
					for(int j = 0; j<sf.collisionId.size();j++){
						Integer[] index = sf.collision.get(j);
						if(index[0].intValue() < clicked_x && index[2].intValue() >clicked_x){
							if(index[1].intValue()<clicked_y && index[3].intValue()>clicked_y){
								
								res = ic.mgp.getResponder(ic.getSName());
								res.responseOnMouseWheel(clicked_x, clicked_y, scopeNo, wr, sf.collisionId.get(j));
								
							}
						}
					}
				}
				
			});
			jFrame.pack();
		}
		return jFrame;
	}

	
	/**
	 * 発生したマウスイベントからマウスの現在の座標を絶対位置で返す
	 * @param arg0
	 * @return
	 */
	private Point getScreenLocation(MouseEvent arg0) {
		
		Point cursor = arg0.getPoint();
		Point target_location = getJContentPane().getLocationOnScreen();
		
		return new Point(
				(int)(target_location.getX() + cursor.getX()),
				(int)(target_location.getY() + cursor.getY()) );
		
	}
	
	
	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}
	
	class Draw extends TimerTask{
		
		@Override
		public void run() {
			/**
			 * Shellにアニメーションを行わせるループ。
			 */
			
			Graphics g = jFrame.getGraphics();
			
			Image back = jFrame.createImage(jFrame.getWidth(), jFrame.getHeight());
			Graphics g2 = back.getGraphics();
			
			
			
			String str = sf.interval;
			
			g2.drawImage(img, w_left, w_top, jFrame);
			/* 
			 * アニメーションパターンが１つ（ないしはそれ以下）しか存在しない場合、アニメーションを行う必要がないため
			 * 実質的にはアニメーションを行わずにループを抜ける。
			 */
			if(sf.anime.size()>1){
			//if(str != null){
				double t = 0;
				//intervalごとにアニメーションの出現頻度を算出する部分
				if(str != null & str.equals("sometimes")){
					t = Math.random()*1000;
				}
				g.clearRect(0, 0, x+10, y+20);
				
				if(950<t){
					for(int i = 0;i<sf.anime.size();i++){
						
						
						
						
						BufferedImage img2 = sf.anime.get(i);
						
						g2.drawImage(img2, sf.overlay_x.get(i)+w_left, sf.overlay_y.get(i)+w_top, jFrame);
						
						
						
						g.drawImage(back, w_left, w_top, jFrame);
						
						
						
						//System.out.println(i);
						//g.drawImage(img2, sf.overlay_x.get(i)+w_left, sf.overlay_y.get(i)+w_top, jFrame);
						
						try {
							Thread.sleep(sf.time.get(i)*10);
						} catch (InterruptedException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}
						
					}
				}else{
					g.drawImage(back, w_left, w_top, jFrame);
				}
			//}
			}else{
				g.drawImage(back, w_left, w_top, jFrame);
			}
			//g.drawImage(back, 0, 0, jFrame);
			
			g2.dispose();
			g.dispose();
			
		}
		
	}

	/**
	 * 感情の切り替え先を設定するメソッド。タイミングはchangeEmotion()メソッドで行う。
	 * @param surface surface[i]であらわされるサーフェスの種類
	 * @param surfaceNo 
	 */
	public void setEmotion(Surface surface, String surfaceNumber) {
		// TODO 自動生成されたメソッド・スタブ
		this.stackSurface.add(surface);
		this.isEmotionChange = true;
		this.stackSurfaceID.add(Integer.parseInt(surfaceNumber.substring(7)));
	}
	
	/**
	 * 感情の切り替えを行う。直前のsetEmotion()で設定されたサーフェスに変更。
	 */
	public void changeEmotion(){
		if(!stackSurface.isEmpty()){
			isEmotionChange = false;
			sf = stackSurface.poll();
			img = sf.baseImg;	
			surfaceNo = stackSurfaceID.poll();
		}
	}
	
	

	/**
	 * ウィンドウの現在のx座標を返す
	 * @return　x座標
	 */
	public int getWP_x(){
		return windowPoint_x;
	}
	
	/**
	 * ウィンドウの現在のy座標を返す
	 * @return　y座標
	 */
	public int getWP_y(){
		return windowPoint_y;
	}
	
	/**
	 * ウィンドウを入力されたピクセル分移動
	 */
	public void moveWindow(String s, int px){
		/**
		 * ウィンドウを1ピクセルづつ移動する。
		 * ウィンドウがディスプレイの両端を超えそうになったら移動を停止する。
		 */
		
		if(s.equals("LEFT")){	//左に移動
			for(; px>0; px--){
				try {
					if(jFrame.getLocation().x<=0){
						break;
					}
					jFrame.setLocation(jFrame.getLocation().x-1, jFrame.getLocation().y);
					Thread.sleep(10);
					
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		}else if(s.equals("RIGHT")){	//右に移動
			for(; px>0; px--){
				try {
					if(jFrame.getLocation().x+jFrame.getWidth()>=sSize_x){
						break;
					}
					jFrame.setLocation(jFrame.getLocation().x+1, jFrame.getLocation().y);
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 親であるicを返す
	 */
	public ImgControler getIC(){
		return ic;
	}
	
	
	/**
	 * ウィンドウを不可視にしたあと、
	 * 自分の削除を実行。
	 */
	public void delWindow() {
		// TODO 自動生成されたメソッド・スタブ
		jFrame.setVisible(false);
		jFrame = null;
		bl.delWindow();
		t.cancel();
		t = null;
		
	}
	
	/**
	 * ウィンドウの可視性を設定するメソッド
	 * @param b　trueかfalse。可視か不可視か。
	 */
	public void setVisible(boolean b){
		jFrame.setVisible(b);
	}
	
	public TransparentBackground getTB(){
		return tb;
	}

	public int getEmotion() {
		return surfaceNo;
	}
	
	public BalloonImage getBalloonImage(){
		return bi;
	}
	
	public Balloon3 getBalloon(){
		return bl;
	}

	public void clearBalloon() {
		bl.clearBalloon();
	}
	

}
