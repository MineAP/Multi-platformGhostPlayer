package org.mineap.mpgp.window;

import java.util.Vector;

public class UpdateControl{
	
	//�Ǘ�����ׂ��E�B���h�E�����N���X
	Vector<UpdateControlableWindow> tba = new Vector<UpdateControlableWindow>();
	boolean b = false;
	
	//�Ǘ�����TB�Ƀt�H�[�J�X�������Ă��邩
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
