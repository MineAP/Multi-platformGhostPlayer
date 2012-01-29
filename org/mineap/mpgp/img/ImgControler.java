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
 * Shellに対応するサーフェスと、ShellWindow、及びBalloonイメージを保持する。
 * サーフェス名とkeyとして各データをハッシュマップとして管理する。
 * 
 * @author shiraminekeisuke
 */
public class ImgControler {

    //カレントディレクトリまでのパス。
    private String path;
    //Shellの名前
    private String sName;
    //各サーフェスとサーフェス名を保持するハッシュテーブル。keyがサーフェス名、valueがSurfaceクラスのインスタンス。
    private HashMap<String, Surface> hm = new HashMap<String, Surface>();
    //自分が生成したShellWindowを保持。[0]はSakura、[1]はkero。それ以降も一応保持可能。
    private Vector<ShellWindow> swa = new Vector<ShellWindow>();
    //生成元のMGPクラスを保持
    public MGP mgp;
    //Shell用descript.txtから読み込んだ内容。
    public String charset = "Shift_JIS";	//文字コード。今はぜんぜん対処してない。省略可。
    public String name = "";				//サーフェスセットの名前。ユニークであることが望ましい。
    public String id = "";					//サーフェスセットのID。nameと大体等価。ユニークであること。
    public String type = "";				//このdescript.txtが何に対しての設定か。きっとこのshellが入るべき。
    public String craftman = "";			// 製作者名（ASCII）。
    public String craftmanw = "";			// 製作者名（ワイドグリフ）。どちらか片方を必ず取得。
    public String craftmanurl = "";		//製作者のURL。省略可。
    public String readme = "readme.txt";	//readmeファイルの名前。省略可。
    //OS固有の区切り文字
    private String ps;
    //拡張子
    private String extension;
    //透明ウィンドウ実現のための背景更新をコントロールするクラス
    private UpdateControl uc = new UpdateControl();
    //作成者の名前があるかどうか
    private boolean craftmanFlag;
    //Ballonを保持するクラス
    private BalloonImage bi = new BalloonImage();
    //会話を同期させるためのキュー
    private ConcurrentLinkedQueue<Integer> talkSyncQueue = new ConcurrentLinkedQueue<Integer>();

    /**
     * コンストラクタ。引数で渡された文字列を名前として管理。
     * その名前を使って設定と画像の読み込みを行う。
     * @param string
     * @param mgp 
     */
    public ImgControler(String string, MGP mgp) {

        this.mgp = mgp;
        //絶対アドレスを取得
        File file = ConfigManager.getInstance().getResourceDir();
        String path2 = file.getAbsolutePath();
        //System.out.println(path2);

        //引数をShellの名前として保存
        sName = string;
        //区切り文字を取得
        ps = java.io.File.separator;
        //取得した絶対アドレスからshellの保存場所を特定
        path = path2 + ps + "shell" + ps + sName;

        //特定したShellの保存場所からイメージ、そのほかを読み込む
        if (!readSurface()) {
            path = path2 + ps + "ghost" + ps + sName + ps + "shell" + ps + "master" + ps;

            //読み込みに失敗。異常終了。
            if (!readSurface()) {
                System.exit(0);
            }

        }
        //特定したShellの保存場所からディスクリプトファイルを読み込む
        if (!readDescript()) {
            path = path2 + ps + "ghost" + ps + sName + ps + "shell" + ps + "master" + ps;

            //読み込みに失敗。異常終了。
            if (!readDescript()) {
                System.exit(0);
            }

        }

        //Ballonを読み込む。現在デフォルトのみ。
        path = path2 + ps + "balloon" + ps + "default" + ps;
        if (!readBallon()) {
            path = path2 + ps + "balloon" + ps + "default" + ps;

            //読み込みに失敗。異常終了。
            if (!readBallon()) {
                System.exit(0);
            }

        }

        //データが読み込めているかどうか確認する
		/*
        for(Iterator it = hm.keySet().iterator() ; it.hasNext();){
        System.out.println(it.next());
        }
         */

        //さくら側にサーフェスをセット
        ShellWindow sw = new ShellWindow("sakura", hm.get("surface0"), this, bi);
        swa.add(sw);
        //uc.tba.add(sw.getTB());

        //けろ側にサーフェスをセット
        sw = new ShellWindow("kero", hm.get("surface10"), this, bi);
        swa.add(sw);
        //uc.tba.add(sw.getTB());


    }

    /**
     * バルーンに関する部分の読み込み
     * @return
     */
    private boolean readBallon() {
        // TODO 自動生成されたメソッド・スタブ

        Matcher m;

        //Balloonに関するdiscriptの読み込み
        try {
            BufferedReader br = new BufferedReader(new FileReader(path + "descript.txt"));

            //ファイルの最後に到達するまで読み込みを続ける
            while (true) {

                String str = br.readLine();
                if (str == null) {
                    break;
                }
                //前後の空白を削除
                str = str.trim();

                m = Pattern.compile("^type,(.*)").matcher(str);
                if (m.find()) {
                    bi.b_Type = m.group(1);

                    if (!bi.b_Type.equals("balloon")) {
                        mgp.getMySplashWindow().drawString("-!-" + path + "descript.txtのtypeエントリが間違っています。balloonであるべきです。");
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
                 * font.shadowcolor以下無視。
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
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
            return false;
        }


        //balloonイメージの読み込み
        String extensionB = "";

        ImgIO iio = null;

        for (int i = 0; i < 16; i++) {
            String str = "balloonc" + i;
            if ((new File(path + str + ".gif")).exists()) {
                //拡張子がgifの時の処理
                extensionB = ".gif";
                try {
                    iio = new ImgIO(path + str + extension);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ((new File(path + str + ".png")).exists()) {
                //拡張子がpngの時の処理
                extensionB = ".png";
                try {
                    iio = new ImgIO(path + str + extension);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ((new File(path + str + ".bmp")).exists()) {
                //拡張子がbmpの時の処理
                extensionB = ".bmp";
                try {
                    iio = new ImgIO(path + ps + str + extension);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ImgControler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //イメージの透過処理を行う
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

                //イメージの透過処理を行う
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

                //イメージの透過処理を行う
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

                //イメージの透過処理を行う
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
     * Shellに関するdiscript.txtの読み込みを行う。discript.txtはそのシェルが持つ特定のプロファイルを定義する。
     * @return 読み込みが成功したらtrue、失敗したらfalse。
     */
    private boolean readDescript() {

        Pattern p;
        Matcher m;

        try {
            //読込先のファイル名を指定
            BufferedReader br = new BufferedReader(new FileReader(path + ps + "descript.txt"));

            //ファイルの最後に到達するまで読み込みを続ける
            while (true) {

                String str = br.readLine();
                if (str == null) {
                    break;
                }
                //前後の空白を削除
                str = str.trim();

                //文字コードの読み込み。
                p = Pattern.compile("^charset,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    charset = m.group(1);
                }

                //シェルの名前を読み込み
                p = Pattern.compile("^name,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    name = m.group(1);
                }

                //シェルのIDの読み込み
                p = Pattern.compile("^id,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    id = m.group(1);
                }

                //ファイルセットの種別を読み込み
                p = Pattern.compile("^type,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    type = m.group(1);
                    if (!type.endsWith("shell")) {
                        mgp.getMySplashWindow().drawString("-!-" + path + "descript.txtのtypeエントリが間違っています。shellであるべきです。");
                    }
                }

                //製作者名を読み込み。どちらか片方を必ず読み込むようにしたいね。
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

                //製作者のwebサイトのアドレスを読み込み（省略可）
                p = Pattern.compile("^craftmanurl,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    craftmanurl = m.group(1);
                }

                //このシェルの説明文ファイルを読み込み（省略可）
                Pattern.compile("^readme,(.*)");
                m = p.matcher(str);
                if (m.find()) {
                    readme = m.group(1);
                }

                //バルーンオフセットに関する行を読み込んだときの設定
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
            mgp.getMySplashWindow().drawString("-!-" + path + ps + "descript.txtのcraftmanエントリの値が読み込まれませんでした。");
        }

        return true;

    }

    /**
     * 設定ファイル、イメージの読み込みを行う
     * @return 読み込みが成功したらtrue、失敗したらfalse。
     */
    private boolean readSurface() {

        Pattern p;
        Matcher m;
        BufferedReader br = null;

        try {
            //読込先のファイル名を指定。
            try {
                br = new BufferedReader(new FileReader(path + ps + "surface.txt"));
            } catch (FileNotFoundException e) {
                br = new BufferedReader(new FileReader(path + ps + "surfaces.txt"));
            }
            int z = 1;

            //ファイルの最後に到達するまで読み込みを続ける。
            while (true) {
                String str = br.readLine();
                if (str == null) {
                    //ファイルの最後まで到達したらブレーク。終わり終わり。
                    break;
                }
                //前後の空白を削除
                str = str.trim();

                //行がsurfaceで始まるとき
                if (Pattern.compile("^surface.*").matcher(str).matches()) {
                    int i = 0;
                    Surface sf = new Surface();
                    //標準画像はサーフェスの名前と同じというフォーマットなので、
                    //そのサーフェス名から取得すべきイメージ名を作り、ロードする。
                    ImgIO iio = null;
                    if ((new File(path + ps + str + ".gif")).exists()) {
                        //拡張子がgifの時の処理
                        extension = ".gif";
                        iio = new ImgIO(path + ps + str + extension);
                    } else if ((new File(path + ps + str + ".png")).exists()) {
                        //拡張子がpngの時の処理
                        extension = ".png";
                        iio = new ImgIO(path + ps + str + extension);
                    } else if ((new File(path + ps + str + ".bmp")).exists()) {
                        //拡張子がbmpの時の処理
                        extension = ".bmp";
                        iio = new ImgIO(path + ps + str + extension);
                    }

                    //イメージの透過処理を行う
                    BufferedImage img = null;
                    try {
                        img = iio.rgbToARgb(iio.getBImage().getRGB(0, 0));
                    } catch (NullPointerException e) {
                        mgp.getMySplashWindow().drawString("-!-画像の読み込みが正常に完了しなかった可能性があります。" +
                                str + extension + "の読み込みでエラーが発生しました。");
                    }
                    sf.baseImg = img;

                    z++;

                    String st_name = str;

                    //サーフェス要素に関する読み込みループ。
                    while (true) {

                        str = br.readLine();
                        //前後の空白を削除
                        str = str.trim();
                        if (str.equals("}")) {
                            //閉じ括弧に遭遇したらブレーク。特定のsurfaceの読み込みを完了したことになる。
                            //各情報を記憶したSurfaceクラスをハッシュマップに格納
                            hm.put(st_name, sf);
                            break;
                        }



                        //矩形領域設定のための行を読み込んだときの反応
                        if (Pattern.compile(".*collision.*").matcher(str).matches() | Pattern.compile(".*point.*").matcher(str).matches()) {

                            /*
                             * キャラクタのあたり判定領域をSurfaceクラスに保存する。
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

                        //インターバルの設定のための行を読み込んだときの反応
                        if (Pattern.compile(".*interval.*").matcher(str).matches()) {

                            /*
                             * 瞬きなどのアニメーションをする間隔を記述してある部分。
                             * sometimeは１秒間に１／２の確立で実施するらしいよ
                             */

                            int index = str.indexOf("interval,");
                            index = index + 9;

                            str = str.substring(index);
                            sf.interval = str;
                        //System.out.println(str);

                        }

                        //surface定義のオーバーライドに関する行を読み込んだときの反応
                        if (Pattern.compile(".*element.*").matcher(str).matches()) {

                            /*
                             * 複数枚のイメージを合成して新しい立ち絵を生成する。うまく使えば消費メモリの軽減に。
                             * まあ今のところはならなさそうだね。
                             */

                            /* SERIKO/1.xのみ対応。2.0でも同じ仕様かな？ */

                            //このサーフェスセットがエレメントかどうか。
                            sf.isElement = true;

                            //読み込んだ文字列を退避
                            String data = str;

                            //オフセットのy座標を取得
                            int index = str.lastIndexOf(",");
                            index = index + 1;
                            str = data.substring(index);
                            sf.overlay_y.add(Integer.parseInt(str));

                            //オフセットのx座標を取得
                            int temp = data.lastIndexOf(",", index - 2);
                            str = data.substring(temp + 1, index - 1);
                            sf.overlay_x.add(Integer.parseInt(str));

                            //オフセット用画像の取得
                            index = data.lastIndexOf(",", temp - 2);
                            str = data.substring(index + 1, temp);
                            BufferedImage img1 = ImageIO.read(new File(path + ps + str));
                            sf.anime.add(img1);

                            //アニメーションナンバー設定
                            sf.animationNo.add(i);

                            i++;
                        }

                        //特定パターンに対する画像上書きを設定する行を読み込んだときの反応
                        if (Pattern.compile(".*pattern.*").matcher(str).matches()) {

                            if (Pattern.compile("^animation.*").matcher(str).matches()) {
                                //animetionでアニメーションパターンを設定しているときの動作

                                /**　SERIKO/2.0対応部分　*/
                                str = str.trim();

                                p = Pattern.compile("animation(.\\d*).pattern(\\d*)," +
                                        "overlayfast,(\\d*),(\\d*),(\\d*),(\\d*)");
                                m = p.matcher(str);
                                if (m.find()) {
                                    //アニメーション番号のセット
                                    sf.animationNo.add(Integer.parseInt(m.group(1)));

                                    /**
                                     * この部分のパスの指定を改善するべきだ。
                                     */
                                    //イメージの読み込み
                                    String s_name = m.group(3);
                                    s_name = "surface" + s_name;

                                    //ここで読み込みイメージを透過する処理を行う
                                    iio = new ImgIO(path + ps + s_name + extension);

                                    img = iio.rgbToARgb(iio.getBImage().getRGB(0, 0));

                                    sf.anime.add(img);

                                    //パターンの実行時間読み込み
                                    sf.time.add(Integer.parseInt(m.group(4)));

                                    //オーバーレイ座標読み込み
                                    sf.overlay_x.add(Integer.parseInt(m.group(5)));
                                    sf.overlay_y.add(Integer.parseInt(m.group(6)));

                                }

                            } else if (Pattern.compile("^\\d.*").matcher(str).matches()) {
                                //行の先頭でアニメーションパターンを設定しているときの動作

                                /**　SERIKO/1.x対応部分　*/
                                str = str.trim();

                                p = Pattern.compile("(\\d*)pattern(.\\d*),(\\d*)," +
                                        "(\\d*),overlayfast,(\\d*),(\\d*)");
                                m = p.matcher(str);
                                if (m.find()) {
                                    //行頭で設定されたアニメーション番号をSurfaceクラスに挿入
                                    sf.animationNo.add(Integer.parseInt(m.group(1)));

                                    //画像の読み込み
                                    String s_name = m.group(3);
                                    s_name = "surface" + s_name;

                                    //ここで読み込みイメージを透過する処理を行う
                                    iio = new ImgIO(path + ps + s_name + extension);
                                    img = iio.rgbToARgb(iio.getBImage().getRGB(0, 0));

                                    sf.anime.add(img);

                                    //パターンの実行時間を読み込み
                                    //実行時間（間隔）をSurfaceクラスに格納
                                    sf.time.add(Integer.parseInt(m.group(4)));

                                    //オーバーレイに関する座標の読み込みを試みる
                                    //各オーバレイ座標をSurfaceクラスに格納
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
     * 指定したShellWindowのサーフェスを変更する。
     * @param shellNo　変更したいShellWindowの番号
     * @param surfaceNo 変更したいShellWindowのサーフェス
     */
    public void setEmotion(int shellNo, String surfaceNo) {
        swa.get(shellNo).setEmotion(hm.get(surfaceNo), surfaceNo);
    }

    /**
     * 指定したShellWinodwに渡した文字列をしゃべらせる。
     * @param shellnum　しゃべらせたいShellWindowの番号
     * @param talk　しゃべらせたい文字列
     * @param isSSTP 喋る内容がSSTPによるものかどうか
     */
    public void talk(int shellnum, String talk, Boolean isSSTP) {
        /*
         * ここを使って会話を同期させるようにする
         * 
         * shellnumをキューに格納し、各バルーンは指定されたtalkの文字列分出力したら
         * talkSyncQueueからpollして自分を取り除き、次のフェーズに会話権を渡す。
         */

        talkSyncQueue.add(shellnum);

        swa.get(shellnum).getBalloon().drawString(talk, 0, isSSTP);
    }

    /**
     * talkSyncQueueを返す
     * @return
     */
    public ConcurrentLinkedQueue<Integer> getTalkSyncQueue() {
        return talkSyncQueue;
    }

    /**
     * 指定したShellWindowのTalkWindowをクリアする。
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
     * Vectorに格納されているShellWindowクラスのインスタンスを返す
     * @param s　スコープの対象。0がSakura、1がkero。
     * @return
     */
    public ShellWindow getSW(int s) {
        return swa.get(s);
    }

    /**
     * UpdateControlクラスを返す
     * @return
     */
    public UpdateControl getUpdateControl() {
        return uc;
    }

    /**
     * ICの親であるMGPを返す
     * @return
     */
    public MGP getMGP() {
        return mgp;
    }

    /**
     * 保持するShellの削除を実行する。
     */
    public void delShell() {
        for (int i = 0; i > swa.size(); i++) {
            swa.get(i).delWindow();
        }

    }

    /**
     * パラメータで指定されたShellWindowのサーフェスIDを返す
     * @param i
     * @return
     */
    public int getEmotion(int i) {
        // TODO 自動生成されたメソッド・スタブ
        return swa.get(i).getEmotion();
    }

    /**
     * ImgControlerが保持するBalloonが会話中かどうかを判別する
     * 会話中の場合はtrueを返す
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
     * ImgControlerが保持するBalloonのsetVisibleFalseCountが０になっているかどうかを返す。
     * 0になっていればそのBalloonは非表示許可である。
     * 0になっているときはtrueを返す
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
