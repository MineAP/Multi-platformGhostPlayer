package org.mineap.mpgp.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

/**
 * AlphaImgIO�͎��g���ێ�����BufferedImage�Ɏw�肳�ꂽ�C���[�W��ێ����܂�<br>
 * �ێ�����BufferedImage�ɑ΂��ē��߂��܂ޗl�X�ȏ������h�b�g�P�ʂōs�����Ƃ��o���܂��B
 * �܂��A������̃t�@�C���������o�����Ƃ��\�ł��B
 * 
 * @author shiraminekeisuke
 *
 */
public class ImgIO {

    private String filename;
    private BufferedImage bImg;
    private int height;
    private int width;

    /**
     * �R���X�g���N�^�Bfilename�œn���ꂽ�p�X���g���ăC���[�W��ǂݍ���<br>
     * @param filename �t�@�C���̖��O�i�p�X�j
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public ImgIO(String filename) throws FileNotFoundException, IOException {
        // TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
        this.filename = filename;
        System.out.println(filename);

        bImg = ImageIO.read(new File(filename));
        
        if (bImg != null) {
            bImg.getType();
            height = bImg.getHeight();
            width = bImg.getWidth();
        } else {
            throw new IOException();
        }
        
    }

    /**
     * BufferedImage��Ԃ�
     * @return ImgIO�N���X���ێ�����BufferedImage
     */
    public BufferedImage getBImage() {
        return bImg;
    }

    /**
     * �C���[�W�̑傫����Ԃ�
     * @return�@int�^�z��@[��,�c]
     */
    public int[] getSize() {
        int size[] = {width, height};
        return size;
    }

    /**
     * ���͂��ꂽRGB�l�����s�N�Z���𓧉ߐF�Ɏw�肵��BufferedImage��Ԃ�
     * @param rgb�@�����ɂ�����RGB�l
     * @return	�����������{���ꂽBufferedImage
     */
    public BufferedImage rgbToARgb(int rgb) {

        int w = bImg.getWidth();
        int h = bImg.getHeight();

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (bImg.getRGB(x, y) == rgb) {
                    bi.setRGB(x, y, 0);
                } else {
                    bi.setRGB(x, y, bImg.getRGB(x, y));
                }
            }
        }

        return bi;
    }

    /**
     * RGB��int�^�z��ɓ���ĕԂ��B8bit���ƂɁA�A���t�@-�O���[��-���b�h-�u���[�B
     * @param x�@x���W
     * @param y�@y���W
     * @return	aRGB�l
     */
    public int[] getRgb(int x, int y) {
        int[] argb = new int[4];

        if (0 <= x && x < width && 0 <= y && y < height) {

            int p = bImg.getRGB(x, y);

            argb[0] = p >>> 24;

            argb[1] = (p & 0x00ff0000) >>> 16;

            argb[2] = (p & 0x0000ff00) >>> 8;

            argb[3] = p & 0x000000ff;

        }

        return argb;

    }

    /**
     * �����Ă���C���[�W�̎w�肳�ꂽ���W�̒l����͂��ꂽaRGB�ŏ㏑������
     * @param x	x���W
     * @param y	y���W
     * @param aRgb�@�Z�b�g������aRGB�l
     */
    public void setARgb(int x, int y, int aRgb[]) {

        if (0 <= x && x < width && 0 <= y && y < height) {

            int p = aRgb[0] << 24;

            p += aRgb[1] << 16;

            p += aRgb[2] << 8;

            p += aRgb[3];

            bImg.setRGB(x, y, p);

        }

    }

    /**
     * �C���[�W�̏����o�����s��<br>
     * �����o�����C���[�W�̖��O�́A�ȑO�̃t�@�C���̖��O�̐擪��NEW��t���������̂ł���B
     * @return �C���[�W�̏����o���ɐ���������ture
     */
    public boolean writeImage() {

        String type = "";

        boolean f = false;

        for (int i = 0; i < filename.length(); i++) {

            if (f) {
                type = type.concat("" + filename.charAt(i));
            }

            if (filename.charAt(i) == '.') {
                f = true;
                type = "";
            }
        }

        try {
            ImageIO.write(bImg, type, new File("NEW" + filename));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
