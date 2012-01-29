package org.mineap.mpgp;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.mineap.mpgp.config.ConfigManager;
import org.mineap.mpgp.img.ImgControler;
import org.mineap.mpgp.sstp.SSTPserver;
import org.mineap.mpgp.window.MySplashWindow;
import org.mineap.mpgp.window.talk.TalkInputWindow;

/**
 * MultiplatformGhostPlayer Version 0.2.2
 * 
 * @author shiraminekeisuke
 *
 */
public class MGP {

    //�C���[�W�̓ǂݍ��݂���Shell�̐����܂ŁBSERIKO�݊��V�X�e���i�\��j
    private Vector<ImgControler> ic = new Vector<ImgControler>();
    //SHIORI�Ƃ̋��n�����s���B
    private Vector<Responder> res = new Vector<Responder>();
    //�eGhost�����X�N���v�g���������B
    private Vector<SakuraThread> st = new Vector<SakuraThread>();
    //���[�U�[����̕������͂��\�ɂ���B
    private Vector<TalkInputWindow> tiw = new Vector<TalkInputWindow>();
    //�z��Ɋi�[����Ă���e�v�f�̃f�[�^�̖��O���i�[�B���Ԃǂ���B
    private Vector<String> dataName = new Vector<String>();
    //Ghost+Shell�Z�b�g�Ɋ���U����X���b�h
    private Vector<Timer> t = new Vector<Timer>();
    //Ghost+Shell�Z�b�g���N���������Ԃ�ێ�����
    private Vector<Long> oldTime = new Vector<Long>();
    //Ghost+Shell�Z�b�g�����鎖���\���ǂ���
    private Vector<Integer> cantaklFlagArray = new Vector<Integer>();
    public boolean isSurfaceRestore;
    //SSTP�T�[�o�[
    private SSTPserver sstps;
    //SplashWindow
    private MySplashWindow msw;
    
    /**
     * �R���X�g���N�^�B�Ƃ肠����Shell+Ghost�Z�b�g���Ăяo��
     */
    public MGP() {

        msw = new MySplashWindow(this);

        callGandS("def");

        //SSTP�T�[�o�[�X�^�[�g
        sstps = new SSTPserver(this);

    }

    /**
     * �G���g���|�C���g
     * @param args
     */
    public static void main(String[] args) throws Exception {
    	
    	// e.g. >java MGP "./resource"
    	
    	if (args.length >= 1)
    	{
    		File dir = new File(args[0]);
    		if (dir.exists() && ConfigManager.getInstance().setResourceDir(dir))
    		{
    			System.out.println("���\�[�X�f�B���N�g��:" + dir.getPath());
    		}
    		else
    		{
    			throw new Exception("���\�[�X�f�B���N�g����������܂���B");
    		}
    	}
    	else
    	{
    		throw new IllegalArgumentException("���\�[�X�f�B���N�g�����w�肵�Ă��������B\n" +
    				"e.g. >java MGP \"./resource\"");
    	}
    	
    	System.out.println("MGP���N�����܂��B");
    	
        new MGP();
    }

    /**
     * �e�z��̂����Ă��镔���i�������f�[�^�̃C���f�b�N�X�͂��ׂē����ł��邱�Ɓj�ɐV���������
     * Ghost�{Shell�Z�b�g�����B
     * ��ԍŏ��ɐ��������i�f�t�H���g�́jGhost�{Shell�Z�b�g��kuronee�i���o�j�ł���B
     * @param str �Ăяo�������S�[�X�g�ƃV�F���̃f�[�^�A����������킷�S�̖��B�f�[�^���i�[���Ă���f�B���N�g�����Ɠ����B
     */
    private void callGandS(String str) {
        if (str.equals("def")) {
            //---------��������----------
            //ImgControler�𐶐�
            ic.add(new ImgControler("kuronee", this));
            //Responder�𐶐�
            res.add(new Responder("kuronee", this));
            //SakuraThread�𐶐�
            st.add(new SakuraThread(ic.get(0), this, "kuronee"));
            //TalkInputWindow�𐶐�
            TalkInputWindow tw = new TalkInputWindow("kuronee", this);
            tiw.add(tw);
            //�E�B���h�E�R���g���[����TalkInputWindow��ǉ�
            //ic.get(0).getUpdateControl().tba.add(tw);
            //���O��ۑ�
            dataName.add("kuronee");
            //--------�����܂ł�---------
            //Ghost�{Shell��1�Z�b�g�B

            /**�R�A�̃X�^�[�g*/
            oldTime.add(new Date().getTime());
            t.add(new Timer());
            t.get(0).schedule(new Core(str), 5, 1000);


            /**
             * �e�X�g�p�X�N���v�g���M
             */
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO �����������ꂽ catch �u���b�N
                e.printStackTrace();
            }

            String str2 = "\\h�͂��߂܂��āB\\n\\s[1]MGP�o�[�W����022�ւ悤�����B\\n\\e";
            st.get(0).ssPlayer(str2);

            str2 = "\\u\\s[10]���̃v���O�����̓e�X�g�ł�ŁB\\n\\e";
            st.get(0).ssPlayer(str2);

            str2 = "\\h\\s[0]�����Ȃ�ł��B\\n\\e";
            st.get(0).ssPlayer(str2);

            str2 = "\\u\\s[10]\\n���A�����Ȃ�H\\n\\e";
            st.get(0).ssPlayer(str2);

            str2 = "\\h\\s[2]\\n�E�E�E�E�E\\n\\e";
            st.get(0).ssPlayer(str2);

            str2 = "\\u\\s[11]\\n�E�E�E�E�E�I�I\\e";
            st.get(0).ssPlayer(str2);

            try {
                Thread.sleep(25000);
            } catch (InterruptedException e) {
                // TODO �����������ꂽ catch �u���b�N
                e.printStackTrace();
            }

            str2 = "\\h\\s[0]\\n������p�ł����H\\n\\e";
            st.get(0).ssPlayer(str2);

            str2 = "\\u\\s[11]\\n�E�E�E�E�E�I�I\\e";
            st.get(0).ssPlayer(str2);


        }
    }

    /**
     * Shell+Ghost�Z�b�g���폜����B
     * @param str�@Shell+Ghost�Z�b�g�̖��O 
     */
    public boolean delGhost(String str) {
        int dNum = 0;
        for (int i = 0; i < dataName.size(); i++) {
            if (str.equals(dataName.get(i))) {
                dNum = i;
                tiw.get(dNum).delWindow();
                tiw.removeElementAt(dNum);
                t.get(dNum).cancel();
                t.removeElementAt(dNum);
                ic.removeElementAt(dNum);
                res.removeElementAt(dNum);
                st.removeElementAt(dNum);
                dataName.removeElementAt(dNum);
            }

        }


        if (ic.isEmpty()) {
            System.exit(1);
        }

        return true;

    }

    /**
     * @author shiraminekeisuke
     * �R�A�N���X�B�eGhost-Shell�Z�b�g�̃��C�����[�v�B
     * 
     * �������̂��͂悭�킩��Ȃ��B
     * �X�N���v�g�̓ǂݍ��݁A��͂��s���A�����̃Z�b�g�ɖ��߂��΂��Ƃ��A
     * �l�b�g���[�N�ւ̐ڑ������݂�Ƃ�������̂ł͂Ȃ����낤���B
     *
     */
    public class Core extends TimerTask {

        //�S������Ghost-Shell�Z�b�g�̖��O
        private String name;
        private int count;

        public Core(String str) {
            // TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
            name = str;
        }

        @Override
        public void run() {
            // TODO �����������ꂽ���\�b�h�E�X�^�u

            Date newDate = new Date();
            long oldTime = getOldTime(name);

            long time = newDate.getTime() - oldTime;

            long time_h = (time / 3600000) % 60;

            getResponder(name).responseOnSecondChange(Long.toString(time_h), "0", "0", "1");

            count++;

            if (count >= 60) {
                getResponder(name).responseOnMinuteChange(Long.toString(time_h), "0", "0", "1");
                count = 0;
            }


        }
    }

    /**
     * Vector�Ɋi�[����Ă���SakuraThread�^�̃I�u�W�F�N�g��Ԃ��B
     * @param str�@�擾������SakuraThread�N���X�̖��O
     * @return�@SakuraThread�N���X�I�u�W�F�N�g
     */
    public SakuraThread getSakuraThread(String str) {
        int index = 0;
        for (int i = 0; i < dataName.size(); i++) {
            if (str.equals(dataName.get(i))) {
                index = i;
            }
        }
        return st.get(index);
    }

    /**
     * Vector�Ɋi�[����Ă���Date�^�̃I�u�W�F�N�g��Ԃ��B
     * @param str�@�擾������Date�N���X�̖��O
     * @return�@Date�N���X�I�u�W�F�N�g
     */
    public Long getOldTime(String str) {
        int index = 0;
        for (int i = 0; i < dataName.size(); i++) {
            if (str.equals(dataName.get(i))) {
                index = i;
            }
        }
        return oldTime.get(index);
    }

    /**
     * Vector�Ɋi�[����Ă���ImgControler�^�I�u�W�F�N�g��Ԃ��B
     * @param str�@�擾������ImgControler�N���X�̖��O
     * @return ImgControler�N���X�̃I�u�W�F�N�g
     */
    public ImgControler getImgControler(String str) {
        int index = 0;
        for (int i = 0; i < dataName.size(); i++) {
            if (str.equals(dataName.get(i))) {
                index = i;
            }
        }
        return ic.get(index);
    }

    /**
     * Vector�Ɋi����Ă���Responder�^�I�u�W�F�N�g��Ԃ��B
     * @param str �擾������Responder�N���X�̖��O
     * @return Responder�N���X�̃I�u�W�F�N�g
     */
    public Responder getResponder(String str) {
        int index = 0;
        for (int i = 0; i < dataName.size(); i++) {
            if (str.equals(dataName.get(i))) {
                index = i;
            }
        }
        return res.get(index);

    }

    /**
     * ResponderArray��Ԃ����\�b�h
     * @return
     */
    public Vector<Responder> getResponder() {
        // TODO �����������ꂽ���\�b�h�E�X�^�u
        return this.res;
    }

    /**
     * SakuraThreadArray��Ԃ����\�b�h
     * @return
     */
    public Vector<SakuraThread> getSakuraThread() {
        return this.st;
    }

    /**
     * �n���ꂽ���O��Ghost+Shell�z��ɑ��݂��邩�ǂ���
     * @return
     */
    public int isFindName(String name1, String name2) {

        for (int i = 0; i < res.size(); i++) {
            if (name1.equals(res.elementAt(i).sakuraName)) {
                if (name2.equals(res.elementAt(i).keroName)) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * �X�v���b�V���E�B���h�E�N���X��Ԃ�
     * @return
     */
    public MySplashWindow getMySplashWindow() {
        return msw;
    }
}
