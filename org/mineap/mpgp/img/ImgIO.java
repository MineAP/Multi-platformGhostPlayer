package org.mineap.mpgp.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

/**
 * AlphaImgIOは自身が保持するBufferedImageに指定されたイメージを保持します<br>
 * 保持するBufferedImageに対して透過を含む様々な処理をドット単位で行うことが出来ます。
 * また、処理後のファイルを書き出すことも可能です。
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
     * コンストラクタ。filenameで渡されたパスを使ってイメージを読み込む<br>
     * @param filename ファイルの名前（パス）
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public ImgIO(String filename) throws FileNotFoundException, IOException {
        // TODO 自動生成されたコンストラクター・スタブ
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
     * BufferedImageを返す
     * @return ImgIOクラスが保持するBufferedImage
     */
    public BufferedImage getBImage() {
        return bImg;
    }

    /**
     * イメージの大きさを返す
     * @return　int型配列　[横,縦]
     */
    public int[] getSize() {
        int size[] = {width, height};
        return size;
    }

    /**
     * 入力されたRGB値を持つピクセルを透過色に指定したBufferedImageを返す
     * @param rgb　透明にしたいRGB値
     * @return	透明処理が施されたBufferedImage
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
     * RGBをint型配列に入れて返す。8bitごとに、アルファ-グリーン-レッド-ブルー。
     * @param x　x座標
     * @param y　y座標
     * @return	aRGB値
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
     * 持っているイメージの指定された座標の値を入力されたaRGBで上書きする
     * @param x	x座標
     * @param y	y座標
     * @param aRgb　セットしたいaRGB値
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
     * イメージの書き出しを行う<br>
     * 書き出したイメージの名前は、以前のファイルの名前の先頭にNEWを付加したものである。
     * @return イメージの書き出しに成功したらture
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
