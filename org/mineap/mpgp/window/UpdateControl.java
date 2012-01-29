package org.mineap.mpgp.window;

import java.util.Vector;

public class UpdateControl{
	
	//管理するべきウィンドウを持つクラス
	Vector<UpdateControlableWindow> tba = new Vector<UpdateControlableWindow>();
	boolean b = false;
	
	//管理するTBにフォーカスがあっているか
	public boolean isAppOnFocus(){
		b = false;
		for(int i = 0; i<tba.size();i++){
			System.out.println(tba.get(i).getIsOnFocus());
			if(tba.get(i).getIsOnFocus()){
				b = true;
			}
		}
		System.out.println("return:"+b);
		System.out.println();
		
		return b;
	}


}
