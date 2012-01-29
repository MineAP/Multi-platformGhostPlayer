package org.mineap.mpgp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mineap.mpgp.img.ImgControler;

/**
 * @author shiraminekeisuke
 * SakuraScript����������N���X�B
 * �eGhost���ЂƂێ�����B
 */
public class SakuraThread {
	
	private ImgControler ic;
	private MGP mgp;
	private String name;
	
	
	/**
	 * �R���X�g���N�^�B�X�N���v�g����p�������ƂȂ�ImgControler�Ɛe�ƂȂ�MGP��n�����B
	 * @param controler
	 * @param mgp
	 */
	public SakuraThread(ImgControler controler, MGP mgp, String name) {
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
		this.ic = controler;
		this.mgp = mgp;
		this.name = name;
	}


	/**
	 * ���͂��ꂽ�X�N���v�g�𗝉����A���s����B
	 * @param str �X�N���v�g
	 * @return	
	 */
	public int ssPlayer(String str){
		
		str = str.trim();
		
		char en = '\u00A5';
		char bs = '\\';
		str = str.replace(en, bs);
		
		int endIndex = str.length();
		//�ǂݍ��ݒ��̃X�N���v�g���ǂ��܂ŉ��߂��Ă��邩
		int index = 0;
		//�X�R�[�v�B0��1���B����ȍ~�̓T�|�[�g�����B
		int scope = 0;
		//�N�C�b�N�Z�N�V���������ǂ���
		boolean q = false;
		//���̑I�������^�C���A�E�g���邩�ǂ���
		boolean isTimeout = true;
		
		System.out.println("*�X�N���v�g��̓��[�v�J�n");
		
		while(true){
			
			if(index >= endIndex){
				System.out.println("*�X�N���v�g��͒��f�F\\e�����o�ł��܂���ł����B");
				break;
			}
			
			str = str.substring(index);
			System.out.println("#"+str);
			//�h���h�����o���镔��
			Pattern p = Pattern.compile("^(\\\\).*");
			Matcher m1 = p.matcher(str);
			inner:if(m1.find()){
				
				/**---�����n---*/
				
				//\h�����o���镔��
				Matcher m2 = Pattern.compile("^\\\\(h).*").matcher(str);
				if(m2.find()){
					
					scope = 0;
					
					//index���X�V
					index = m2.start(1)+1;
					//inner���甲���Ď��̕�����𒲂ׂ�
					break inner;
				
					
				}
				//��u�����o���镔��
				m2 = Pattern.compile("^\\\\(u).*").matcher(str);
				if(m2.find()){
					
					scope = 1;
					
					index = m2.start(1)+1;

					break inner;
				
					
				}
				//\0�����o���镔��
				m2 = Pattern.compile("^\\\\(0).*").matcher(str);
				if(m2.find()){
					
					scope = 0;
					
					index = m2.start(1)+1;

					break inner;
				
					
				}
				//\1�����o���镔��
				m2 = Pattern.compile("^\\\\(1).*").matcher(str);
				if(m2.find()){
					
					scope = 1;
					
					index = m2.start(1)+1;

					break inner;
				
					
				}
				//\n�����o���镔��
				m2 = Pattern.compile("^\\\\(n).*").matcher(str);
				if(m2.find()){
					
					String talk = "\n";
					
					ic.talk(scope, talk, false);
					index = m2.start(1)+1;

					break inner;
				
					
				}
				//\c�����o���镔��
				m2 = Pattern.compile("^\\\\(c).*").matcher(str);
				if(m2.find()){
					
					ic.clearTalk(scope);
					
					index = m2.start(1)+1;

					break inner;
				
				}
				//\x�����o���镔��
				m2 = Pattern.compile("^\\\\(x).*").matcher(str);
				if(m2.find()){
					
					/**
					 * �N���b�N�҂��B�ǂ�����Ď�������񂾁B
					 */
					
					index = m2.start(1)+1;

					break inner;
				
					
				}
				//\t�����o���镔��
				m2 = Pattern.compile("^\\\\(t).*").matcher(str);
				if(m2.find()){
					
					/**
					 * �^�C���N���e�B�J���Z�b�V�����B�������B
					 */
					
					index = m2.start(1)+1;

					break inner;
				
					
				}
				//\s[id]�����o���镔�� []���Ȃ��Ă��F������
				m2 = Pattern.compile("^\\\\s\\[?(-?[0-9]+)(\\]?)").matcher(str);
				if(m2.find()){
					
					/**
					 * �T�[�t�F�X�̐؂�ւ������{
					 * id = -1�̎��̓T�[�t�F�X�E�B���h�E��\�����Ȃ�
					 */
					
					String s_id = m2.group(1);
					int index_d = s_id.length();
					if(-1==Integer.parseInt(s_id)){
						ic.getSW(scope).setVisible(false);
						index = m2.start(1)+index_d+1;
					}else{
						ic.getSW(scope).setVisible(true);
						ic.setEmotion(scope, "surface"+s_id);
						//[]������ꍇ�́A���̕���index�����Z����
						int addIndex = m2.group(2).length();
						index = m2.start(1)+index_d+addIndex;
					}
					
					ic.getMGP().getResponder(name).responseOnSurfaceChange(ic.getEmotion(0),
							ic.getEmotion(1));
					break inner;
					
				}
				//\b[id]�����o���镔��[]���Ȃ��Ă��F������
				m2 = Pattern.compile("^\\\\b\\[?(-?[0-9]+)(\\]?)").matcher(str);
				if(m2.find()){
					/**
					 * �o���[���̐؂�ւ������{
					 * id = -1�̎��̓o���[�����\��  ������
					 */
					
					String b_id = m2.group(1);
					
					int index_d = b_id.length();
					if(-1 == Integer.parseInt(b_id)){
						ic.getSW(scope).getBalloon().setVisible(false);
					}else{
						ic.getSW(scope).getBalloon().setVisible(true);
					}
					index = m2.group(2).length()*2 +2 + index_d;
					break inner;
					
				}
				//\w?�����o���镔��
				m2 = Pattern.compile("^\\\\w([0-9]+)").matcher(str);
				if(m2.find()){
					/**
					 * 50*?�~���b�ҋ@�@������
					 * �ǂ̃X���b�h���X���[�v����Ηǂ��̂��B
					 * ���̃X�N���v�g��͕������H
					 */
					
					int i = Integer.parseInt(m2.group(1));
					if(i>0){
						
					}
					index = 2 + m2.group(1).length();
					break inner;
					
				}
				//\_[?]�����o���镔��
				m2 = Pattern.compile("^\\\\_\\[([0-9]+)\\]").matcher(str);
				if(m2.find()){
					/**
					 * ?�~���b�ҋ@�@������
					 */
					
					int i = Integer.parseInt(m2.group(1));
					if(i>0){
						
					}
					index = m2.group(1).length();
					break inner;
				}
				//\_q�����o���镔��
				m2 = Pattern.compile("^\\\\_q").matcher(str);
				if(m2.find()){
					/**
					 * �N�C�b�N�Z�N�V�����B�Z�N�V�������̓��b�Z�[�W���m�[�E�F�C�g�ŕ\�������B�@������
					 */
					if(q){
						q = false;
					}else{
						q = true;
					}
					index = 3;
					break inner;
				}
				//\_l[x,y]�����o���镔��
				m2 = Pattern.compile("^\\\\_l\\[([0-9]+),([0-9]+)\\]").matcher(str);
				if(m2.find()){
					/**
					 * ���X�R�[�v�̃J�[�\���ʒu�̐�Ύw��B�J�[�\����(x,y)�Ɉړ�����@������
					 */
					int x = Integer.parseInt(m2.group(1));
					int y = Integer.parseInt(m2.group(2));
					
					index = m2.group(1).length()+m2.group(2).length()+6;
					
					break inner;
					
				}
				//\4�����o���镔��
				m2 = Pattern.compile("^\\\\(4)").matcher(str);
				if(m2.find()){
					/**
					 * \4�L�����N�^�E�B���h�E�𗣂�
					 */
					
					if(ic.getSW(0).getWP_x()>ic.getSW(1).getWP_x()){
						for(int i=0;i<150;i++){
							ic.getSW(0).moveWindow("RIGHT", 1);
							ic.getSW(1).moveWindow("LEFT", 1);
						}
					}else{
						for(int i=0;i<150;i++){
							ic.getSW(0).moveWindow("LEFT", 1);
							ic.getSW(1).moveWindow("RIGHT", 1);
						}
					}
					index = 2;
					break inner;
					
				}
				//\5�����o���镔��
				m2 = Pattern.compile("^\\\\(5)").matcher(str);
				if(m2.find()){
					/**
					 * \5�L�����N�^�E�B���h�E���߂Â���
					 */
					
					//�E�B���h�E�O���E�B���h�E�P���E���ɂ���Ƃ�
					if(ic.getSW(0).getWP_x()>ic.getSW(1).getWP_x()){
						
						for(int i=0;i<150;i++){
							if(Math.abs(ic.getSW(0).getWP_x()-ic.getSW(1).getWP_x())<20){
								break;
							}
							ic.getSW(0).moveWindow("LEFT", 1);
							ic.getSW(1).moveWindow("RIGHT", 1);
						}
						
					//�E�B���h�E�O���E�B���h�E�P��荶���ɂ���Ƃ�
					}else{
						
						for(int i=0;i<150;i++){
							if(Math.abs(ic.getSW(0).getWP_x()-ic.getSW(1).getWP_x())<20){
								break;
							}
							ic.getSW(0).moveWindow("RIGHT", 1);
							ic.getSW(1).moveWindow("LEFT", 1);
						}
						
					}
					index = 2;
					break inner;
					
				}
				//\e�����o���镔��
				m2 = Pattern.compile("^\\\\(e).*").matcher(str);
				if(m2.find()){
					
					/**
					 * ���񂢁[�B�X�N���v�g�ǂݍ��ݏI���B
					 */
					
					index = m2.start(1)+1;
					q = false;
					System.out.println("*�X�N���v�g��͏I��");
					return 0;
				
				}
				
				/**---�I�����n---*/
				
				//\q[title,id]�����o���镔��
				m2 = Pattern.compile("^\\\\q\\[([^]]+),([^]]+)\\]").matcher(str);
				if(m2.find()){
					/**
					 * title�Ŏ������^�C�g�����������I������\���B
					 * �I����OnChoiceSelect�C�x���g����������B
					 * id�Ŏw�肳�ꂽ�I�������p�����[�^�Ƃ��ēn�����B
					 */
					
					System.out.println(m2.group(1)+","+m2.group(2));
					
					String title = "<a href=\""+ m2.group(1)+","+m2.group(2) +"\">" + m2.group(1) +"</a>";
					
					ic.talk(scope, title, false);
					
					index = 3+m2.group(1).length()+m2.group(2).length()+2;
					break inner;
				}
				//\q[id][title]�����o���镔��
				m2 = Pattern.compile("^\\\\q\\[([^]]+)\\]\\[([^]]+)\\]").matcher(str);
				if(m2.find()){
					/**
					 * �I�����n�B
					 * ���d�l�̂��ߐ�������Ȃ��B
					 */
					
					String title = "<a href=\""+ m2.group(1)+","+m2.group(2) +"\">" + m2.group(2) +"</a>";
					
					ic.talk(scope, title, false);
					
					
					index = 6+m2.group(1).length()+m2.group(2).length();
					break inner;
				}
				//\*�����o
				m2 = Pattern.compile("^\\\\\\*").matcher(str);
				if(m2.find()){
					/**
					 * \*�̎��ɂ���I�����̓^�C���A�E�g���Ȃ� ���d�l�B��������Ȃ��B
					 */
					
					isTimeout = false;
					index = 2;
					break inner;
				}
				//\_a[���ʎq]�����o
				m2 = Pattern.compile("^\\\\_a\\[(.*)\\]([^\\\\_a\\[\\]]*)\\\\_a\\[\\]").matcher(str);
				if(m2.find()){
					
					String talk = "<a href=\""+ m2.group(1) + "\">" + m2.group(2) +"</a>";
					
					ic.talk(scope, talk, false);
					
					index = 3 + 1 + m2.group(1).length() + 1 + m2.group(2).length() + 5;
					
					break inner;
				}
				
				/**---�����R�[�h�֌W---*/
				
				/**---���̑��̎��s�n---*/
				
				//\-�����o���镔��
				m2 = Pattern.compile("^\\\\(-).*").matcher(str);
				if(m2.find()){
					
					/**
					 * ������Ghost���I���B
					 */
					ic.delShell();
					mgp.delGhost(ic.getSName());
					
					index = m2.start(1)+1;
					
					return 0;
					//break inner;
				}
				
				/**---����---*/
				
				
				
			}else{
				
				//�ʏ�̕���������o���镔��
				p = Pattern.compile("^([^\\\\]*)\\\\.*");
				m1 = p.matcher(str);
				if(m1.find()){
					String talk = m1.group(1);
					ic.talk(scope, talk, false);
					index = m1.start(1)+talk.length();
				}
			}
			
		}
		return -1;
	}

	
}
