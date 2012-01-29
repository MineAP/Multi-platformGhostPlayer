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
		// TODO 自動生成されたコンストラクター・スタブ
		this.mgp = mgp;
		this.socket = socket;
		this.start();
	}

	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			int b = sstpInterpreter(br);
			
			socket.close();
			
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
		}
	}

	private int sstpInterpreter(BufferedReader br2) {
		// TODO 自動生成されたメソッド・スタブ
		
		String str = "";
		
		
		while(true){
			try {
				str = br2.readLine();
				strArray.add(str);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
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
		// TODO 自動生成されたメソッド・スタブ
		
		String request = "";
		
		for(int i=0; i<strArray.size(); i++){
			if(null == strArray.elementAt(i)){
				break;
			}
			Matcher m = Pattern.compile("Event:\\s*(.+)").matcher(strArray.elementAt(i));
			if(m.find()){
				request = "GET SHIORI/3.0\r\n";
				
				//sender情報を追加
				for(int j=0; j<strArray.size(); j++){
					Matcher m2 = Pattern.compile("(Sender.*)").matcher(strArray.elementAt(j));
					if(m2.find()){
						request += m2.group(1) + "\r\n";
						break;
					}
				}
				
				//イベントIDを追加
				request += "ID: "+ m.group(1) + "\r\n";
				
				//Referenceを追加
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
		// TODO 自動生成されたメソッド・スタブ
		
		boolean findEntry = false;
		String defScript = "";
		Vector<Integer> completionIndex = new Vector<Integer>();
		Vector<String> entryArray = new Vector<String>();
		
		for(int j=0; j<strArray.size(); j++){
			Matcher m2 = Pattern.compile("IfGhost:\\s(.*),(.*)").matcher(strArray.elementAt(j));
			if(m2.find()){
				
				//IfGhostを見つけたとき。まずGhostの名前を探す
				String ghostName1 = m2.group(1);
				String ghostName2 = m2.group(2);
				String script = "";
				
				m2 = Pattern.compile("Script:\\s(.*)").matcher(strArray.elementAt(j+1));
				if(m2.find()){
					//Scriptを探す
					if(defScript.equals("")){
						//Scriptが空だったら上書き（デフォルトスクリプトとして使用）
						defScript = m2.group(1);
					}
					
					script = m2.group(1);
					
					//Entryがあるかどうか確認
					//選択肢を含む場合、ゴースト側にどうやってSSTPでの会話だと知らせるかが問題
					for(int n=j+2; n<strArray.size(); n++){
						m2 = Pattern.compile("Entry:\\s(.*)").matcher(strArray.elementAt(n));
						if(m2.find()){
							entryArray.add(m2.group(1));
							findEntry = true;
						}
					}
					
				}
				
				int index = 0;
				
				//該当するGhostが存在するかどうか
				if(-1 != (index = mgp.isFindName(ghostName1, ghostName2))){
					mgp.getSakuraThread().get(index).ssPlayer(script);
					completionIndex.add(index);
				}
				
			}
			
		}
		
		//デフォルトスクリプト実行部分
		if(!defScript.equals("")){
			for(int i=0; i<mgp.getSakuraThread().size(); i++){
				
				//IfGhostで既に喋ったキャラクタにはスクリプトを送らないようにする処理
				for(int k=0; k<completionIndex.size(); k++){
					if(!(i == completionIndex.get(k))){
						mgp.getSakuraThread().get(i).ssPlayer(defScript);
					}
				}
			}
		}
		
	}

	private void findSEND() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	
	private void findEXECUTE() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	
	private void findCOMMUNICATE11() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	private void findCOMMUNICATE12() {
		// TODO 自動生成されたメソッド・スタブ
		
	}


}
