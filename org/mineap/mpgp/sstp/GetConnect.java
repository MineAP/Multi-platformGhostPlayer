package org.mineap.mpgp.sstp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mineap.mpgp.MGP;

public class GetConnect extends Thread{
	
	private MGP mgp;
	private Socket socket;
	private BufferedReader br;
	private PrintWriter out;
	private Vector<String> strArray = new Vector<String>();
	
	
	public GetConnect(Socket socket, MGP mgp) {
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
		this.mgp = mgp;
		this.socket = socket;
		this.start();
	}

	public void run() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			int b = sstpInterpreter(br);
			
			socket.close();
			
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
				// TODO �����������ꂽ catch �u���b�N
				e1.printStackTrace();
			}
		}
	}

	private int sstpInterpreter(BufferedReader br2) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		String str = "";
		
		
		while(true){
			try {
				str = br2.readLine();
				strArray.add(str);
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
			
			if(str == null){
				break;
			}
		}
		
		Matcher m = Pattern.compile("NOTIFY SSTP/1.1").matcher(strArray.get(0));
		if(m.find()){
			findNOTIFY();
		}
		m = Pattern.compile("SEND SSTP/1.4").matcher(strArray.get(0));
		if(m.find()){
			findSEND();
		}
		m = Pattern.compile("EXECUTE SSTP/1.3").matcher(strArray.get(0));
		if(m.find()){
			findEXECUTE();
		}
		m = Pattern.compile("COMMUNICATE SSTP/1.1").matcher(strArray.get(0));
		if(m.find()){
			findCOMMUNICATE11();
		}
		m = Pattern.compile("COMMUNICATE SSTP/1.2").matcher(strArray.get(0));
		if(m.find()){
			findCOMMUNICATE12();
		}
		
		return 1;
	}

	private void findNOTIFY() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		String request = "";
		
		for(int i=0; i<strArray.size(); i++){
			if(null == strArray.elementAt(i)){
				break;
			}
			Matcher m = Pattern.compile("Event:\\s*(.+)").matcher(strArray.elementAt(i));
			if(m.find()){
				request = "GET SHIORI/3.0\r\n";
				
				//sender����ǉ�
				for(int j=0; j<strArray.size(); j++){
					Matcher m2 = Pattern.compile("(Sender.*)").matcher(strArray.elementAt(j));
					if(m2.find()){
						request += m2.group(1) + "\r\n";
						break;
					}
				}
				
				//�C�x���gID��ǉ�
				request += "ID: "+ m.group(1) + "\r\n";
				
				//Reference��ǉ�
				for(int j=0; j<strArray.size(); j++){
					if(null != strArray.elementAt(j)){
						Matcher m2 = Pattern.compile("(Reference\\d+:.*)").matcher(strArray.elementAt(j));
						if(m2.find()){
							request += m2.group(1) + "\r\n";
						}
					}
				}
				
				request += "Charset: UTF-16\r\n";
				
				
			}	
		}
		for(int index=0; index<mgp.getResponder().size(); index++){
			Matcher m = Pattern.compile("200 OK").matcher(mgp.getResponder().get(index).throwRequest(request));
			if(!m.find()){
				atNoResponse();
			}
		}
	}
	
	private void atNoResponse() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		boolean findEntry = false;
		String defScript = "";
		Vector<Integer> completionIndex = new Vector<Integer>();
		Vector<String> entryArray = new Vector<String>();
		
		for(int j=0; j<strArray.size(); j++){
			Matcher m2 = Pattern.compile("IfGhost:\\s(.*),(.*)").matcher(strArray.elementAt(j));
			if(m2.find()){
				
				//IfGhost���������Ƃ��B�܂�Ghost�̖��O��T��
				String ghostName1 = m2.group(1);
				String ghostName2 = m2.group(2);
				String script = "";
				
				m2 = Pattern.compile("Script:\\s(.*)").matcher(strArray.elementAt(j+1));
				if(m2.find()){
					//Script��T��
					if(defScript.equals("")){
						//Script���󂾂�����㏑���i�f�t�H���g�X�N���v�g�Ƃ��Ďg�p�j
						defScript = m2.group(1);
					}
					
					script = m2.group(1);
					
					//Entry�����邩�ǂ����m�F
					//�I�������܂ޏꍇ�A�S�[�X�g���ɂǂ������SSTP�ł̉�b���ƒm�点�邩�����
					for(int n=j+2; n<strArray.size(); n++){
						m2 = Pattern.compile("Entry:\\s(.*)").matcher(strArray.elementAt(n));
						if(m2.find()){
							entryArray.add(m2.group(1));
							findEntry = true;
						}
					}
					
				}
				
				int index = 0;
				
				//�Y������Ghost�����݂��邩�ǂ���
				if(-1 != (index = mgp.isFindName(ghostName1, ghostName2))){
					mgp.getSakuraThread().get(index).ssPlayer(script);
					completionIndex.add(index);
				}
				
			}
			
		}
		
		//�f�t�H���g�X�N���v�g���s����
		if(!defScript.equals("")){
			for(int i=0; i<mgp.getSakuraThread().size(); i++){
				
				//IfGhost�Ŋ��ɒ������L�����N�^�ɂ̓X�N���v�g�𑗂�Ȃ��悤�ɂ��鏈��
				for(int k=0; k<completionIndex.size(); k++){
					if(!(i == completionIndex.get(k))){
						mgp.getSakuraThread().get(i).ssPlayer(defScript);
					}
				}
			}
		}
		
	}

	private void findSEND() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}
	
	private void findEXECUTE() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}
	
	private void findCOMMUNICATE11() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}

	private void findCOMMUNICATE12() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}


}
