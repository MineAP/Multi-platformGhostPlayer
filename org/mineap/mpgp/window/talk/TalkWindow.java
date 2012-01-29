package org.mineap.mpgp.window.talk;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.mineap.mpgp.window.shell.ShellWindow;

/**
 * @author shiraminekeisuke
 *
 */
public class TalkWindow {

	private JFrame jFrame = null;
	private JPanel jContentPane = null;
	private JTextArea jTextArea = null;
	private JScrollPane jScrollPane = null;
	private JEditorPane jEditorPane = null;
	private ShellWindow sw;
	private String name;

	/**
	 * �R���X�g���N�^
	 * �E�B���h�E�̐��������s�B�i�f�t�H���g�ł͌����Ȃ����Ƃɒ��Ӂj
	 * @param name 
	 */
	TalkWindow(ShellWindow window, String name){
		this.sw = window;
		this.name = name;
		jFrame = getNewJFrame();
		
	}
	
	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	private JFrame getNewJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setSize(new Dimension(281, 272));
			jFrame.setTitle(name+"-TalkWindow");
			jFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			jFrame.setContentPane(getNewJContentPane());
			jFrame.setResizable(false);
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNewJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			//jScrollPane.setViewportView(getNewJTextArea());
			jScrollPane.setViewportView(getNewJEditorPane(""));
		}
		return jScrollPane;
	}
	
	
	/**
	 * html�𗘗p����������񋟂���B����ɂ���đI�����ɂ��Ή��B
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getNewJEditorPane(String text){
		if(jEditorPane == null){
			jEditorPane = new JEditorPane("text/html",text);
			jEditorPane.setEditable(false);
			jEditorPane.addHyperlinkListener(new HyperlinkListener(){

				public void hyperlinkUpdate(HyperlinkEvent arg0) {
					// TODO �����������ꂽ���\�b�h�E�X�^�u
					String id = null;
					
					int index = arg0.getDescription().indexOf(",");
					
					//,�����o����Ȃ���΂����OnAnchorSelect���Ɣ���
					if(index == -1){
						if(arg0.getEventType() == HyperlinkEvent.EventType.ENTERED){
							id = arg0.getDescription();
							sw.getIC().mgp.getResponder(sw.getIC().name).responseOnAnchorSelect(id);
						}
					}//����ȊO�̏ꍇ��OnChoice~���Ɣ���
					else{
						//�n�C�p�[�����N���N���b�N���ꂽ�Ƃ�
						if (arg0.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
							//�N���b�N���ꂽ�n�C�p�[�����N�ɏ�����Ă����l��,�܂ł�ID�Ƃ��Ď擾
							id = arg0.getDescription().substring(0, index);
							sw.getIC().mgp.getResponder(sw.getIC().name).responseOnChoiceSelect(id);
						}//�n�C�p�[�����N��Ƀ}�E�X�J�[�\�����������Ƃ�
						else if(arg0.getEventType() == HyperlinkEvent.EventType.ENTERED){
							id = arg0.getDescription().substring(0, index);
							//�N���b�N���ꂽ�n�C�p�[�����N�ɏ�����Ă����l��,�̌���^�C�g���Ƃ��Ď擾
							id = arg0.getDescription().substring(index);
							String str[] = new String[8];
							sw.getIC().mgp.getResponder(sw.getIC().name).responseOnChoiceEnter(id, id, str );
						}
						else{
							
						}
					}
				}
				
			});
			
		}
		return jEditorPane;
	}
	
	
	/**
	 * @return jContentPane
	 */
	public JPanel getJContentPane() {
		return jContentPane;
	}

	/**
	 * @return jFrame
	 */
	public JFrame getJFrame() {
		return jFrame;
	}

	/**
	 * @return jTextField
	 */
	public JTextArea getJTextArea() {
		return jTextArea;
	}
	
	/**
	 * @return jEditorPane
	 */
	public JEditorPane getJEditorPane(){
		return jEditorPane;
	}
	
	/**
	 * JEditorPane��V����HTML������ɂ���č쐬�������B
	 * @return�@���������������������ǂ���
	 */
	public boolean setNewJEditorPane(String text){
		
		System.out.println(text);
		
		try {
			jEditorPane = new JEditorPane(text);
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		
		//jScrollPane.setViewportView(getNewJEditorPane(text));
		
		return true;
		
	}
	
	public void delWindow(){
		jFrame.setVisible(false);
		jFrame = null;
	}

}
