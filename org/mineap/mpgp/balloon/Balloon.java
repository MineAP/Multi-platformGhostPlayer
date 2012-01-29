package org.mineap.mpgp.balloon;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.mineap.mpgp.window.shell.ShellWindow;

public class Balloon {
	
	//バルーンに使うイメージを保持
	private BalloonImage bi;
	//対となるサーフェス
	private ShellWindow sw;
	//自身を表示するウィンドウ
	private JFrame jFrame;
	//キャラクターネーム
	private String cName;
	//バーなしでのドラッグ＆ドロップを実現する
	private Point start_drag;
	private Point start_loc;
	//パネル
	private JPanel jContentPane;
	//アニメーション用タイマー
	private Timer t;
	//自分がSakuraかどうか
	private int myScope = 0;
	//使用するバルーンのイメージ
	private BufferedImage img;
	//ウィンドウのパーツの大きさ
	private int windowTop;
	private int windowLeft;
	//各種表示に関するフラグ
	private boolean isSSTP;
	private boolean isSSTPmessage;
	private boolean isOnline;
	private boolean arrowFlag = false;
	//今何行目まで喋りが進んでいるか。
	private int talkIndexY = 0;
	//今何文字目まで喋りが進んでいるか。
	private int talkIndexX = 0;
	private boolean isNumber;
	//喋る内容をStringにして各行ごとに保持
	private Vector<String> talkArray = new Vector<String>();
	//ウィンドウを不可視にするまでのカウント
	private int setVisibleFalseCount = 0;
	//ウィンドウを不可視にするまでの時間　*100ミリ秒。
	private int waitTime = 5;
	//しゃべった内容を保存しておくログ
	public Vector<String> log = new Vector<String>();
	//行単位でしゃべり終わったかどうか
	public boolean isLineComplete;
	//入力された文字列をしゃべり終わったかどうか
	public boolean isTalkComplete = false;
	//以前までの入力で\eが存在するかどうか。上のやつとどう違うんだ？
	public boolean eniFlag = true;
	//出力した合計文字数　サーフェスの切り替えのタイミングに用いる
	public int allCharactersCount;
	//サーフェス側のサーフェス切り替えに用いるキュー
	private ConcurrentLinkedQueue<Integer> motion = new ConcurrentLinkedQueue<Integer>();
	private ConcurrentLinkedQueue<Boolean> enNQueue = new ConcurrentLinkedQueue<Boolean>();
	//talkSyncQueueをpollするまでの出力文字数を格納するキュー
	private ConcurrentLinkedQueue<Integer> talkCountQueue = new ConcurrentLinkedQueue<Integer>();
	//文字描画開始位置
	private int originY;
	//Talk権限があるかどうか
	private int isTalkable;
	//
	public int talkNextCount;
	//
	public boolean tSQpollFlag;
	
	/**
	 * コンストラクタ
	 * TalkWindowに替わる、ユーザーとのインタフェース。
	 * @param sw	対となるShellWindow
	 * @param bi	バルーンのイメージを保持するBalloonImageクラス
	 * @param cName	キャラクターの名前。sakuraかkeroか。
	 */
	public Balloon(ShellWindow sw, BalloonImage bi, String cName){
		this.bi = bi;
		this.sw = sw;
		this.cName = cName;
		
		getNewJFrame();
		
		windowTop = jFrame.getInsets().top;
		windowLeft = jFrame.getInsets().left;
		
		if(cName.equals("sakura")){
			myScope = 0;
		}else{
			myScope = 1;
		}
		
		
		//アニメーション用スレッドスタート
		t = new Timer();
		t.schedule(new Draw(), 100, 100);
		
		
	}

	/**
	 * ウィンドウを生成。
	 * ウィンドウ全体を使ったマウスドラッグに対応。
	 * @return
	 */
	private JFrame getNewJFrame() {
		// TODO 自動生成されたメソッド・スタブ
		if(jFrame == null){
			jFrame = new JFrame();
			jFrame.setUndecorated(true);
			jFrame.setTitle(cName+"-TalkWindow");
			jFrame.setSize(200, 200);
			jFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			jFrame.setContentPane(getJContentPane());
			jFrame.setResizable(false);
			jFrame.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent arg0) {
					// TODO 自動生成されたメソッド・スタブ
					
					//arrowのクリックを検出する
					if(arrowFlag){
						if(arg0.getX() > bi.arrow0IndexX && arg0.getX() < bi.arrow0IndexX+bi.arrows.get(0).getWidth()){
							if(arg0.getY() > bi.arrow0IndexY && arg0.getY() < bi.arrow0IndexY+bi.arrows.get(0).getWidth()){
								//System.out.println("Clicked arrow0");
								
								if(originY < bi.originY){
									originY = originY+14;
								}
							}
						}
						if(arg0.getX() > bi.arrow1IndexX && arg0.getX() < bi.arrow1IndexX+bi.arrows.get(0).getWidth()){
							if(arg0.getY() > img.getHeight()-20 + bi.arrow1IndexY && arg0.getY() < img.getHeight()-20 + bi.arrow1IndexY+bi.arrows.get(0).getWidth()){
								//System.out.println("Clicked arrow1");
								
								if(originY + log.size()*14 > img.getHeight() + bi.validrectBottom){
									originY = originY-14;
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
					
				}
			});
			jFrame.pack();
		}
		
		return jFrame;
	}

	/**
	 * パネル。
	 * @return
	 */
	private JPanel getJContentPane() {
		// TODO 自動生成されたメソッド・スタブ
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
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
	 * アニメーションを行う内部クラス。
	 * 
	 * @author shiraminekeisuke
	 *
	 */
	class Draw extends TimerTask{


		@Override
		public void run() {
			/**
			 * Shellにアニメーションを行わせるループ。
			 */
			
			//if(jFrame.isVisible()){
			
			if(sw.getIC().getTalkSyncQueue().peek() != null && sw.getIC().getTalkSyncQueue().peek() == myScope){
				
				
				jFrame.setVisible(true);
				
				//キャラクター同士で同期をとるための処理
				if(talkCountQueue.peek() != null && talkCountQueue.peek() <= talkNextCount){
					
					//トークの進捗状況をリセット
					talkNextCount = 0;
					//トークの進捗状況の最大値を保持するtalkCountQueueをpollする
					talkCountQueue.poll();
					//会話の同期をとるためのTalkSyncQueueをpollして次の会話へ
					sw.getIC().getTalkSyncQueue().poll();
					
				}else{
					//キャラクターのサーフェスの変更を指示する。出力文字数がmotionで指定された文字数より大きくなったときに実施。
					if(sw.getIC().getTalkSyncQueue().peek() != null && sw.getIC().getTalkSyncQueue().peek() == myScope && !motion.isEmpty() && allCharactersCount > motion.peek()){
						
						//サーフェスの切り替えを指示。切り替え後のサーフェスはShellWindowが直前に指定されたもの。
						sw.changeEmotion();
						//自分のモーションの完了を通知。
						motion.poll();
						//文字数カウントをリセット。
						allCharactersCount = 0;
					}
				}
			}
			if(jFrame.isVisible()){
				
				//文字描画の開始点の設定
				if(!log.isEmpty() && log.size()+2>=(img.getHeight())/14 && !isTalkComplete){
					arrowFlag = true;
					int x = log.size()-img.getHeight()/14+3;
					originY = bi.originY-x*14;
				}else if(!isTalkComplete){
					arrowFlag = false;
					originY = bi.originY;
				}
				
				//指定された待ち時間よりもカウントが大きくなったときウィンドウを不可視に設定し、その他いろいろをリセット。
				if(setVisibleFalseCount > waitTime){
					setVisibleFalseCount = 0;
					jFrame.setVisible(false);
					
					talkArray.clear();
					motion.clear();
					log.clear();
					eniFlag = true;
					talkIndexX = 0;
					talkIndexY = 0;
					isLineComplete = false;
					isTalkComplete = false;
					
				}
				
				
				Graphics g = jFrame.getGraphics();
				
				Image backImage = jFrame.createImage(img.getWidth(), img.getHeight()*4);
				Graphics g2 = backImage.getGraphics();
				
				g2.drawImage(img, windowTop, windowLeft, jFrame);
				
				//文字列出力ループ
				if(isTalkComplete){
					//文字列の出力が最後まで終わっているときの動作
					setVisibleFalseCount++;
					//ログの中身をすべて書き出して終了
					for(int i=0; i<log.size(); i++){
						g2.drawString(log.elementAt(i), bi.originX, originY+windowTop+14*(1+i));
					}
				}else{
					//文字列の出力が最後まで終わっていないときの動作
					//ログの中身をtalkIndexYのインデックスまで書き出す
					for(int i=0; i<talkIndexY; i++){
						g2.drawString(log.elementAt(i), bi.originX, originY+windowTop+14*(1+i));
					}
					
					//talkIndexXのインデクスまでを書き出す
					String str = "";
					str += talkArray.elementAt(talkIndexY).substring(0, talkIndexX);
					g2.drawString(str, bi.originX, originY+windowTop+14*(1+talkIndexY));
				
					allCharactersCount++;
					talkNextCount++;
					
					//もし、その行書き出しを終えたのなら、その行をlogに追加する。
					if(talkIndexX+1 > talkArray.get(talkIndexY).length()){
						log.add(str);
						isLineComplete = true;
					}
					
					
				}
				
				//下の文字非描画領域を上書きして見えないようにする。
				Image img2 = img.getSubimage(0, img.getHeight()+bi.validrectBottom, img.getWidth(), bi.validrectBottom*-1);
				g2.drawImage(img2, 0, img.getHeight()+bi.validrectBottom, jFrame);
				//arrowを表示するかどうか
				if(arrowFlag){
					g2.drawImage(bi.arrows.elementAt(0), bi.arrow0IndexX, bi.arrow0IndexY, jFrame);
					g2.drawImage(bi.arrows.elementAt(1), bi.arrow1IndexX, img.getHeight()-20 + bi.arrow1IndexY, jFrame);
				}
				//SSTPマーカの表示に関する部分
				if(isSSTP){
					//g2.drawImage();
				}
				//オンラインマーカの表示に関する部分
				if(isOnline){
					
				}
				//sstpmessageの表示に関する部分
				if(isSSTPmessage){
					
				}
				//numberの表示に関する部分
				if(isNumber){
					
				}
				
				g.drawImage(backImage, 0, 0, jFrame);
				
				g2.dispose();
				
				g.dispose();
				
				talkIndexX++;
				
				if(isLineComplete){
					//行の書き出しが終了していたら、
					if(talkIndexY+1 >= talkArray.size()){
						//文字列全体の終了が完了したことを通知するか
						talkIndexY = 0;
						isTalkComplete = true;
					}else{
						//次の行の書き出しへ移行するように通知する
						talkIndexY++;
					}
					//横のインデクスをリセット
					talkIndexX = 0;
					//lineの読み込み完了に関するフラグをリセット
					isLineComplete = false;
				}
				
			}	
			
		}
	}
	
	/**
	 * 渡された文字列を描画する。
	 * @param str	描画する文字列。
	 * @param windowSize	文字列の種類。ウィンドウの大きさ指定。
	 * @param isSSTP	SSTPによる喋りかどうか。
	 * @return
	 */
	public boolean drawString(String str, int windowSize, boolean isSSTP){
		
		char en = '\u00A5';
		char bs = '\\';
		str = str.replace(en, bs);
		
		if(jFrame.isVisible()){
		//	clearBalloon();
		}
		
		this.talkCountQueue.add(str.length());
		
		if(windowSize == 0){
			if(myScope == 0){
				img = bi.ballonsGraphics.elementAt(0);
			}else if(myScope == 1){
				img = bi.ballonkGraphics.elementAt(0);
			}
			
		}else if(windowSize == 1){
			
		}
		int index = 0;
		while(true){
			int temp = str.indexOf("\n",index);
			
			//\nだったときの処理
			if(temp != -1){
				
				enNQueue.add(true);
				
				break;
				
			}else{
				int talkArrayIndex = 0;

				if(!eniFlag){
					
					//\eがないので、最後のインデックスの文章に新しい文章を追加。
					
					motion.add(str.substring(index).length());
					
					while(null != enNQueue.peek()){
						enNQueue.poll();
						talkArray.addElement("");
						//System.out.println("空白行を追加");
					}
					
					talkArray.set(talkArray.size()-1, talkArray.lastElement() + str.substring(index));
					
					//改行処理
					while(talkArray.lastElement().length()>img.getWidth()/15){
						int n = img.getWidth()/15;
						String tempString = talkArray.lastElement().substring(n,talkArray.lastElement().length());
						//System.out.println(tempString);
						talkArray.set(talkArray.indexOf(talkArray.lastElement()), talkArray.lastElement().substring(0,n));
						talkArray.insertElementAt(tempString, talkArray.size()+talkArrayIndex);
					}
						
				}else{
					
					//\eがあったので、talkArrayの最後のインデックスの次に文章を追加。
					
					while(null != enNQueue.peek()){
						enNQueue.poll();
						talkArray.addElement("");
						//System.out.println("空白行を追加");
					}
					
					motion.add(str.substring(index).length());
					talkArray.insertElementAt(str.substring(index), talkArray.size());
					
					//改行処理
					while(talkArray.lastElement().length()>img.getWidth()/15){
						int n = img.getWidth()/15;
						String tempString = talkArray.lastElement().substring(n,talkArray.lastElement().length());
						//System.out.println(tempString);
						talkArray.set(talkArray.indexOf(talkArray.lastElement()), talkArray.lastElement().substring(0,n));
						talkArray.insertElementAt(tempString, talkArray.size()+talkArrayIndex);
					}
				}
				break;
			}
		}
		
		int y = img.getHeight();
		int x = img.getWidth();
		
		Insets i = jFrame.getInsets();
		x = x + i.left + i.right;
		y = y + i.top + i.bottom;
		
		jFrame.setSize(x,y);
		jFrame.setLocation(sw.getWP_x()-jFrame.getWidth(), sw.getWP_y());
		
		//jFrame.setVisible(true);
		
		if(isSSTP){
			this.isSSTP = true;
			this.isSSTPmessage = true;
		}
		
		/*
		for(int n=0; n<talkArray.size(); n++){		
			System.out.println("-->"+talkArray.elementAt(n));
		}
		*/
		
		eniFlag = false;
		
		return true;
		
	}

	public void clearBalloon() {
		// TODO 自動生成されたメソッド・スタブ
		this.talkIndexY = 0;
		this.talkIndexX = 0;
		this.talkArray.clear();
		//backImage = null;
	}
	
	public void setVisible(boolean b){
		jFrame.setVisible(b);
	}

	public void delWindow() {
		// TODO 自動生成されたメソッド・スタブ
		jFrame.setVisible(false);
		jFrame = null;
	}
	
	
}
