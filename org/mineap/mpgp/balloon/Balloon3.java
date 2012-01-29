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
	
	//�o���[���Ɏg���C���[�W��ێ�
	private BalloonImage bi;
	//�΂ƂȂ�T�[�t�F�X
	private ShellWindow sw;
	//���g��\������E�B���h�E
	private JFrame jFrame;
	//�L�����N�^�[�l�[��
	private String cName;
	//�o�[�Ȃ��ł̃h���b�O���h���b�v����������
	private Point start_drag;
	private Point start_loc;
	//�p�l��
	private JPanel jContentPane;
	//�A�j���[�V�����p�^�C�}�[
	private Timer t;
	//������Sakura���ǂ���
	private int myScope = 0;
	//�g�p����o���[���̃C���[�W
	private BufferedImage img;
	//�E�B���h�E�̃p�[�c�̑傫��
	private int windowTop;
	private int windowLeft;
	//�e��\���Ɋւ���t���O
	private boolean isSSTP;
	private boolean isSSTPmessage;
	private boolean isOnline;
	private boolean arrowFlag = false;
	//�����s�ڂ܂Œ��肪�i��ł��邩�B
	private int talkIndexY = 0;
	//���������ڂ܂Œ��肪�i��ł��邩�B
	private int talkIndexX = 0;
	private boolean isNumber;
	//������e��String�ɂ��Ċe�s���Ƃɕێ�
	private Vector<String> talkArray = new Vector<String>();
	//�E�B���h�E��s���ɂ���܂ł̃J�E���g
	public int setVisibleFalseCount = 0;
	//�E�B���h�E��s���ɂ���܂ł̎��ԁ@*50�~���b�B
	private int waitTime = 100;
	//����ׂ������e��ۑ����Ă������O
	public Vector<String> log = new Vector<String>();
	//�s�P�ʂł���ׂ�I��������ǂ���
	public boolean isLineComplete;
	//���͂��ꂽ�����������ׂ�I��������ǂ���
	public boolean isTalkComplete = false;
	//�ȑO�܂ł̓��͂�\e�����݂��邩�ǂ���
	public boolean eniFlag = true;
	//�o�͂������v�������@�T�[�t�F�X�̐؂�ւ��̃^�C�~���O�ɗp����
	public int allCharactersCount;
	//�T�[�t�F�X���̃T�[�t�F�X�؂�ւ��ɗp����L���[
	private ConcurrentLinkedQueue<Integer> motion = new ConcurrentLinkedQueue<Integer>();
	private ConcurrentLinkedQueue<Boolean> enNQueue = new ConcurrentLinkedQueue<Boolean>();
	//talkSyncQueue��poll����܂ł̏o�͕��������i�[����L���[
	private ConcurrentLinkedQueue<Integer> talkCountQueue = new ConcurrentLinkedQueue<Integer>();
	//�����`��J�n�ʒu
	private int originY;
	//Talk���������邩�ǂ���
	private int isTalkable;
	public int talkNextCount;
	
	private int temp = 0;
	
	
	/**
	 * �R���X�g���N�^
	 * TalkWindow�ɑւ��A���[�U�[�Ƃ̃C���^�t�F�[�X�B
	 * @param sw	�΂ƂȂ�ShellWindow
	 * @param bi	�o���[���̃C���[�W��ێ�����BalloonImage�N���X
	 * @param cName	�L�����N�^�[�̖��O�Bsakura��kero���B
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
		
		
		//�A�j���[�V�����p�X���b�h�X�^�[�g
		t = new Timer();
		t.schedule(new Draw(), 100, 100);
		
		
	}

	/**
	 * �E�B���h�E�𐶐��B
	 * �E�B���h�E�S�̂��g�����}�E�X�h���b�O�ɑΉ��B
	 * @return
	 */
	private JFrame getNewJFrame() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
					// TODO �����������ꂽ���\�b�h�E�X�^�u
					
					//arrow�̃N���b�N�����o����
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
					// TODO �����������ꂽ���\�b�h�E�X�^�u
					
				}

				public void mouseExited(MouseEvent arg0) {
					// TODO �����������ꂽ���\�b�h�E�X�^�u
					
				}

				public void mousePressed(MouseEvent arg0) {
					// TODO �����������ꂽ���\�b�h�E�X�^�u
					start_drag = getScreenLocation(arg0);
					start_loc = jFrame.getLocation();
				}

				public void mouseReleased(MouseEvent arg0) {
					// TODO �����������ꂽ���\�b�h�E�X�^�u
					
				}
			});

			jFrame.addMouseMotionListener(new MouseMotionListener(){

				public void mouseDragged(MouseEvent arg0) {
					// TODO �����������ꂽ���\�b�h�E�X�^�u
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
					// TODO �����������ꂽ���\�b�h�E�X�^�u
					
				}
			});
			jFrame.pack();
		}
		
		return jFrame;
	}

	/**
	 * �p�l���B
	 * @return
	 */
	private JPanel getJContentPane() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}
	
	/**
	 * ���������}�E�X�C�x���g����}�E�X�̌��݂̍��W���Έʒu�ŕԂ�
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
	 * �A�j���[�V�������s�������N���X�B
	 * 
	 * @author shiraminekeisuke
	 *
	 */
	class Draw extends TimerTask{


		@Override
		public void run() {
			/**
			 * Shell�ɃA�j���[�V�������s�킹�郋�[�v�B
			 */
			
			
			/**
			 * �L���[�̓��e���m�F
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
			
			//�t���[�����\������Ă��邩�ǂ���
			if(jFrame.isVisible()){
				//�����ɃX�R�[�v�������Ă��邩�ǂ���
				if(null != sw.getIC().getTalkSyncQueue().peek() && sw.getIC().getTalkSyncQueue().peek() == myScope ){
					
					setVisibleFalseCount = 0;
					
					if(talkCountQueue.peek() != null && talkCountQueue.peek() <= talkNextCount){
						talkNextCount = 0;
						talkCountQueue.poll();
						sw.getIC().getTalkSyncQueue().remove();
					}else{
						//�����`��̊J�n�_�̐ݒ�
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
						
						
						//������o�̓��[�v
						if(isTalkComplete){
							setVisibleFalseCount++;
							//��b�̌����s��
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
						
						//���̕�����`��̈���㏑�����Č����Ȃ��悤�ɂ���B
						Image img2 = img.getSubimage(0, img.getHeight()+bi.validrectBottom, img.getWidth(), bi.validrectBottom*-1);
						g2.drawImage(img2, 0, img.getHeight()+bi.validrectBottom, jFrame);
						//arrow��\�����邩�ǂ���
						if(arrowFlag){
							g2.drawImage(bi.arrows.elementAt(0), bi.arrow0IndexX, bi.arrow0IndexY, jFrame);
							g2.drawImage(bi.arrows.elementAt(1), bi.arrow1IndexX, img.getHeight()-20 + bi.arrow1IndexY, jFrame);
						}
						//SSTP�}�[�J�̕\���Ɋւ��镔��
						if(isSSTP){
							//g2.drawImage();
						}
						//�I�����C���}�[�J�̕\���Ɋւ��镔��
						if(isOnline){
							
						}
						//sstpmessage�̕\���Ɋւ��镔��
						if(isSSTPmessage){
							
						}
						//number�̕\���Ɋւ��镔��
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
			
			//�J�E���g���C���N�������g
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
	 * �n���ꂽ�������`�悷��B
	 * @param str	�`�悷�镶����B
	 * @param windowSize	������̎�ށB�E�B���h�E�̑傫���w��B
	 * @param isSSTP	SSTP�ɂ�钝�肩�ǂ����B
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
		
		//�\������o���[�����ꍇ�ɂ���Ă�����
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
			
			//\n�������Ƃ��̏���
			if(temp != -1){
				
				enNQueue.add(true);
				
				break;
				
			}else{
				int talkArrayIndex = 0;

				if(!eniFlag){
					
					//\e���Ȃ��̂ŁA�Ō�̃C���f�b�N�X�̕��͂ɐV�������͂�ǉ��B
					
					//�T�[�t�F�X�ύX�p�L���[�ɑ}��
					motion.add(str.substring(index).length());
					
					while(null != enNQueue.peek()){
						enNQueue.poll();
						talkArray.addElement("");
						System.out.println("�󔒍s��ǉ�");
					}
					
					talkArray.set(talkArray.size()-1, talkArray.lastElement() + str.substring(index));
					
					//���s����
					while(talkArray.lastElement().length()>img.getWidth()/15){
						int n = img.getWidth()/15;
						String tempString = talkArray.lastElement().substring(n,talkArray.lastElement().length());
						//System.out.println(tempString);
						talkArray.set(talkArray.indexOf(talkArray.lastElement()), talkArray.lastElement().substring(0,n));
						talkArray.insertElementAt(tempString, talkArray.size()+talkArrayIndex);
					}
					
				}else{
					
					//\e���������̂ŁAtalkArray�̍Ō�̃C���f�b�N�X�̎��ɕ��͂�ǉ��B
					
					while(null != enNQueue.peek()){
						enNQueue.poll();
						talkArray.addElement("");
						System.out.println("�󔒍s��ǉ�");
					}
					
					motion.add(str.substring(index).length());
					talkArray.insertElementAt(str.substring(index), talkArray.size());
					
					//���s����
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
	 * �o���[���̕\���̈���N���A����
	 */
	public void clearBalloon() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		this.talkIndexY = 0;
		this.talkIndexX = 0;
		this.talkArray.clear();
		//backImage = null;
	}
	
	/**
	 * �o���[���̉��E�s����ݒ肷��
	 * @param b setVisible()�ł����l�@�u�[���A���^
	 */
	public void setVisible(boolean b){
		jFrame.setVisible(b);
	}

	public void delWindow() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		jFrame.setVisible(false);
		jFrame = null;
	}
	
	/**
	 * Balloon�����Z�b�g����
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