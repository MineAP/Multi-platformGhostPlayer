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
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.mineap.mpgp.window.shell.ShellWindow;

public class Balloon3 {
	
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
	public int setVisibleFalseCount = 0;
	//ウィンドウを不可視にするまでの時間　*50ミリ秒。
	private int waitTime = 100;
	//しゃべった内容を保存しておくログ
	public Vector<String> log = new Vector<String>();
	//行単位でしゃべり終わったかどうか
	public boolean isLineComplete;
	//入力された文字列をしゃべり終わったかどうか
	public boolean isTalkComplete = false;
	//以前までの入力で\eが存在するかどうか
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
	public int talkNextCount;
	
	private int temp = 0;
	
	
	/**
	 * コンストラクタ
	 * TalkWindowに替わる、ユーザーとのインタフェース。
	 * @param sw	対となるShellWindow
	 * @param bi	バルーンのイメージを保持するBalloonImageクラス
	 * @param cName	キャラクターの名前。sakuraかkeroか。
	 */
	public Balloon3(ShellWindow sw, BalloonImage bi, String cName){
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
								System.out.println("Clicked arrow0");
								
								if(originY < bi.originY){
									System.out.println("new originY set");
									originY = originY+14;
								}
							}
						}
						if(arg0.getX() > bi.arrow1IndexX && arg0.getX() < bi.arrow1IndexX+bi.arrows.get(0).getWidth()){
							if(arg0.getY() > img.getHeight()-20 + bi.arrow1IndexY && arg0.getY() < img.getHeight()-20 + bi.arrow1IndexY+bi.arrows.get(0).getWidth()){
								System.out.println("Clicked arrow1");
								
								if(originY + log.size()*14 > img.getHeight() + bi.validrectBottom){
									System.out.println("new originX set");
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
			
			
			/**
			 * キューの内容を確認
			 *
			if(cName.equals("sakura")){
				System.out.print("TalkSyncQueue is ");
				for(Iterator it = sw.getIC().getTalkSyncQueue().iterator() ; it.hasNext();){
					System.out.print(it.next());
				}
				System.out.println();
				System.out.print("TalkCountQueue is ");
				for(Iterator it = talkCountQueue.iterator() ; it.hasNext();){
					System.out.print(it.next());
				}
				System.out.println();
			}
			*/
			
			if(null != sw.getIC().getTalkSyncQueue().peek() && sw.getIC().getTalkSyncQueue().peek() == myScope ){
				jFrame.setVisible(true);
			}
			
			//フレームが表示されているかどうか
			if(jFrame.isVisible()){
				//自分にスコープが合っているかどうか
				if(null != sw.getIC().getTalkSyncQueue().peek() && sw.getIC().getTalkSyncQueue().peek() == myScope ){
					
					setVisibleFalseCount = 0;
					
					if(talkCountQueue.peek() != null && talkCountQueue.peek() <= talkNextCount){
						talkNextCount = 0;
						talkCountQueue.poll();
						sw.getIC().getTalkSyncQueue().remove();
					}else{
						//文字描画の開始点の設定
						if(log.size()+2>=(img.getHeight())/14 && !isTalkComplete){
							arrowFlag = true;
							int x = log.size()-img.getHeight()/14+3;
							originY = bi.originY-x*14;
						}else if(!isTalkComplete){
							arrowFlag = false;
							originY = bi.originY;
						}
						
						Graphics g = jFrame.getGraphics();
						
						Image backImage = jFrame.createImage(img.getWidth(), img.getHeight()*4);
						Graphics g2 = backImage.getGraphics();
						
						g2.drawImage(img, windowTop, windowLeft, jFrame);
						
						if(setVisibleFalseCount > 0){
							--setVisibleFalseCount;
						}
						
						
						//文字列出力ループ
						if(isTalkComplete){
							setVisibleFalseCount++;
							//会話の交代を行う
							sw.getIC().getTalkSyncQueue().remove();
							for(int i=0; i<log.size(); i++){
								g2.drawString(log.elementAt(i), bi.originX, originY+windowTop+14*(1+i));
							}
						}else{
							if(talkIndexY > 0){
								for(int i=0; i<talkIndexY; i++){
									g2.drawString(log.elementAt(i), bi.originX, originY+windowTop+14*(1+i));
								}
							}
							
							String str = "";
							str += talkArray.elementAt(talkIndexY).substring(0, talkIndexX);
							g2.drawString(str, bi.originX, originY+windowTop+14*(1+talkIndexY));
							allCharactersCount++;
							talkNextCount++;
							if(talkIndexX+1 > talkArray.get(talkIndexY).length()){
								log.add(str);
								isLineComplete = true;
							}
						}
						
						//System.out.println("M = " + motion.peek() +", ACC = "+allCharactersCount );
						
						if(!motion.isEmpty() && allCharactersCount-1 > motion.peek()){
							//System.out.println("change");
							sw.changeEmotion();
							motion.poll();
							allCharactersCount = 0;
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
							if(talkIndexY >= talkArray.size()){
								talkIndexY = 0;
								
							}else{
								talkIndexY++;
							}
							talkIndexX = 0;
							isLineComplete = false;
						}
					}
				}
			}
			
			//カウントをインクリメント
			setVisibleFalseCount++;
			
			/*
			System.out.println("SVFT:"+setVisibleFalseCount);
			System.out.println("WT:"+waitTime);
			*/
			
			//sw.getIC().isVFCIsZero();
			sw.getIC().isTalkComplete();
			
			//System.out.println(cName + ":" + setVisibleFalseCount);
			
			if(setVisibleFalseCount > waitTime){
				
				isTalkComplete = true;
				
				if(sw.getIC().isTalkComplete()){
					jFrame.setVisible(false);
					setVisibleFalseCount = 0;
					balloonReset();
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
		
		//表示するバルーンを場合によってかえる
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
					
					//サーフェス変更用キューに挿入
					motion.add(str.substring(index).length());
					
					while(null != enNQueue.peek()){
						enNQueue.poll();
						talkArray.addElement("");
						System.out.println("空白行を追加");
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
						System.out.println("空白行を追加");
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
		isTalkComplete = false;
		
		if(isSSTP){
			this.isSSTP = true;
			this.isSSTPmessage = true;
		}
		
		for(int n=0; n<talkArray.size(); n++){		
			System.out.println("-->"+talkArray.elementAt(n));
		}
		
		
		eniFlag = false;
		
		return true;
		
	}

	/**
	 * バルーンの表示領域をクリアする
	 */
	public void clearBalloon() {
		// TODO 自動生成されたメソッド・スタブ
		this.talkIndexY = 0;
		this.talkIndexX = 0;
		this.talkArray.clear();
		//backImage = null;
	}
	
	/**
	 * バルーンの可視・不可視を設定する
	 * @param b setVisible()でつかう値　ブーリアン型
	 */
	public void setVisible(boolean b){
		jFrame.setVisible(b);
	}

	public void delWindow() {
		// TODO 自動生成されたメソッド・スタブ
		jFrame.setVisible(false);
		jFrame = null;
	}
	
	/**
	 * Balloonをリセットする
	 */
	public void balloonReset(){
		talkArray.clear();
		motion.clear();
		log.clear();
		eniFlag = true;
		talkIndexX = 0;
		talkIndexY = 0;
		isLineComplete = false;
		//isTalkComplete = false;
	}
	
	
}
