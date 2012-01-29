package org.mineap.mpgp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mineap.mpgp.config.ConfigManager;

/**
 * Responder�BSHIORI�ƃf�[�^������肷�镔���B
 * SHIORI�ւ̌Ăт������K�v�Ȏ��ۂ͑S�Ă����ɏW�߂�B
 * 
 * @author shiraminekeisuke
 *
 */

public class Responder {
	
	//�����̖��O��ێ�����
	private String sName;
	//�����̐e�ł���MGP�N���X�̃I�u�W�F�N�g��ێ�����
	private MGP mgp;
	//OS�ŗL�̋�؂蕶��
	private String ps;
	//�J�����g�f�B���N�g���܂ł̐�΃p�X
	private String path;
	//�S�[�X�g�f�[�^�S�̖̂��O
	String name;
	//�S�[�X�g�f�[�^�S�̂̎��ʎq
	String id;
	//�t�@�C���Z�b�g�̎��
	String type;
	//����Җ�(ASCII)
	String craftman;
	//����Җ�(���C�h����)
	String craftmanw;
	//�����URL
	String craftmanurl;
	//sakura���S�[�X�g�̖��O
	String sakuraName;
	//kero���S�[�X�g�̖��O
	String keroName;
	//����̃S�[�X�g���w�肵�Ȃ�SEND����M���邩�ǂ����B�O�̂Ƃ�IfGhost�Ŋ��S�Ɏw�����ꂽ�Ƃ�����SEND
	//���b�Z�[�W���󂯎��Ȃ��B
	int sstpAllowunspecifiedsend = 1;
	//�A�C�R���t�@�C�����B�g���q���w�肷��B�ȗ��\�B
	String icon;
	//�}�E�X�J�[�\���̃t�@�C�����B�ȗ��\�B
	String cursor;
	//SHIORI�T�u�V�X�e���Ƃ��ă��[�h�����class(jar?)�t�@�C�����B�ȗ��\�B
	String shiori = "shiori.class";
	//MAKOTO�T�u�V�X�e���Ƃ��ă��[�h�����class(jar?)�t�@�C�����B�ȗ��\�B�ȗ������ꍇ�̓��[�h����Ȃ��B
	String makoto;
	

	
	/**
	 * �R���X�g���N�^�B�����̖��O�ƐeMGP�N���X������������
	 * @param name
	 * @param mgp
	 */
	public Responder(String name, MGP mgp) {
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
		this.sName = name;
		this.mgp = mgp;
		
		//��؂蕶�����擾
		ps = java.io.File.separator;
		//��΃A�h���X���擾
		File file = ConfigManager.getInstance().getResourceDir();
		String path2 = file.getAbsolutePath();
		
		path = path2 + ps + "ghost" + ps + sName;
		
		//���肵��Shell�̕ۑ��ꏊ����f�B�X�N���v�g�t�@�C����ǂݍ���
		if(!readDescript()){
			path = path2 + ps +"ghost"+ ps + sName + ps + "ghost" + ps + "master" + ps;
			
			//�ǂݍ��݂Ɏ��s�B�ُ�I���B
			if(!readDescript()){System.exit(0);}
			
		}
		
	}

	
	/**
	 * discript.txt�̓ǂݍ��݂��s���Bdiscript.txt�͂��̃V�F����������̃v���t�@�C�����`����B
	 * @return �ǂݍ��݂�����������true�A���s������false�B
	 */
	private boolean readDescript() {
		Pattern p;
		Matcher m;
		
		try {
			//�Ǎ���̃t�@�C�������w��
			BufferedReader br = new BufferedReader(new FileReader(path + ps +"descript.txt"));
			
			//�t�@�C���̍Ō�ɓ��B����܂œǂݍ��݂𑱂���
			while(true){
				String str = br.readLine();
				if(str == null){
					break;
				}
				//�O��̋󔒂��폜
				str = str.trim();
				
				//�S�[�X�g�̖��O��ǂݍ���
				p = Pattern.compile("^name,(.*)");
				m = p.matcher(str);
				if(m.find()){
					name = m.group(1);
				}
				
				//�S�[�X�g��ID�̓ǂݍ���
				p = Pattern.compile("^id,(.*)");
				m = p.matcher(str);
				if(m.find()){
					id = m.group(1);
				}
				
				//�t�@�C���Z�b�g�̎�ʂ�ǂݍ���
				p = Pattern.compile("^type,(.*)");
				m = p.matcher(str);
				if(m.find()){
					type = m.group(1);
				}
				
				//����Җ���ǂݍ��݁B�ǂ��炩�Е���K���ǂݍ��ނ悤�ɂ������ˁB
				p = Pattern.compile("^craftman,(.*)");
				m = p.matcher(str);
				if(m.find()){
					craftman = m.group(1);
				}
				p = Pattern.compile("^craftmanw,(.*)");
				m = p.matcher(str);
				if(m.find()){
					craftmanw = m.group(1);
				}
				
				//����҂�web�T�C�g�̃A�h���X��ǂݍ��݁i�ȗ��j
				p = Pattern.compile("^craftmanurl,(.*)");
				m = p.matcher(str);
				if(m.find()){
					craftmanurl = m.group(1);
				}
				
				//sakura���S�[�X�g�̖��O��ǂݍ���
				p = Pattern.compile("^sakura\\.name,(.*)");
				m = p.matcher(str);
				if(m.find()){
					sakuraName = m.group(1);
				}
				
				//kero���S�[�X�g�̖��O��ǂݍ���
				p = Pattern.compile("^kero\\.name,(.*)");
				m = p.matcher(str);
				if(m.find()){
					keroName = m.group(1);
				}
				
				//sstp�̓ǂݍ���
				p = Pattern.compile("^sstp\\.allowunspecifiedsend,(\\d)");
				m = p.matcher(str);
				if(m.find()){
					sstpAllowunspecifiedsend = Integer.parseInt(m.group(1));
				}
				
				//icon�̃t�@�C������ǂݍ���
				p = Pattern.compile("^icon,(.*)");
				m = p.matcher(str);
				if(m.find()){
					icon = m.group(1);
				}
				
				//�J�[�\���̃t�@�C�����̓ǂݍ���
				p = Pattern.compile("^cursor,(.*)");
				m = p.matcher(str);
				if(m.find()){
					cursor = m.group(1);
				}
				
				//SHIORI�T�u�V�X�e���̃t�@�C������ǂݍ���
				p = Pattern.compile("^shiori,(.*)");
				m = p.matcher(str);
				if(m.find()){
					shiori = m.group(1);
				}
				
				//MAKOTO�T�u�V�X�e���̃t�@�C������ǂݍ���
				p = Pattern.compile("^makoto,(.*)");
				m = p.matcher(str);
				if(m.find()){
					makoto = m.group(1);
				}
				
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	//���N�G�X�g��SHIORI�ɓ�����
	public String throwRequest(String request){
		
		/*
		 * �Ƃ肠������satori�ɓ�����Ηǂ����ǁA
		 * �Ή�SHIORI�������Ă����Ƃ��͂����őΏ�����ׂ��B
		 */
		
		System.out.println("request:\n"+request);
		
		
		return "SHIORI/3.0 200 OK";
		
	}
	
	
	//SSTP�C�x���g//
	public void responseOnMusicPlay(){
		
	}
	
	//���̑��̃C�x���g//
	public void responseOnNetworkHeavy(){
		
	}
	
	public void responseOnRecommendsiteChoice(){
		
	}
	
	public void responseOnSSTPBlacklisting(){
		
	}
	
	public void responseOnSSTPBreak(){
		
	}
	
	public void responseOnTranslate(){
		
	}
	
	//�C���X�g�[���C�x���g//
	public void responseOnInstallBegin(){
		
	}
	
	public void responseOnInstallComplete(){
		
	}
	
	public void responseOnInstallCompleteEx(){
		
	}
	
	public void responseOnInstallFailure(){
		
	}
	
	public void responseOnInstallRefuse(){
		
	}
	
	//�L�[�{�[�h�C�x���g//
	public void responseOnKeyPress(){
		
	}
	
	//�T�[�t�F�X�C�x���g//
	/**
	 * �T�[�t�F�X���ω������Ƃ��ɌĂ΂��C�x���g�ł�
	 * �T�C�����g�C�x���g�ł�
	 * @surfaceID1	���C�����L�����̕ύX��̃T�[�t�F�X�ԍ�
	 * @surfaceID2	�T�u���L�����̕ύX��̃T�[�t�F�X�ԍ�
	 */
	public void responseOnSurfaceChange(int surfaceID1, int surfaceID2){
		String str = "NOTIFY SHIORI/3.0\r\n" +
		"Sender: MGP\r\n" +
		"ID: OnSurfaceChange\r\n" +
		"Reference0: " + surfaceID1 + "\r\n" +
		"Reference1: " + surfaceID2 + "\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * �T�[�t�F�X���ω�������A��莞�ԂŌĂяo�����C�x���g�ł�
	 * ���̃C�x���g�ɉ������鎖�ɂ���ăT�[�t�F�X��߂������ł��܂��B
	 * @param surfaceID1	���C�����̕ύX��̃T�[�t�F�X�ԍ�
	 * @param surfaceID2	�T�u���̃T�[�t�F�X�ԍ�
	 */
	public void responseOnSurfaceRestore(int surfaceID1, int surfaceID2){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: MGP\r\n" +
		"ID: OnSurfaceRestore\r\n" +
		"Reference0: " + surfaceID1 + "\r\n" +
		"Reference1: " + surfaceID2 + "\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	//�h���b�v�C�x���g
	public void responseOnFileDrop2(){
		
	}
	
	public void responseOnFileDropEx(){
		
	}
	
	public void responseOnNarCreated(){
		
	}
	
	public void responseOnNarCreationg(){
		
	}
	
	public void responseOnUpdatedataCreated(){
		
	}
	
	public void responseOnUpdatedataCreating(){
		
	}
	
	public void responseOnURLDropFailure(){
		
	}
	
	public void responseOnURLDropped(){
		
	}
	
	public void responseOnURLDropping(){
		
	}
	
	//�l�b�g���[�N�X�V�C�x���g//
	public void responseOnUpdateOnDownloadBegin(){
		
	}
	
	public void responseOnUpdateOnMD5CompareBegin(){
		
	}
	
	public void responseOnUpDateOnMD5CompareComplete(){
		
	}
	
	public void responseOnUpdateOnMD5CompareFailure(){
		
	}
	
	public void responseOnUpdateBegin(){
		
	}
	
	public void responseOnUpdateComplete(){
		
	}
	
	public void responseOnUpdateFailure(){
		
	}
	
	public void responseOnUpadteReady(){
		
	}
	
	//�w�b�h���C���Z���X�C�x���g//
	public void responseOnHeadLinesenseOnFind(){
		
	}
	
	public void responseOnHeadLinesenseBegin(){
		
	}
	
	public void responseOnHeadLinesenseComplete(){
		
	}
	
	public void responseOnHeadLinesenseFailure(){
		
	}
	
	//�}�E�X�C�x���g//
	/**
	 * �}�E�X���N���b�N���ꂽ�Ƃ��ɔ�������C�x���g�ł�
	 * @param mouse_x	�}�E�X�J�[�\����x���W�i���[�J�����W�j
	 * @param mouse_y	�}�E�X�J�[�\����y���W�i���[�J�����W)
	 * @param scopeNo	�X�R�[�v�ԍ�
	 * @param collisionID	�����蔻�莯�ʎq
	 * @param buttonNo	�N���b�N���ꂽ�{�^��(0�܂��͂Ȃ��F���@�P�F�E�@�Q�F��)
	 */
	public void responseOnMouseClick(int mouse_x, int mouse_y, int scopeNo, String collisionID,
			int buttonNo){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: MGP\r\n" +
		"ID: OnMouseClick\r\n" +
		"Reference0: " + mouse_x + "\r\n" +
		"Reference1: " + mouse_y + "\r\n" +
		"Reference2: " + 0 + "\r\n" +
		"Reference3: " + scopeNo + "\r\n" +
		"Reference4: " + collisionID + "\r\n" +
		"Reference5: " + buttonNo + "\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * �}�E�X���_�u���N���b�N���ꂽ�Ƃ��ɔ�������C�x���g�ł�
	 * @param mouse_x	�}�E�X�J�[�\����x���W�i���[�J�����W�j
	 * @param mouse_y	�}�E�X�J�[�\����y���W�i���[�J�����W)
	 * @param scopeNo	�X�R�[�v�ԍ�
	 * @param collisionID	�����蔻�莯�ʎq
	 * @param buttonNo	�N���b�N���ꂽ�{�^��(0�܂��͂Ȃ��F���@�P�F�E�@�Q�F��)
	 */
	public void responseOnMouseDoubleClick(int mouse_x, int mouse_y, int scopeNo, String collisionID,
			int buttonNo){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: MGP\r\n" +
		"ID: OnMouseDoubleClick\r\n" +
		"Reference0: " + mouse_x + "\r\n" +
		"Reference1: " + mouse_y + "\r\n" +
		"Reference2: " + 0 + "\r\n" +
		"Reference3: " + scopeNo + "\r\n" +
		"Reference4: " + collisionID + "\r\n" +
		"Reference5: " + buttonNo + "\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * �T�[�t�F�X��Ń}�E�X���������Ƃ��ɔ�������C�x���g�ł�
	 * @param mouse_x	�}�E�X�J�[�\����x���W�i���[�J�����W�j
	 * @param mouse_y	�}�E�X�J�[�\����y���W�i���[�J�����W)
	 * @param scopeNo	�X�R�[�v�ԍ�
	 * @param collisionID	�����蔻�莯�ʎq
	 */
	public void responseOnMouseMove(int mouse_x, int mouse_y, int scopeNo, String collisionID){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: MGP\r\n" +
		"ID: OnMouseMove\r\n" +
		"Reference0: " + mouse_x + "\r\n" +
		"Reference1: " + mouse_y + "\r\n" +
		"Reference2: " + 0 + "\r\n" +
		"Reference3: " + scopeNo + "\r\n" +
		"Reference4: " + collisionID + "\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * �}�E�X�̃z�C�[������]�����Ƃ��ɔ�������C�x���g�ł�
	 * @param mouse_x	�}�E�X�J�[�\����x���W�i���[�J�����W�j
	 * @param mouse_y	�}�E�X�J�[�\����y���W�i���[�J�����W)
	 * @param wheelValue	�}�E�X�z�C�[���̉�]�ʋy�щ�]����
	 * @param scopeNo	�X�R�[�v�ԍ�
	 * @param collisionID	�����蔻�莯�ʎq
	 */
	public void responseOnMouseWheel(int mouse_x, int mouse_y, int wheelValue, 
			int scopeNo, String collisionID){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: MGP\r\n" +
		"ID: OnMouseWheel\r\n" +
		"Reference0: " + mouse_x + "\r\n" +
		"Reference1: " + mouse_y + "\r\n" +
		"Reference2: " + wheelValue + "\r\n" +
		"Reference3: " + scopeNo + "\r\n" +
		"Reference4: " + collisionID + "\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	//���[���`�F�b�N�C�x���g//
	public void responseOnBIFF2Complete(){
		
	}
	
	public void responseOnBIFFBegin(){
		
	}
	
	public void responseOnBIFFComplete(){
		
	}
	
	public void responseBIFFFailure(){
		
	}
	
	//�O���A�v��-���̂��C�x���g//
	
	//�O���A�v��-�L�ǂ�ӃC�x���g//
	
	//�O���A�v���C�x���g//
	
	//���ԃC�x���g//
	/**
	 * ���ݎ����̕��̒P�ʂ��ύX���ꂽ�Ƃ��ɌĂ΂��C�x���g�ł�
	 * @time	�A���N�����ԁi�P�ʁF���ԁj
	 * @flag1	���؂�t���O�i�P�F���؂ꒆ�@�O�F�ʏ�j
	 * @flag2	�d�Ȃ�t���O�i�P�F�d�Ȃ蒆�@�O�F�ʏ�j
	 * @cantalkFlag	cantalk�t���O�i�P�F���ۂɍĐ������@�O�F�Đ�����Ȃ��j
	 */
	public void responseOnMinuteChange(String time, String flag1, String flag2, String cantalkFlag){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: mgp\r\n" +
		"ID: OnMinuteChange\r\n" +
		"Reference0: " + time + "\r\n" +
		"Reference1: " + flag1 + "\r\n" +
		"Reference2: " + flag2 + "\r\n" +
		"Reference3: " + cantalkFlag + "\r\n" +
		"SecurityLevel: local\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * ���ݎ����̕b�̒P�ʂ��ύX���ꂽ�Ƃ��ɌĂ΂��C�x���g�ł�
	 * @param time
	 * @param flag1
	 * @param flag2
	 * @param cantalkFlag
	 */
	public void responseOnSecondChange(String time, String flag1, String flag2, String cantalkFlag){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: mgp\r\n" +
		"ID: OnSecondChange\r\n" +
		"Reference0: " + time + "\r\n" +
		"Reference1: " + flag1 + "\r\n" +
		"Reference2: " + flag2 + "\r\n" +
		"Reference3: " + cantalkFlag + "\r\n" +
		"SecurityLevel: local\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	//���v���킹�C�x���g//
	public void responseOnSNTPBegin(){
		
	}
	
	public void responseOnSNTPCompare(){
		
	}
	
	public void responseOnSNTPCorrect(){
		
	}
	
	public void responseOnSNTPFailure(){
		
	}
	
	//���ŃC�x���g//
	public void responseOnOtherGhostVinished(){
		
	}
	
	public void responseOnVanishButtonHold(){
		
	}
	
	public void responseOnVanishCansel(){
		
	}
	
	public void responseOnVanished(){
		
	}
	
	public void responseOnVanishSelected(){
		
	}
	
	public void responseOnVanishSelectiong(){
		
	}
	
	//���ʒm�C�x���g//
	public void responsebasewareversion(){
		
	}
	
	public void responseOnNotifySelfInfo(){
		
	}
	
	//��ԕύX�C�x���g//
	public void responseOnBoot(){
		
	}
	
	public void responseOnClose(){
		
	}
	
	public void responseOnDisplayChange(){
		
	}
	
	public void responseOnFirstBoot(){
		
	}
	
	public void responseOnShellScaling(){
		
	}
	
	public void responseOnWindowStateMinimize(){
		
	}
	
	public void responseOnWindowStateRestore(){
		
	}
	
	//�؂�ւ��C�x���g//
	public void responseOnGhostCallComplete(){
		
	}
	
	public void responseOnGhostCalled(){
		
	}
	
	public void responseOnGhostCalling(){
		
	}
	
	public void responseOnGhostChanged(){
		
	}
	
	public void responseOnGhostChanging(){
		
	}
	
	public void responseOnOtherGhostClosed(){
		
	}
	
	public void responseOnShellChanged(){
		
	}
	
	public void responseOnShellChanging(){
		
	}
	
	//�I�����C�x���g//
	/**
	 * �A���J�[���N���b�N�����Ƃ��ɔ�������C�x���g�ł�
	 * @anchor �I�����ꂽ�A���J�[�������ʎq
	 */
	public void responseOnAnchorSelect(String anchor){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: embryo\r\n" +
		"ID: OnChoiceEnter\r\n" +
		"Reference0: " + anchor + "\r\n" +
		"SecurityLevel: local\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * �A���J�[�ȊO�̑I�����̏�ɃJ�[�\����������u�ԋy�ъO�ꂽ�u�Ԃɔ�������C�x���g�ł�
	 * @param title	�I�����̃^�C�g��
	 * @param label	�W�����v���x��
	 * @param info	�g�����
	 */
	public void responseOnChoiceEnter(String title, String label, String info[]){
		for(int i=0;i<info.length;i++){
			if(info[i] == null){
				info[i] = "";
			}
		}
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: embryo\r\n" +
		"ID: OnChoiceEnter\r\n" +
		"Reference0: " + title + "\r\n" +
		"Reference1: " + label + "\r\n" +
		"Reference2: " + info[0] + "\r\n" +
		"Reference3: " + info[1] + "\r\n" +
		"Reference4: " + info[2] + "\r\n" +
		"Reference5: " + info[3] + "\r\n" +
		"Reference6: " + info[4] + "\r\n" +
		"Reference7: " + info[5] + "\r\n" +
		"Reference8: " + info[6] + "\r\n" +
		"Reference9: " + info[7] + "\r\n" +
		"SecurityLevel: local\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * �I�������N���b�N�����Ƃ��ɔ�������C�x���g�ł�
	 * @param selectId	�I�����ꂽ�I�����������ʎq
	 */
	public void responseOnChoiceSelect(String selectId){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: embryo\r\n" +
		"ID: OnChoiceSelect\r\n" +
		"Reference0: " + selectId + "\r\n" +
		"SecurityLevel: local\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	public void responseOnChoiceTimeout(){
		
	}
	
	//���̓{�b�N�X�C�x���g//
	/**
	 * �O������b��������ꂽ�u�Ԃɔ�������C�x���g�ł��B���[�U����b��������ꂽ�Ƃ���senderName=user�ƂȂ�܂��B
	 * @senderName	������Ƃ̖��O
	 * @script	�X�N���v�g(������Ƃ����[�U�̂Ƃ��͒P���ȕ�����)
	 * @info	�g�����
	 */
	public void responseOnCommunicate(String senderName, String script, String info){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: mgp\r\n" +
		"ID: OnCommunicate\r\n" +
		"Reference0: " + senderName + "\r\n" +
		"Reference1: " + script + "\r\n" +
		"Reference2: " + info + "\r\n" +
		"SecurityLevel: local\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	public void responseOnTeachStart(){
		
	}
	
	public void responseOnUserInput(){
		
	}

	
}
