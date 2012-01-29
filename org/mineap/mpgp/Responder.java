package org.mineap.mpgp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mineap.mpgp.config.ConfigManager;

/**
 * Responder。SHIORIとデータをやり取りする部分。
 * SHIORIへの呼びかけが必要な事象は全てここに集める。
 * 
 * @author shiraminekeisuke
 *
 */

public class Responder {
	
	//自分の名前を保持する
	private String sName;
	//自分の親であるMGPクラスのオブジェクトを保持する
	private MGP mgp;
	//OS固有の区切り文字
	private String ps;
	//カレントディレクトリまでの絶対パス
	private String path;
	//ゴーストデータ全体の名前
	String name;
	//ゴーストデータ全体の識別子
	String id;
	//ファイルセットの種類
	String type;
	//製作者名(ASCII)
	String craftman;
	//製作者名(ワイド文字)
	String craftmanw;
	//製作者URL
	String craftmanurl;
	//sakura側ゴーストの名前
	String sakuraName;
	//kero側ゴーストの名前
	String keroName;
	//特定のゴーストを指定しないSENDを受信するかどうか。０のときIfGhostで完全に指名されたときしかSEND
	//メッセージを受け取らない。
	int sstpAllowunspecifiedsend = 1;
	//アイコンファイル名。拡張子も指定する。省略可能。
	String icon;
	//マウスカーソルのファイル名。省略可能。
	String cursor;
	//SHIORIサブシステムとしてロードされるclass(jar?)ファイル名。省略可能。
	String shiori = "shiori.class";
	//MAKOTOサブシステムとしてロードされるclass(jar?)ファイル名。省略可能。省略した場合はロードされない。
	String makoto;
	

	
	/**
	 * コンストラクタ。自分の名前と親MGPクラスを初期化する
	 * @param name
	 * @param mgp
	 */
	public Responder(String name, MGP mgp) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.sName = name;
		this.mgp = mgp;
		
		//区切り文字を取得
		ps = java.io.File.separator;
		//絶対アドレスを取得
		File file = ConfigManager.getInstance().getResourceDir();
		String path2 = file.getAbsolutePath();
		
		path = path2 + ps + "ghost" + ps + sName;
		
		//特定したShellの保存場所からディスクリプトファイルを読み込む
		if(!readDescript()){
			path = path2 + ps +"ghost"+ ps + sName + ps + "ghost" + ps + "master" + ps;
			
			//読み込みに失敗。異常終了。
			if(!readDescript()){System.exit(0);}
			
		}
		
	}

	
	/**
	 * discript.txtの読み込みを行う。discript.txtはそのシェルが持つ特定のプロファイルを定義する。
	 * @return 読み込みが成功したらtrue、失敗したらfalse。
	 */
	private boolean readDescript() {
		Pattern p;
		Matcher m;
		
		try {
			//読込先のファイル名を指定
			BufferedReader br = new BufferedReader(new FileReader(path + ps +"descript.txt"));
			
			//ファイルの最後に到達するまで読み込みを続ける
			while(true){
				String str = br.readLine();
				if(str == null){
					break;
				}
				//前後の空白を削除
				str = str.trim();
				
				//ゴーストの名前を読み込み
				p = Pattern.compile("^name,(.*)");
				m = p.matcher(str);
				if(m.find()){
					name = m.group(1);
				}
				
				//ゴーストのIDの読み込み
				p = Pattern.compile("^id,(.*)");
				m = p.matcher(str);
				if(m.find()){
					id = m.group(1);
				}
				
				//ファイルセットの種別を読み込み
				p = Pattern.compile("^type,(.*)");
				m = p.matcher(str);
				if(m.find()){
					type = m.group(1);
				}
				
				//製作者名を読み込み。どちらか片方を必ず読み込むようにしたいね。
				p = Pattern.compile("^craftman,(.*)");
				m = p.matcher(str);
				if(m.find()){
					craftman = m.group(1);
				}
				p = Pattern.compile("^craftmanw,(.*)");
				m = p.matcher(str);
				if(m.find()){
					craftmanw = m.group(1);
				}
				
				//製作者のwebサイトのアドレスを読み込み（省略可）
				p = Pattern.compile("^craftmanurl,(.*)");
				m = p.matcher(str);
				if(m.find()){
					craftmanurl = m.group(1);
				}
				
				//sakura側ゴーストの名前を読み込む
				p = Pattern.compile("^sakura\\.name,(.*)");
				m = p.matcher(str);
				if(m.find()){
					sakuraName = m.group(1);
				}
				
				//kero側ゴーストの名前を読み込む
				p = Pattern.compile("^kero\\.name,(.*)");
				m = p.matcher(str);
				if(m.find()){
					keroName = m.group(1);
				}
				
				//sstpの読み込み
				p = Pattern.compile("^sstp\\.allowunspecifiedsend,(\\d)");
				m = p.matcher(str);
				if(m.find()){
					sstpAllowunspecifiedsend = Integer.parseInt(m.group(1));
				}
				
				//iconのファイル名を読み込み
				p = Pattern.compile("^icon,(.*)");
				m = p.matcher(str);
				if(m.find()){
					icon = m.group(1);
				}
				
				//カーソルのファイル名の読み込み
				p = Pattern.compile("^cursor,(.*)");
				m = p.matcher(str);
				if(m.find()){
					cursor = m.group(1);
				}
				
				//SHIORIサブシステムのファイル名を読み込み
				p = Pattern.compile("^shiori,(.*)");
				m = p.matcher(str);
				if(m.find()){
					shiori = m.group(1);
				}
				
				//MAKOTOサブシステムのファイル名を読み込み
				p = Pattern.compile("^makoto,(.*)");
				m = p.matcher(str);
				if(m.find()){
					makoto = m.group(1);
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
	
	//リクエストをSHIORIに投げる
	public String throwRequest(String request){
		
		/*
		 * とりあえずはsatoriに投げれば良いけど、
		 * 対応SHIORIが増えてきたときはここで対処するべし。
		 */
		
		System.out.println("request:\n"+request);
		
		
		return "SHIORI/3.0 200 OK";
		
	}
	
	
	//SSTPイベント//
	public void responseOnMusicPlay(){
		
	}
	
	//その他のイベント//
	public void responseOnNetworkHeavy(){
		
	}
	
	public void responseOnRecommendsiteChoice(){
		
	}
	
	public void responseOnSSTPBlacklisting(){
		
	}
	
	public void responseOnSSTPBreak(){
		
	}
	
	public void responseOnTranslate(){
		
	}
	
	//インストールイベント//
	public void responseOnInstallBegin(){
		
	}
	
	public void responseOnInstallComplete(){
		
	}
	
	public void responseOnInstallCompleteEx(){
		
	}
	
	public void responseOnInstallFailure(){
		
	}
	
	public void responseOnInstallRefuse(){
		
	}
	
	//キーボードイベント//
	public void responseOnKeyPress(){
		
	}
	
	//サーフェスイベント//
	/**
	 * サーフェスが変化したときに呼ばれるイベントです
	 * サイレントイベントです
	 * @surfaceID1	メイン側キャラの変更後のサーフェス番号
	 * @surfaceID2	サブ側キャラの変更後のサーフェス番号
	 */
	public void responseOnSurfaceChange(int surfaceID1, int surfaceID2){
		String str = "NOTIFY SHIORI/3.0\r\n" +
		"Sender: MGP\r\n" +
		"ID: OnSurfaceChange\r\n" +
		"Reference0: " + surfaceID1 + "\r\n" +
		"Reference1: " + surfaceID2 + "\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * サーフェスが変化した後、一定時間で呼び出されるイベントです
	 * このイベントに応答する事によってサーフェスを戻す事ができます。
	 * @param surfaceID1	メイン側の変更後のサーフェス番号
	 * @param surfaceID2	サブ側のサーフェス番号
	 */
	public void responseOnSurfaceRestore(int surfaceID1, int surfaceID2){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: MGP\r\n" +
		"ID: OnSurfaceRestore\r\n" +
		"Reference0: " + surfaceID1 + "\r\n" +
		"Reference1: " + surfaceID2 + "\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	//ドロップイベント
	public void responseOnFileDrop2(){
		
	}
	
	public void responseOnFileDropEx(){
		
	}
	
	public void responseOnNarCreated(){
		
	}
	
	public void responseOnNarCreationg(){
		
	}
	
	public void responseOnUpdatedataCreated(){
		
	}
	
	public void responseOnUpdatedataCreating(){
		
	}
	
	public void responseOnURLDropFailure(){
		
	}
	
	public void responseOnURLDropped(){
		
	}
	
	public void responseOnURLDropping(){
		
	}
	
	//ネットワーク更新イベント//
	public void responseOnUpdateOnDownloadBegin(){
		
	}
	
	public void responseOnUpdateOnMD5CompareBegin(){
		
	}
	
	public void responseOnUpDateOnMD5CompareComplete(){
		
	}
	
	public void responseOnUpdateOnMD5CompareFailure(){
		
	}
	
	public void responseOnUpdateBegin(){
		
	}
	
	public void responseOnUpdateComplete(){
		
	}
	
	public void responseOnUpdateFailure(){
		
	}
	
	public void responseOnUpadteReady(){
		
	}
	
	//ヘッドラインセンスイベント//
	public void responseOnHeadLinesenseOnFind(){
		
	}
	
	public void responseOnHeadLinesenseBegin(){
		
	}
	
	public void responseOnHeadLinesenseComplete(){
		
	}
	
	public void responseOnHeadLinesenseFailure(){
		
	}
	
	//マウスイベント//
	/**
	 * マウスがクリックされたときに発生するイベントです
	 * @param mouse_x	マウスカーソルのx座標（ローカル座標）
	 * @param mouse_y	マウスカーソルのy座標（ローカル座標)
	 * @param scopeNo	スコープ番号
	 * @param collisionID	あたり判定識別子
	 * @param buttonNo	クリックされたボタン(0またはなし：左　１：右　２：中)
	 */
	public void responseOnMouseClick(int mouse_x, int mouse_y, int scopeNo, String collisionID,
			int buttonNo){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: MGP\r\n" +
		"ID: OnMouseClick\r\n" +
		"Reference0: " + mouse_x + "\r\n" +
		"Reference1: " + mouse_y + "\r\n" +
		"Reference2: " + 0 + "\r\n" +
		"Reference3: " + scopeNo + "\r\n" +
		"Reference4: " + collisionID + "\r\n" +
		"Reference5: " + buttonNo + "\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * マウスがダブルクリックされたときに発生するイベントです
	 * @param mouse_x	マウスカーソルのx座標（ローカル座標）
	 * @param mouse_y	マウスカーソルのy座標（ローカル座標)
	 * @param scopeNo	スコープ番号
	 * @param collisionID	あたり判定識別子
	 * @param buttonNo	クリックされたボタン(0またはなし：左　１：右　２：中)
	 */
	public void responseOnMouseDoubleClick(int mouse_x, int mouse_y, int scopeNo, String collisionID,
			int buttonNo){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: MGP\r\n" +
		"ID: OnMouseDoubleClick\r\n" +
		"Reference0: " + mouse_x + "\r\n" +
		"Reference1: " + mouse_y + "\r\n" +
		"Reference2: " + 0 + "\r\n" +
		"Reference3: " + scopeNo + "\r\n" +
		"Reference4: " + collisionID + "\r\n" +
		"Reference5: " + buttonNo + "\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * サーフェス上でマウスが動いたときに発生するイベントです
	 * @param mouse_x	マウスカーソルのx座標（ローカル座標）
	 * @param mouse_y	マウスカーソルのy座標（ローカル座標)
	 * @param scopeNo	スコープ番号
	 * @param collisionID	あたり判定識別子
	 */
	public void responseOnMouseMove(int mouse_x, int mouse_y, int scopeNo, String collisionID){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: MGP\r\n" +
		"ID: OnMouseMove\r\n" +
		"Reference0: " + mouse_x + "\r\n" +
		"Reference1: " + mouse_y + "\r\n" +
		"Reference2: " + 0 + "\r\n" +
		"Reference3: " + scopeNo + "\r\n" +
		"Reference4: " + collisionID + "\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * マウスのホイールが回転したときに発生するイベントです
	 * @param mouse_x	マウスカーソルのx座標（ローカル座標）
	 * @param mouse_y	マウスカーソルのy座標（ローカル座標)
	 * @param wheelValue	マウスホイールの回転量及び回転方向
	 * @param scopeNo	スコープ番号
	 * @param collisionID	あたり判定識別子
	 */
	public void responseOnMouseWheel(int mouse_x, int mouse_y, int wheelValue, 
			int scopeNo, String collisionID){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: MGP\r\n" +
		"ID: OnMouseWheel\r\n" +
		"Reference0: " + mouse_x + "\r\n" +
		"Reference1: " + mouse_y + "\r\n" +
		"Reference2: " + wheelValue + "\r\n" +
		"Reference3: " + scopeNo + "\r\n" +
		"Reference4: " + collisionID + "\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	//メールチェックイベント//
	public void responseOnBIFF2Complete(){
		
	}
	
	public void responseOnBIFFBegin(){
		
	}
	
	public void responseOnBIFFComplete(){
		
	}
	
	public void responseBIFFFailure(){
		
	}
	
	//外部アプリ-きのこイベント//
	
	//外部アプリ-猫どりふイベント//
	
	//外部アプリイベント//
	
	//時間イベント//
	/**
	 * 現在時刻の分の単位が変更されたときに呼ばれるイベントです
	 * @time	連続起動時間（単位：時間）
	 * @flag1	見切れフラグ（１：見切れ中　０：通常）
	 * @flag2	重なりフラグ（１：重なり中　０：通常）
	 * @cantalkFlag	cantalkフラグ（１：実際に再生される　０：再生されない）
	 */
	public void responseOnMinuteChange(String time, String flag1, String flag2, String cantalkFlag){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: mgp\r\n" +
		"ID: OnMinuteChange\r\n" +
		"Reference0: " + time + "\r\n" +
		"Reference1: " + flag1 + "\r\n" +
		"Reference2: " + flag2 + "\r\n" +
		"Reference3: " + cantalkFlag + "\r\n" +
		"SecurityLevel: local\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * 現在時刻の秒の単位が変更されたときに呼ばれるイベントです
	 * @param time
	 * @param flag1
	 * @param flag2
	 * @param cantalkFlag
	 */
	public void responseOnSecondChange(String time, String flag1, String flag2, String cantalkFlag){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: mgp\r\n" +
		"ID: OnSecondChange\r\n" +
		"Reference0: " + time + "\r\n" +
		"Reference1: " + flag1 + "\r\n" +
		"Reference2: " + flag2 + "\r\n" +
		"Reference3: " + cantalkFlag + "\r\n" +
		"SecurityLevel: local\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	//時計合わせイベント//
	public void responseOnSNTPBegin(){
		
	}
	
	public void responseOnSNTPCompare(){
		
	}
	
	public void responseOnSNTPCorrect(){
		
	}
	
	public void responseOnSNTPFailure(){
		
	}
	
	//消滅イベント//
	public void responseOnOtherGhostVinished(){
		
	}
	
	public void responseOnVanishButtonHold(){
		
	}
	
	public void responseOnVanishCansel(){
		
	}
	
	public void responseOnVanished(){
		
	}
	
	public void responseOnVanishSelected(){
		
	}
	
	public void responseOnVanishSelectiong(){
		
	}
	
	//情報通知イベント//
	public void responsebasewareversion(){
		
	}
	
	public void responseOnNotifySelfInfo(){
		
	}
	
	//状態変更イベント//
	public void responseOnBoot(){
		
	}
	
	public void responseOnClose(){
		
	}
	
	public void responseOnDisplayChange(){
		
	}
	
	public void responseOnFirstBoot(){
		
	}
	
	public void responseOnShellScaling(){
		
	}
	
	public void responseOnWindowStateMinimize(){
		
	}
	
	public void responseOnWindowStateRestore(){
		
	}
	
	//切り替えイベント//
	public void responseOnGhostCallComplete(){
		
	}
	
	public void responseOnGhostCalled(){
		
	}
	
	public void responseOnGhostCalling(){
		
	}
	
	public void responseOnGhostChanged(){
		
	}
	
	public void responseOnGhostChanging(){
		
	}
	
	public void responseOnOtherGhostClosed(){
		
	}
	
	public void responseOnShellChanged(){
		
	}
	
	public void responseOnShellChanging(){
		
	}
	
	//選択肢イベント//
	/**
	 * アンカーをクリックしたときに発生するイベントです
	 * @anchor 選択されたアンカーが持つ識別子
	 */
	public void responseOnAnchorSelect(String anchor){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: embryo\r\n" +
		"ID: OnChoiceEnter\r\n" +
		"Reference0: " + anchor + "\r\n" +
		"SecurityLevel: local\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * アンカー以外の選択肢の上にカーソルが乗った瞬間及び外れた瞬間に発生するイベントです
	 * @param title	選択肢のタイトル
	 * @param label	ジャンプラベル
	 * @param info	拡張情報
	 */
	public void responseOnChoiceEnter(String title, String label, String info[]){
		for(int i=0;i<info.length;i++){
			if(info[i] == null){
				info[i] = "";
			}
		}
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: embryo\r\n" +
		"ID: OnChoiceEnter\r\n" +
		"Reference0: " + title + "\r\n" +
		"Reference1: " + label + "\r\n" +
		"Reference2: " + info[0] + "\r\n" +
		"Reference3: " + info[1] + "\r\n" +
		"Reference4: " + info[2] + "\r\n" +
		"Reference5: " + info[3] + "\r\n" +
		"Reference6: " + info[4] + "\r\n" +
		"Reference7: " + info[5] + "\r\n" +
		"Reference8: " + info[6] + "\r\n" +
		"Reference9: " + info[7] + "\r\n" +
		"SecurityLevel: local\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	/**
	 * 選択肢をクリックしたときに発生するイベントです
	 * @param selectId	選択された選択肢が持つ識別子
	 */
	public void responseOnChoiceSelect(String selectId){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: embryo\r\n" +
		"ID: OnChoiceSelect\r\n" +
		"Reference0: " + selectId + "\r\n" +
		"SecurityLevel: local\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	public void responseOnChoiceTimeout(){
		
	}
	
	//入力ボックスイベント//
	/**
	 * 外部から話しかけられた瞬間に発生するイベントです。ユーザから話しかけられたときはsenderName=userとなります。
	 * @senderName	送りもとの名前
	 * @script	スクリプト(送りもとがユーザのときは単純な文字列)
	 * @info	拡張情報
	 */
	public void responseOnCommunicate(String senderName, String script, String info){
		String str = "GET SHIORI/3.0\r\n" +
		"Sender: mgp\r\n" +
		"ID: OnCommunicate\r\n" +
		"Reference0: " + senderName + "\r\n" +
		"Reference1: " + script + "\r\n" +
		"Reference2: " + info + "\r\n" +
		"SecurityLevel: local\r\n" +
		"Charset: UTF-16\r\n" +
		"\r\n";
		//System.out.println(str);
	}
	
	public void responseOnTeachStart(){
		
	}
	
	public void responseOnUserInput(){
		
	}

	
}
