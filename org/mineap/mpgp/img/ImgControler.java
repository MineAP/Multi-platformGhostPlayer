package org.mineap.mpgp.img;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.mineap.mpgp.MGP;
import org.mineap.mpgp.balloon.BalloonImage;
import org.mineap.mpgp.config.ConfigManager;
import org.mineap.mpgp.window.UpdateControl;
import org.mineap.mpgp.window.shell.ShellWindow;

/**
 * Shell�ɑΉ�����T�[�t�F�X�ƁAShellWindow�A�y��Balloon�C���[�W��ێ�����B
 * �T�[�t�F�X����key�Ƃ��Ċe�f�[�^���n�b�V���}�b�v�Ƃ��ĊǗ�����B
 * 
 * @author shiraminekeisuke
 */
public class ImgControler {

    //�J�����g�f�B���N�g���܂ł̃p�X�B
    private String path;
    //Shell�̖��O
    private String sName;
    //�e�T�[�t�F�X�ƃT�[�t�F�X����ێ�����n�b�V���e�[�u���Bkey���T�[�t�F�X���Avalue��Surface�N���X�̃C���X�^���X�B
    private HashMap<String, Surface> hm = new HashMap<String, Surface>();
    //��������������ShellWindow��ێ��B[0]��Sakura�A[1]��kero�B����ȍ~���ꉞ�ێ��\�B
    private Vector<ShellWindow> swa = new Vector<ShellWindow>();
    //��������MGP�N���X��ێ�
    public MGP mgp;
    //Shell�pdescript.txt����ǂݍ��񂾓��e�B
    public String charset = "Shift_JIS";	//�����R�[�h�B���͂��񂺂�Ώ����ĂȂ��B�ȗ��B
    public String name = "";				//�T�[�t�F�X�Z�b�g�̖��O�B���j�[�N�ł��邱�Ƃ��]�܂����B
    public String id = "";					//�T�[�t�F�X�Z�b�g��ID�Bname�Ƒ�̓����B���j�[�N�ł��邱�ƁB
    public String type = "";				//����descript.txt�����ɑ΂��Ă̐ݒ肩�B�����Ƃ���shell������ׂ��B
    public String craftman = "";			// ����Җ��iASCII�j�B
    public String craftmanw = "";			// ����Җ��i���C�h�O���t�j�B�ǂ��炩�Е���K���擾�B
    public String craftmanurl = "";		//����҂�URL�B�ȗ��B
    public String readme = "readme.txt";	//readme�t�@�C���̖��O�B�ȗ��B
    //OS�ŗL�̋�؂蕶��
    private String ps;
    //�g���q
    private String extension;
    //�����E�B���h�E�����̂��߂̔w�i�X�V���R���g���[������N���X
    private UpdateControl uc = new UpdateControl();
    //�쐬�҂̖��O�����邩�ǂ���
    private boolean craftmanFlag;
    //Ballon��ێ�����N���X
    private BalloonImage bi = new BalloonImage();
    //��b�𓯊������邽�߂̃L���[
    private ConcurrentLinkedQueue<Integer> talkSyncQueue = new ConcurrentLinkedQueue<Integer>();

    /**
     * �R���X�g���N�^�B�����œn���ꂽ������𖼑O�Ƃ��ĊǗ��B
     * ���̖��O���g���Đݒ�Ɖ摜�̓ǂݍ��݂��s���B
     * @param string
     * @param mgp 
     */
    public ImgControler(String string, MGP mgp) {

        this.mgp = mgp;
        //��΃A�h���X���擾
        File file = ConfigManager.getInstance().getResourceDir();
        String path2 = file.getAbsolutePath();
        //System.out.println(path2);

        //������Shell�̖��O�Ƃ��ĕۑ�
        sName = string;
        //��؂蕶�����擾
        ps = java.io.File.separator;
        //�擾������΃A�h���X����shell�̕ۑ��ꏊ�����
        path = path2 + ps + "shell" + ps + sName;

        //���肵��Shell�̕ۑ��ꏊ����C���[�W�A���̂ق���ǂݍ���
        if (!readSurface()) {
            path = path2 + ps + "ghost" + ps + sName + ps + "shell" + ps + "master" + ps;

            //�ǂݍ��݂Ɏ��s�B�ُ�I���B
            if (!readSurface()) {
                System.exit(0);
            }

        }
        //���肵��Shell�̕ۑ��ꏊ����f�B�X�N���v�g�t�@�C����ǂݍ���
        if (!readDescript()) {
            path = path2 + ps + "ghost" + ps + sName + ps + "shell" + ps + "master" + ps;

            //�ǂݍ��݂Ɏ��s�B�ُ�I���B
            if (!readDescript()) {
                System.exit(0);
            }

        }

        //Ballon��ǂݍ��ށB���݃f�t�H���g�̂݁B
        path = path2 + ps + "balloon" + ps + "default" + ps;
        if (!readBallon()) {
            path = path2 + ps + "balloon" + ps + "default" + ps;

            //�ǂݍ��݂Ɏ��s�B�ُ�I���B
            if (!readBallon()) {
                System.exit(0);
            }

        }

        //�f�[�^���ǂݍ��߂Ă��邩�ǂ����m�F����
		/*
        for(Iterator it = hm.keySet().iterator() ; it.hasNext();){
        System.out.println(it.next());
        }
         */

        //�����瑤�ɃT�[�t�F�X���Z�b�g
        ShellWindow sw = new ShellWindow("sakura", hm.get("surface0"), this, bi);
        swa.add(sw);
        //uc.tba.add(sw.getTB());

        //���둤�ɃT�[�t�F�X���Z�b�g
        sw = new ShellWindow("kero", hm.get("surface10"), this, bi);
        swa.add(sw);
        //uc.tba.add(sw.getTB());


    }

    /**
     * �o���[���Ɋւ��镔���̓ǂݍ���
     * @return
     */
    private boolean readBallon() {
        // TODO �����������ꂽ���\�b�h�E�X�^�u

        Matcher m;

        //Balloon�Ɋւ���discript�̓ǂݍ���
        try {
            BufferedReader br = new BufferedReader(new FileReader(path + "descript.txt"));

            //�t�@�C���̍Ō�ɓ��B����܂œǂݍ��݂𑱂���
            while (true) {

                String str = br.readLine();
                if (str == null) {
                    break;
                }
                //�O��̋󔒂��폜
                str = str.trim();

                m = Pattern.compile("^type,(.*)").matcher(str);
                if (m.find()) {
                    bi.b_Type = m.group(1);

                    if (!bi.b_Type.equals("balloon")) {
                        mgp.getMySplashWindow().drawString("-!-" + path + "descript.txt��type�G���g�����Ԉ���Ă��܂��Bballoon�ł���ׂ��ł��B");
                    }

                }
                m = Pattern.compile("^name,(.*)").matcher(str);
                if (m.find()) {
                    bi.b_Name = m.group(1);
                }
                m = Pattern.compile("^id,(.*)").matcher(str);
                if (m.find()) {
                    bi.b_ID = m.group(1);
                }
                m = Pattern.compile("^origin\\.x,(\\-*\\d+)").matcher(str);
                if (m.find()) {
                    bi.originX = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^origin\\.y,(\\-*\\d+)").matcher(str);
                if (m.find()) {
                    bi.originY = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^validrect\\.left,(\\-*\\d+)").matcher(str);
                if (m.find()) {
                    bi.validrectLeft = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^validrect\\.top,(\\-*\\d+)").matcher(str);
                if (m.find()) {
                    bi.validrectTop = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^validrect\\.right,(\\-*\\d+)").matcher(str);
                if (m.find()) {
                    bi.validrectRight = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^validrect\\.bottom,(\\-*\\d+)").matcher(str);
                if (m.find()) {
                    bi.validrectBottom = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^wordwrappoint\\.x,(\\-*\\d+)").matcher(str);
                if (m.find()) {
                    bi.wordwrappointX = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^wordwrappoint\\.y,(\\-*\\d+)").matcher(str);
                if (m.find()) {
                    bi.wordwrappointY = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^font\\.height,(\\-*\\d+)").matcher(str);
                if (m.find()) {
                    bi.fontHeight = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^font\\.name,(.*)").matcher(str);
                if (m.find()) {
                    bi.fontName = m.group(1);
                }
                m = Pattern.compile("^font\\.color\\.r,(\\-*\\d+)").matcher(str);
                if (m.find()) {
                    bi.fontCollorR = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^font\\.color\\.g,(\\-*\\d+)").matcher(str);
                if (m.find()) {
                    bi.fontCollorG = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^font\\.color\\.b,(\\-*\\d+)").matcher(str);
                if (m.find()) {
                    bi.fontCollorB = Integer.parseInt(m.group(1));
                }


                /**
                 * font.shadowcolor�ȉ������B
                 */
                m = Pattern.compile("^arrow0\\.x,(\\d+)").matcher(str);
                if (m.find()) {
                    bi.arrow0IndexX = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^arrow0\\.y,(\\d+)").matcher(str);
                if (m.find()) {
                    bi.arrow0IndexY = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^arrow1\\.x,(\\d+)").matcher(str);
                if (m.find()) {
                    bi.arrow1IndexX = Integer.parseInt(m.group(1));
                }
                m = Pattern.compile("^arrow1\\.y,(\\d+)").matcher(str);
                if (m.find()) {
                    bi.arrow1IndexY = Integer.parseInt(m.group(1));
                }





            }



        } catch (FileNotFoundException e1) {
            // TODO �����������ꂽ catch �u���b�N
            e1.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO �����������ꂽ catch �u���b�N
            e.printStackTrace();
            return false;
        }


        //balloon�C���[�W�̓ǂݍ���
        String extensionB = "";

        ImgIO iio = null;

        for (int i = 0; i < 16; i++) {
            String str = "balloonc" + i;
            if ((new File(path + str + ".gif")).exists()) {
                //�g���q��gif�̎��̏���
                extensionB = ".gif";
                try {
                    iio = new ImgIO(path + str + extension);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ((new File(path + str + ".png")).exists()) {
                //�g���q��png�̎��̏���
                extensionB = ".png";
                try {
                    iio = new ImgIO(path + str + extension);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ((new File(path + str + ".bmp")).exists()) {
                //�g���q��bmp�̎��̏���
                extensionB = ".bmp";
                try {
                    iio = new ImgIO(path + ps + str + extension);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //�C���[�W�̓��ߏ������s��
            BufferedImage img = null;
            try {
                img = iio.rgbToARgb(iio.getBImage().getRGB(0, 0));
                bi.balloncGraphics.add(img);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

        for (int i = 0; i < 16; i++) {
            String str = "balloonk" + i;

            if (new File(path + str + extension).exists()) {
                try {

                    iio = new ImgIO(path + str + extension);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                }

                //�C���[�W�̓��ߏ������s��
                BufferedImage img = null;
                try {
                    img = iio.rgbToARgb(iio.getBImage().getRGB(0, 0));
                    bi.ballonkGraphics.add(img);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

        }

        for (int i = 0; i < 16; i++) {
            String str = "balloons" + i;

            if (new File(path + str + extension).exists()) {
                try {

                    iio = new ImgIO(path + str + extension);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                }

                //�C���[�W�̓��ߏ������s��
                BufferedImage img = null;
                try {
                    img = iio.rgbToARgb(iio.getBImage().getRGB(0, 0));
                    bi.ballonsGraphics.add(img);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

        }

        for (int i = 0; i < 16; i++) {
            String str = "arrow" + i;

            if (new File(path + str + extension).exists()) {
                try {

                    iio = new ImgIO(path + str + extension);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                }

                //�C���[�W�̓��ߏ������s��
                BufferedImage img = null;
                try {
                    img = iio.rgbToARgb(iio.getBImage().getRGB(0, 0));
                    bi.arrows.add(img);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

        }


        return true;
    }

    /**
     * Shell�Ɋւ���discript.txt�̓ǂݍ��݂��s���Bdiscript.txt�͂��̃V�F����������̃v���t�@�C�����`����B
     * @return �ǂݍ��݂�����������true�A���s������false�B
     */
    private boolean readDescript() {

        Pattern p;
        Matcher m;

        try {
            //�Ǎ���̃t�@�C�������w��
            BufferedReader br = new BufferedReader(new FileReader(path + ps + "descript.txt"));

            //�t�@�C���̍Ō�ɓ��B����܂œǂݍ��݂𑱂���
            while (true) {

                String str = br.readLine();
                if (str == null) {
                    break;
                }
                //�O��̋󔒂��폜
                str = str.trim();

                //�����R�[�h�̓ǂݍ��݁B
                p = Pattern.compile("^charset,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    charset = m.group(1);
                }

                //�V�F���̖��O��ǂݍ���
                p = Pattern.compile("^name,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    name = m.group(1);
                }

                //�V�F����ID�̓ǂݍ���
                p = Pattern.compile("^id,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    id = m.group(1);
                }

                //�t�@�C���Z�b�g�̎�ʂ�ǂݍ���
                p = Pattern.compile("^type,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    type = m.group(1);
                    if (!type.endsWith("shell")) {
                        mgp.getMySplashWindow().drawString("-!-" + path + "descript.txt��type�G���g�����Ԉ���Ă��܂��Bshell�ł���ׂ��ł��B");
                    }
                }

                //����Җ���ǂݍ��݁B�ǂ��炩�Е���K���ǂݍ��ނ悤�ɂ������ˁB
                p = Pattern.compile("^craftman,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    craftman = m.group(1);
                    craftmanFlag = true;
                }
                p = Pattern.compile("^craftmanw,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    craftmanw = m.group(1);
                    craftmanFlag = true;
                }

                //����҂�web�T�C�g�̃A�h���X��ǂݍ��݁i�ȗ��j
                p = Pattern.compile("^craftmanurl,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    craftmanurl = m.group(1);
                }

                //���̃V�F���̐������t�@�C����ǂݍ��݁i�ȗ��j
                Pattern.compile("^readme,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    readme = m.group(1);
                }

                //�o���[���I�t�Z�b�g�Ɋւ���s��ǂݍ��񂾂Ƃ��̐ݒ�
                Matcher m2 = Pattern.compile("(.*)balloon\\.(.*)\\,(.*)").matcher(str);
                if (m2.find()) {
                    if (m2.group(2).equals("offsetx")) {

                        for (Iterator<String> it = hm.keySet().iterator(); it.hasNext();) {
                            hm.get(it.next()).b_offsetx = Integer.parseInt(m2.group(3));
                        }

                    }
                    if (m2.group(2).equals("offsety")) {

                        for (Iterator<String> it = hm.keySet().iterator(); it.hasNext();) {
                            hm.get(it.next()).b_offsety = Integer.parseInt(m2.group(3));
                        }

                    }
                    if (m2.group(2).equals("alignment")) {

                        for (Iterator<String> it = hm.keySet().iterator(); it.hasNext();) {
                            hm.get(it.next()).alignment = m2.group(3);
                        }

                    }

                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (!craftmanFlag) {
            mgp.getMySplashWindow().drawString("-!-" + path + ps + "descript.txt��craftman�G���g���̒l���ǂݍ��܂�܂���ł����B");
        }

        return true;

    }

    /**
     * �ݒ�t�@�C���A�C���[�W�̓ǂݍ��݂��s��
     * @return �ǂݍ��݂�����������true�A���s������false�B
     */
    private boolean readSurface() {

        Pattern p;
        Matcher m;
        BufferedReader br = null;

        try {
            //�Ǎ���̃t�@�C�������w��B
            try {
                br = new BufferedReader(new FileReader(path + ps + "surface.txt"));
            } catch (FileNotFoundException e) {
                br = new BufferedReader(new FileReader(path + ps + "surfaces.txt"));
            }
            int z = 1;

            //�t�@�C���̍Ō�ɓ��B����܂œǂݍ��݂𑱂���B
            while (true) {
                String str = br.readLine();
                if (str == null) {
                    //�t�@�C���̍Ō�܂œ��B������u���[�N�B�I���I���B
                    break;
                }
                //�O��̋󔒂��폜
                str = str.trim();

                //�s��surface�Ŏn�܂�Ƃ�
                if (Pattern.compile("^surface.*").matcher(str).matches()) {
                    int i = 0;
                    Surface sf = new Surface();
                    //�W���摜�̓T�[�t�F�X�̖��O�Ɠ����Ƃ����t�H�[�}�b�g�Ȃ̂ŁA
                    //���̃T�[�t�F�X������擾���ׂ��C���[�W�������A���[�h����B
                    ImgIO iio = null;
                    if ((new File(path + ps + str + ".gif")).exists()) {
                        //�g���q��gif�̎��̏���
                        extension = ".gif";
                        iio = new ImgIO(path + ps + str + extension);
                    } else if ((new File(path + ps + str + ".png")).exists()) {
                        //�g���q��png�̎��̏���
                        extension = ".png";
                        iio = new ImgIO(path + ps + str + extension);
                    } else if ((new File(path + ps + str + ".bmp")).exists()) {
                        //�g���q��bmp�̎��̏���
                        extension = ".bmp";
                        iio = new ImgIO(path + ps + str + extension);
                    }

                    //�C���[�W�̓��ߏ������s��
                    BufferedImage img = null;
                    try {
                        img = iio.rgbToARgb(iio.getBImage().getRGB(0, 0));
                    } catch (NullPointerException e) {
                        mgp.getMySplashWindow().drawString("-!-�摜�̓ǂݍ��݂�����Ɋ������Ȃ������\��������܂��B" +
                                str + extension + "�̓ǂݍ��݂ŃG���[���������܂����B");
                    }
                    sf.baseImg = img;

                    z++;

                    String st_name = str;

                    //�T�[�t�F�X�v�f�Ɋւ���ǂݍ��݃��[�v�B
                    while (true) {

                        str = br.readLine();
                        //�O��̋󔒂��폜
                        str = str.trim();
                        if (str.equals("}")) {
                            //�����ʂɑ���������u���[�N�B�����surface�̓ǂݍ��݂������������ƂɂȂ�B
                            //�e�����L������Surface�N���X���n�b�V���}�b�v�Ɋi�[
                            hm.put(st_name, sf);
                            break;
                        }



                        //��`�̈�ݒ�̂��߂̍s��ǂݍ��񂾂Ƃ��̔���
                        if (Pattern.compile(".*collision.*").matcher(str).matches() | Pattern.compile(".*point.*").matcher(str).matches()) {

                            /*
                             * �L�����N�^�̂����蔻��̈��Surface�N���X�ɕۑ�����B
                             */
                            str = str.trim();
                            p = Pattern.compile("collision(.\\d*),(.\\d*)," +
                                    "(.\\d*),(.\\d*),(.\\d*),(.*)");
                            m = p.matcher(str);

                            if (m.find()) {
                                Integer[] temp = {Integer.parseInt(m.group(2)),
                                    Integer.parseInt(m.group(3)),
                                    Integer.parseInt(m.group(4)),
                                    Integer.parseInt(m.group(5))
                                };
                                sf.collision.add(temp);
                                sf.collisionId.add(m.group(6));
                            }

                        }

                        //�C���^�[�o���̐ݒ�̂��߂̍s��ǂݍ��񂾂Ƃ��̔���
                        if (Pattern.compile(".*interval.*").matcher(str).matches()) {

                            /*
                             * �u���Ȃǂ̃A�j���[�V����������Ԋu���L�q���Ă��镔���B
                             * sometime�͂P�b�ԂɂP�^�Q�̊m���Ŏ��{����炵����
                             */

                            int index = str.indexOf("interval,");
                            index = index + 9;

                            str = str.substring(index);
                            sf.interval = str;
                        //System.out.println(str);

                        }

                        //surface��`�̃I�[�o�[���C�h�Ɋւ���s��ǂݍ��񂾂Ƃ��̔���
                        if (Pattern.compile(".*element.*").matcher(str).matches()) {

                            /*
                             * �������̃C���[�W���������ĐV���������G�𐶐�����B���܂��g���Ώ�������̌y���ɁB
                             * �܂����̂Ƃ���͂Ȃ�Ȃ��������ˁB
                             */

                            /* SERIKO/1.x�̂ݑΉ��B2.0�ł������d�l���ȁH */

                            //���̃T�[�t�F�X�Z�b�g���G�������g���ǂ����B
                            sf.isElement = true;

                            //�ǂݍ��񂾕������ޔ�
                            String data = str;

                            //�I�t�Z�b�g��y���W���擾
                            int index = str.lastIndexOf(",");
                            index = index + 1;
                            str = data.substring(index);
                            sf.overlay_y.add(Integer.parseInt(str));

                            //�I�t�Z�b�g��x���W���擾
                            int temp = data.lastIndexOf(",", index - 2);
                            str = data.substring(temp + 1, index - 1);
                            sf.overlay_x.add(Integer.parseInt(str));

                            //�I�t�Z�b�g�p�摜�̎擾
                            index = data.lastIndexOf(",", temp - 2);
                            str = data.substring(index + 1, temp);
                            BufferedImage img1 = ImageIO.read(new File(path + ps + str));
                            sf.anime.add(img1);

                            //�A�j���[�V�����i���o�[�ݒ�
                            sf.animationNo.add(i);

                            i++;
                        }

                        //����p�^�[���ɑ΂���摜�㏑����ݒ肷��s��ǂݍ��񂾂Ƃ��̔���
                        if (Pattern.compile(".*pattern.*").matcher(str).matches()) {

                            if (Pattern.compile("^animation.*").matcher(str).matches()) {
                                //animetion�ŃA�j���[�V�����p�^�[����ݒ肵�Ă���Ƃ��̓���

                                /**�@SERIKO/2.0�Ή������@*/
                                str = str.trim();

                                p = Pattern.compile("animation(.\\d*).pattern(\\d*)," +
                                        "overlayfast,(\\d*),(\\d*),(\\d*),(\\d*)");
                                m = p.matcher(str);
                                if (m.find()) {
                                    //�A�j���[�V�����ԍ��̃Z�b�g
                                    sf.animationNo.add(Integer.parseInt(m.group(1)));

                                    /**
                                     * ���̕����̃p�X�̎w������P����ׂ����B
                                     */
                                    //�C���[�W�̓ǂݍ���
                                    String s_name = m.group(3);
                                    s_name = "surface" + s_name;

                                    //�����œǂݍ��݃C���[�W�𓧉߂��鏈�����s��
                                    iio = new ImgIO(path + ps + s_name + extension);

                                    img = iio.rgbToARgb(iio.getBImage().getRGB(0, 0));

                                    sf.anime.add(img);

                                    //�p�^�[���̎��s���ԓǂݍ���
                                    sf.time.add(Integer.parseInt(m.group(4)));

                                    //�I�[�o�[���C���W�ǂݍ���
                                    sf.overlay_x.add(Integer.parseInt(m.group(5)));
                                    sf.overlay_y.add(Integer.parseInt(m.group(6)));

                                }

                            } else if (Pattern.compile("^\\d.*").matcher(str).matches()) {
                                //�s�̐擪�ŃA�j���[�V�����p�^�[����ݒ肵�Ă���Ƃ��̓���

                                /**�@SERIKO/1.x�Ή������@*/
                                str = str.trim();

                                p = Pattern.compile("(\\d*)pattern(.\\d*),(\\d*)," +
                                        "(\\d*),overlayfast,(\\d*),(\\d*)");
                                m = p.matcher(str);
                                if (m.find()) {
                                    //�s���Őݒ肳�ꂽ�A�j���[�V�����ԍ���Surface�N���X�ɑ}��
                                    sf.animationNo.add(Integer.parseInt(m.group(1)));

                                    //�摜�̓ǂݍ���
                                    String s_name = m.group(3);
                                    s_name = "surface" + s_name;

                                    //�����œǂݍ��݃C���[�W�𓧉߂��鏈�����s��
                                    iio = new ImgIO(path + ps + s_name + extension);
                                    img = iio.rgbToARgb(iio.getBImage().getRGB(0, 0));

                                    sf.anime.add(img);

                                    //�p�^�[���̎��s���Ԃ�ǂݍ���
                                    //���s���ԁi�Ԋu�j��Surface�N���X�Ɋi�[
                                    sf.time.add(Integer.parseInt(m.group(4)));

                                    //�I�[�o�[���C�Ɋւ�����W�̓ǂݍ��݂����݂�
                                    //�e�I�[�o���C���W��Surface�N���X�Ɋi�[
                                    sf.overlay_x.add(Integer.parseInt(m.group(5)));
                                    sf.overlay_y.add(Integer.parseInt(m.group(6)));

                                }
                            }
                            i++;
                        }
                    }
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

    /**
     * �w�肵��ShellWindow�̃T�[�t�F�X��ύX����B
     * @param shellNo�@�ύX������ShellWindow�̔ԍ�
     * @param surfaceNo �ύX������ShellWindow�̃T�[�t�F�X
     */
    public void setEmotion(int shellNo, String surfaceNo) {
        swa.get(shellNo).setEmotion(hm.get(surfaceNo), surfaceNo);
    }

    /**
     * �w�肵��ShellWinodw�ɓn���������������ׂ点��B
     * @param shellnum�@����ׂ点����ShellWindow�̔ԍ�
     * @param talk�@����ׂ点����������
     * @param isSSTP ������e��SSTP�ɂ����̂��ǂ���
     */
    public void talk(int shellnum, String talk, Boolean isSSTP) {
        /*
         * �������g���ĉ�b�𓯊�������悤�ɂ���
         * 
         * shellnum���L���[�Ɋi�[���A�e�o���[���͎w�肳�ꂽtalk�̕����񕪏o�͂�����
         * talkSyncQueue����poll���Ď�������菜���A���̃t�F�[�Y�ɉ�b����n���B
         */

        talkSyncQueue.add(shellnum);

        swa.get(shellnum).getBalloon().drawString(talk, 0, isSSTP);
    }

    /**
     * talkSyncQueue��Ԃ�
     * @return
     */
    public ConcurrentLinkedQueue<Integer> getTalkSyncQueue() {
        return talkSyncQueue;
    }

    /**
     * �w�肵��ShellWindow��TalkWindow���N���A����B
     */
    public void clearTalk(int shellname) {
        swa.get(shellname).clearBalloon();
    }

    /**
     * @return sName
     */
    public String getSName() {
        return sName;
    }

    /**
     * Vector�Ɋi�[����Ă���ShellWindow�N���X�̃C���X�^���X��Ԃ�
     * @param s�@�X�R�[�v�̑ΏہB0��Sakura�A1��kero�B
     * @return
     */
    public ShellWindow getSW(int s) {
        return swa.get(s);
    }

    /**
     * UpdateControl�N���X��Ԃ�
     * @return
     */
    public UpdateControl getUpdateControl() {
        return uc;
    }

    /**
     * IC�̐e�ł���MGP��Ԃ�
     * @return
     */
    public MGP getMGP() {
        return mgp;
    }

    /**
     * �ێ�����Shell�̍폜�����s����B
     */
    public void delShell() {
        for (int i = 0; i > swa.size(); i++) {
            swa.get(i).delWindow();
        }

    }

    /**
     * �p�����[�^�Ŏw�肳�ꂽShellWindow�̃T�[�t�F�XID��Ԃ�
     * @param i
     * @return
     */
    public int getEmotion(int i) {
        // TODO �����������ꂽ���\�b�h�E�X�^�u
        return swa.get(i).getEmotion();
    }

    /**
     * ImgControler���ێ�����Balloon����b�����ǂ����𔻕ʂ���
     * ��b���̏ꍇ��true��Ԃ�
     */
    public boolean isTalkComplete() {
        for (int i = 0; i < swa.size(); i++) {
            if (!swa.get(i).getBalloon().isTalkComplete) {
                return false;
            }
        }
        return true;

    }

    /**
     * ImgControler���ێ�����Balloon��setVisibleFalseCount���O�ɂȂ��Ă��邩�ǂ�����Ԃ��B
     * 0�ɂȂ��Ă���΂���Balloon�͔�\�����ł���B
     * 0�ɂȂ��Ă���Ƃ���true��Ԃ�
     */
    public boolean isVFCIsZero() {
        for (int i = 0; i < swa.size(); i++) {
            if (swa.get(i).getBalloon().setVisibleFalseCount != 0) {
                //System.out.println(i + " is not 0");
                return false;
            }
        }
        return true;
    }
}
