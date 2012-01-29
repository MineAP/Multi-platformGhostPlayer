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

    //イメージの読み込みからShellの生成まで。SERIKO互換システム（予定）
    private Vector<ImgControler> ic = new Vector<ImgControler>();
    //SHIORIとの橋渡しを行う。
    private Vector<Responder> res = new Vector<Responder>();
    //各Ghostが持つスクリプト処理部分。
    private Vector<SakuraThread> st = new Vector<SakuraThread>();
    //ユーザーからの文字入力を可能にする。
    private Vector<TalkInputWindow> tiw = new Vector<TalkInputWindow>();
    //配列に格納されている各要素のデータの名前を格納。順番どおり。
    private Vector<String> dataName = new Vector<String>();
    //Ghost+Shellセットに割り振られるスレッド
    private Vector<Timer> t = new Vector<Timer>();
    //Ghost+Shellセットが起動した時間を保持する
    private Vector<Long> oldTime = new Vector<Long>();
    //Ghost+Shellセットが喋る事が可能かどうか
    private Vector<Integer> cantaklFlagArray = new Vector<Integer>();
    public boolean isSurfaceRestore;
    //SSTPサーバー
    private SSTPserver sstps;
    //SplashWindow
    private MySplashWindow msw;
    
    /**
     * コンストラクタ。とりあえずShell+Ghostセットを呼び出す
     */
    public MGP() {

        msw = new MySplashWindow(this);

        callGandS("def");

        //SSTPサーバースタート
        sstps = new SSTPserver(this);

    }

    /**
     * エントリポイント
     * @param args
     */
    public static void main(String[] args) throws Exception {
    	
    	// e.g. >java MGP "./resource"
    	
    	if (args.length >= 1)
    	{
    		File dir = new File(args[0]);
    		if (dir.exists() && ConfigManager.getInstance().setResourceDir(dir))
    		{
    			System.out.println("リソースディレクトリ:" + dir.getPath());
    		}
    		else
    		{
    			throw new Exception("リソースディレクトリが見つかりません。");
    		}
    	}
    	else
    	{
    		throw new IllegalArgumentException("リソースディレクトリを指定してください。\n" +
    				"e.g. >java MGP \"./resource\"");
    	}
    	
    	System.out.println("MGPを起動します。");
    	
        new MGP();
    }

    /**
     * 各配列のあいている部分（代入するデータのインデックスはすべて同じであること）に新しく作った
     * Ghost＋Shellセットを代入。
     * 一番最初に生成される（デフォルトの）Ghost＋Shellセットはkuronee（黒姉）である。
     * @param str 呼び出したいゴーストとシェルのデータ、両方をあらわす全体名。データが格納してあるディレクトリ名と同じ。
     */
    private void callGandS(String str) {
        if (str.equals("def")) {
            //---------ここから----------
            //ImgControlerを生成
            ic.add(new ImgControler("kuronee", this));
            //Responderを生成
            res.add(new Responder("kuronee", this));
            //SakuraThreadを生成
            st.add(new SakuraThread(ic.get(0), this, "kuronee"));
            //TalkInputWindowを生成
            TalkInputWindow tw = new TalkInputWindow("kuronee", this);
            tiw.add(tw);
            //ウィンドウコントロールにTalkInputWindowを追加
            //ic.get(0).getUpdateControl().tba.add(tw);
            //名前を保存
            dataName.add("kuronee");
            //--------ここまでが---------
            //Ghost＋Shellの1セット。

            /**コアのスタート*/
            oldTime.add(new Date().getTime());
            t.add(new Timer());
            t.get(0).schedule(new Core(str), 5, 1000);


            /**
             * テスト用スクリプト送信
             */
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }

            String str2 = "\\hはじめまして。\\n\\s[1]MGPバージョン022へようこそ。\\n\\e";
            st.get(0).ssPlayer(str2);

            str2 = "\\u\\s[10]このプログラムはテスト版やで。\\n\\e";
            st.get(0).ssPlayer(str2);

            str2 = "\\h\\s[0]そうなんです。\\n\\e";
            st.get(0).ssPlayer(str2);

            str2 = "\\u\\s[10]\\nあ、そうなん？\\n\\e";
            st.get(0).ssPlayer(str2);

            str2 = "\\h\\s[2]\\n・・・・・\\n\\e";
            st.get(0).ssPlayer(str2);

            str2 = "\\u\\s[11]\\n・・・・・！！\\e";
            st.get(0).ssPlayer(str2);

            try {
                Thread.sleep(25000);
            } catch (InterruptedException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }

            str2 = "\\h\\s[0]\\n何か御用ですか？\\n\\e";
            st.get(0).ssPlayer(str2);

            str2 = "\\u\\s[11]\\n・・・・・！！\\e";
            st.get(0).ssPlayer(str2);


        }
    }

    /**
     * Shell+Ghostセットを削除する。
     * @param str　Shell+Ghostセットの名前 
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
     * コアクラス。各Ghost-Shellセットのメインループ。
     * 
     * 何をやるのかはよくわからない。
     * スクリプトの読み込み、解析を行い、自分のセットに命令を飛ばすとか、
     * ネットワークへの接続を試みるとかをするのではないだろうか。
     *
     */
    public class Core extends TimerTask {

        //担当するGhost-Shellセットの名前
        private String name;
        private int count;

        public Core(String str) {
            // TODO 自動生成されたコンストラクター・スタブ
            name = str;
        }

        @Override
        public void run() {
            // TODO 自動生成されたメソッド・スタブ

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
     * Vectorに格納されているSakuraThread型のオブジェクトを返す。
     * @param str　取得したいSakuraThreadクラスの名前
     * @return　SakuraThreadクラスオブジェクト
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
     * Vectorに格納されているDate型のオブジェクトを返す。
     * @param str　取得したいDateクラスの名前
     * @return　Dateクラスオブジェクト
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
     * Vectorに格納されているImgControler型オブジェクトを返す。
     * @param str　取得したいImgControlerクラスの名前
     * @return ImgControlerクラスのオブジェクト
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
     * Vectorに格されているResponder型オブジェクトを返す。
     * @param str 取得したいResponderクラスの名前
     * @return Responderクラスのオブジェクト
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
     * ResponderArrayを返すメソッド
     * @return
     */
    public Vector<Responder> getResponder() {
        // TODO 自動生成されたメソッド・スタブ
        return this.res;
    }

    /**
     * SakuraThreadArrayを返すメソッド
     * @return
     */
    public Vector<SakuraThread> getSakuraThread() {
        return this.st;
    }

    /**
     * 渡された名前がGhost+Shell配列に存在するかどうか
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
     * スプラッシュウィンドウクラスを返す
     * @return
     */
    public MySplashWindow getMySplashWindow() {
        return msw;
    }
}
