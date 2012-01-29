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
 * ShellWindow�𐶐�����N���X
 * Shell�͂��̃N���X���g���ĕ\������B
 * �l�ԂƂ̑Θb���s�����߂̃��C���ɂȂ�B
 */
public class ShellWindow {

	private JFrame jFrame = null;
	private JPanel jContentPane = null;
	//�����̖��O
	private String name;
	//�������g���T�[�t�F�X
	private Surface sf;
	//�������g���x�[�X�x�[�X�C���[�W
	private BufferedImage img;
	//�x�[�X�C���[�W�̑傫��
	private int y;
	private int x;
	//�T�[�t�F�X���ς�������ǂ����̃t���O
	private boolean isEmotionChange;
	private Toolkit tk;
	//Shell�ɉ�b������ׂ̃E�B���h�E(��R����)
	private Balloon3 bl;
	private BalloonImage bi;
	//�^�C�}�[
	private Timer t;
	//�E�B���h�E�̌��݈ʒu
	private int windowPoint_x;
	private int windowPoint_y;
	private Point start_drag;
	private Point start_loc;
	//�X�N���[���̑傫��
	private int sSize_y;
	private int sSize_x;
	//�E�B���h�E�̕��i�`����s���n�_�j
	private int w_top;
	private int w_left;
	//�e�ł���C���[�W�R���g���[��
	private ImgControler ic;
	//�����ȃE�B���h�E����������N���X
	private TransparentBackground tb;
	private Insets i;
	private Responder res;
	private int surfaceNo;
	private ConcurrentLinkedQueue<Surface> stackSurface = new ConcurrentLinkedQueue<Surface>();
	private ConcurrentLinkedQueue<Integer> stackSurfaceID = new ConcurrentLinkedQueue<Integer>();

	public ShellWindow(String windowName, Surface surface, ImgControler ic,BalloonImage bi) {
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
		this.ic = ic;
		name = windowName;
		sf = surface;
		this.bi = bi;
		
		//�E�B���h�E�̐���
		getJFrame();
		
		jFrame.setTitle(name);
		
		//�x�[�X�ƂȂ�C���[�W���擾
		img = sf.baseImg;
		
		//�C���[�W�̑傫�����擾���A����ɂ���ăE�B���h�E�̑傫��������
		y = img.getHeight();
		x = img.getWidth();
		//�E�B���h�E��insets���擾
		i = jFrame.getInsets();
		x = x + i.left + i.right;
		y = y + i.top + i.bottom;
		w_top = i.top;
		w_left = i.left;
		jFrame.setSize(x,y);
		
		//���j�^�̉𑜓x���擾
		tk = jFrame.getToolkit();
		Dimension d = tk.getScreenSize();
		sSize_y = d.height;
		sSize_x = d.width;
		
		
		//�\������Shell�����Ȃ̂��ɂ���ăE�B���h�E�̈ʒu���C��
		if(name.equals("sakura")){
			x = x + 50;
		}else if(name.equals("kero")){
			x = x + 500;
		}
		
		//�X�N���[�����insets���擾
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
		
		//�A�j���[�V�����p�X���b�h�X�^�[�g
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
					// TODO �����������ꂽ���\�b�h�E�X�^�u
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
					// TODO �����������ꂽ���\�b�h�E�X�^�u
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
			 * Shell�ɃA�j���[�V�������s�킹�郋�[�v�B
			 */
			
			Graphics g = jFrame.getGraphics();
			
			Image back = jFrame.createImage(jFrame.getWidth(), jFrame.getHeight());
			Graphics g2 = back.getGraphics();
			
			
			
			String str = sf.interval;
			
			g2.drawImage(img, w_left, w_top, jFrame);
			/* 
			 * �A�j���[�V�����p�^�[�����P�i�Ȃ����͂���ȉ��j�������݂��Ȃ��ꍇ�A�A�j���[�V�������s���K�v���Ȃ�����
			 * �����I�ɂ̓A�j���[�V�������s�킸�Ƀ��[�v�𔲂���B
			 */
			if(sf.anime.size()>1){
			//if(str != null){
				double t = 0;
				//interval���ƂɃA�j���[�V�����̏o���p�x���Z�o���镔��
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
							// TODO �����������ꂽ catch �u���b�N
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
	 * ����̐؂�ւ����ݒ肷�郁�\�b�h�B�^�C�~���O��changeEmotion()���\�b�h�ōs���B
	 * @param surface surface[i]�ł���킳���T�[�t�F�X�̎��
	 * @param surfaceNo 
	 */
	public void setEmotion(Surface surface, String surfaceNumber) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		this.stackSurface.add(surface);
		this.isEmotionChange = true;
		this.stackSurfaceID.add(Integer.parseInt(surfaceNumber.substring(7)));
	}
	
	/**
	 * ����̐؂�ւ����s���B���O��setEmotion()�Őݒ肳�ꂽ�T�[�t�F�X�ɕύX�B
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
	 * �E�B���h�E�̌��݂�x���W��Ԃ�
	 * @return�@x���W
	 */
	public int getWP_x(){
		return windowPoint_x;
	}
	
	/**
	 * �E�B���h�E�̌��݂�y���W��Ԃ�
	 * @return�@y���W
	 */
	public int getWP_y(){
		return windowPoint_y;
	}
	
	/**
	 * �E�B���h�E����͂��ꂽ�s�N�Z�����ړ�
	 */
	public void moveWindow(String s, int px){
		/**
		 * �E�B���h�E��1�s�N�Z���Âړ�����B
		 * �E�B���h�E���f�B�X�v���C�̗��[�𒴂������ɂȂ�����ړ����~����B
		 */
		
		if(s.equals("LEFT")){	//���Ɉړ�
			for(; px>0; px--){
				try {
					if(jFrame.getLocation().x<=0){
						break;
					}
					jFrame.setLocation(jFrame.getLocation().x-1, jFrame.getLocation().y);
					Thread.sleep(10);
					
				} catch (InterruptedException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}
			}
		}else if(s.equals("RIGHT")){	//�E�Ɉړ�
			for(; px>0; px--){
				try {
					if(jFrame.getLocation().x+jFrame.getWidth()>=sSize_x){
						break;
					}
					jFrame.setLocation(jFrame.getLocation().x+1, jFrame.getLocation().y);
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * �e�ł���ic��Ԃ�
	 */
	public ImgControler getIC(){
		return ic;
	}
	
	
	/**
	 * �E�B���h�E��s���ɂ������ƁA
	 * �����̍폜�����s�B
	 */
	public void delWindow() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		jFrame.setVisible(false);
		jFrame = null;
		bl.delWindow();
		t.cancel();
		t = null;
		
	}
	
	/**
	 * �E�B���h�E�̉�����ݒ肷�郁�\�b�h
	 * @param b�@true��false�B�����s�����B
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
