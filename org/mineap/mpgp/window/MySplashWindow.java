/**
 * 
 */
package org.mineap.mpgp.window;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mineap.mpgp.MGP;
import org.mineap.mpgp.config.ConfigManager;
import org.mineap.mpgp.img.ImgIO;

/**
 * @author shiraminekeisuke
 *
 */
public class MySplashWindow {

    private MGP mgp;
    private JFrame jFrame;
    private JPanel jContentPane;
    private ImgIO iio;
    private BufferedImage bImg;
    private JLabel jLabel;

    public MySplashWindow(MGP mgp) {

        this.mgp = mgp;

        //絶対アドレスを取得
        File file = ConfigManager.getInstance().getResourceDir();
        String path2 = file.getAbsolutePath();
        //区切り文字を取得
        String ps = java.io.File.separator;
        //取得した絶対アドレスからshellの保存場所を特定
        String path = path2 + ps + "SplashWindow" + ps + "sakura.png";

        try {
            iio = new ImgIO(path);
        } catch (Exception e) {
            path = path2 + ps + "splash" + ps + "sakura.png";
            try {
                iio = new ImgIO(path);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MySplashWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MySplashWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        bImg = iio.getBImage();

        //System.out.println("ウィンドウ作成");

        getJFrame();

        //System.out.println("描画開始");

        /*
        JLabel jLabel = new JLabel("テスト用文字列");
        jLabel.setBounds(20, bImg.getHeight()-50, 10, 400);
        jFrame.add(jLabel);
         */

        jFrame.setVisible(true);



        drawMainG();
        drawMainS();
    }

    private JFrame getJFrame() {
        // TODO 自動生成されたメソッド・スタブ
        if (jFrame == null) {
            jFrame = new JFrame();
            jFrame.setUndecorated(true);
            int h = jFrame.getToolkit().getScreenSize().height;
            int w = jFrame.getToolkit().getScreenSize().width;
            jFrame.setBounds(w / 2 - bImg.getWidth() / 2, h / 2 - bImg.getHeight() / 2, bImg.getWidth(), bImg.getHeight());
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        return jFrame;
    }

    private void drawMainS() {
        // TODO 自動生成されたメソッド・スタブ
        Graphics g = jFrame.getGraphics();
        g.drawString("Multi-platform Ghost Playerを起動中です...", 10, 30);
        g.dispose();

    }

    public void drawMainG() {
        Graphics g = jFrame.getGraphics();
        g.drawImage(bImg, 0, 0, getJFrame());
        g.dispose();

    }

    public void drawString(String str) {
        /*
        try{
        jLabel.setText(str);
        }catch(NullPointerException e){
        
        }
        jFrame.update(jFrame.getGraphics());
         */
    }

    public void setVisible(boolean b) {
        jFrame.setVisible(b);
    }
}
