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
 * �����ȃE�B���h�E���U�����邽�߂̃o�b�N�O���E���h�̊G��񋟂���
 * TransparentBackground�N���X
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
	//�����̃E�B���h�E�����ꍇ�Ƀt�H�[�J�X���A�v���P�[�V�����P�ʂŊǗ����邽�߂̂���
	private UpdateControl uc;
	//�㏑���^�C�~���O�̎擾��UpdateControl���g�����ǂ���
	private boolean isUseUC;
	private boolean isOnFocus;
	private int updateExceptionFlag;

	/**
	 * �R���X�g���N�^�B�����Ɍ��������t���[����n���B
	 * @param jframe�@�����Ɍ��������t���[��
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
	 * �R���X�g���N�^�B�����Ɍ��������t���[���ƁA�E�B���h�E������Ǘ�����UpdateControl��n���B
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
	 * �w�i���X�V����
	 * Robot�N���X���g���ăL���v�`��������Abackground�ɕۑ�����B
	 */
	private void updateBackground() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
	 * ���̃R���|�[�l���g�̕`�悪�K�v�ɂȂ����Ƃ���Java�V�X�e������Ăяo�����B
	 * �i ���Ƃ��΍ŏ��ɕ\�����鎞�A�ŏ���������̍ĕ\���A ���̃E�B���h�E�̉��ɉB�ꂽ���
	 * �ēx�A�N�e�B�u�ɂȂ������A �R���|�[�l���g�̃T�C�Y���ύX���ꂽ�� ���j
	 * �R���|�[�l���g�̈ʒu���擾���A�w�i��`�悷��B
	 */
	public void paintComponent(Graphics g){
		Point pos = this.getLocationOnScreen();
		Point offset = new Point(-pos.x,-pos.y);
		g.drawImage(background, offset.x, offset.y, null);
		
	}

	public void componentHidden(ComponentEvent arg0) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}

	public void componentMoved(ComponentEvent arg0) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		repaint();
		
	}

	public void componentResized(ComponentEvent arg0) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		repaint();
		
	}

	public void componentShown(ComponentEvent arg0) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		repaint();
		
	}

	public void windowGainedFocus(WindowEvent arg0) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
	 * 1/4�b���ƂɃ��t���b�V�����v������Ă��Ȃ����ǂ������ׂ�B
	 * �܂��A�O��̃��t���b�V������P�b�ȏ�o�߂��Ă��āA���t���[�������ł����
	 * �w�i���擾�������ă��t���b�V�����s���B
	 */
	public void run() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
	 * �E�B���h�E�����ł��鎖���m�F���Ă���repaint()�����s
	 */
	private void refresh() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		if(jFrame.isVisible()){
			repaint();
			refreshRequested = true;
			lastupdate = new Date().getTime();
		}
		
		
	}

	public boolean getIsOnFocus() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return isOnFocus;
	}
	
}
