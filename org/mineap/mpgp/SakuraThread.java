package org.mineap.mpgp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mineap.mpgp.img.ImgControler;

/**
 * @author shiraminekeisuke
 * SakuraScriptを処理するクラス。
 * 各Ghostがひとつ保持する。
 */
public class SakuraThread {
	
	private ImgControler ic;
	private MGP mgp;
	private String name;
	
	
	/**
	 * コンストラクタ。スクリプトを作用させる先となるImgControlerと親となるMGPを渡される。
	 * @param controler
	 * @param mgp
	 */
	public SakuraThread(ImgControler controler, MGP mgp, String name) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.ic = controler;
		this.mgp = mgp;
		this.name = name;
	}


	/**
	 * 入力されたスクリプトを理解し、実行する。
	 * @param str スクリプト
	 * @return	
	 */
	public int ssPlayer(String str){
		
		str = str.trim();
		
		char en = '\u00A5';
		char bs = '\\';
		str = str.replace(en, bs);
		
		int endIndex = str.length();
		//読み込み中のスクリプトをどこまで解釈しているか
		int index = 0;
		//スコープ。0か1か。それ以降はサポートせず。
		int scope = 0;
		//クイックセクション中かどうか
		boolean q = false;
		//次の選択肢をタイムアウトするかどうか
		boolean isTimeout = true;
		
		System.out.println("*スクリプト解析ループ開始");
		
		while(true){
			
			if(index >= endIndex){
				System.out.println("*スクリプト解析中断：\\eが検出できませんでした。");
				break;
			}
			
			str = str.substring(index);
			System.out.println("#"+str);
			//”￥”を検出する部分
			Pattern p = Pattern.compile("^(\\\\).*");
			Matcher m1 = p.matcher(str);
			inner:if(m1.find()){
				
				/**---書式系---*/
				
				//\hを検出する部分
				Matcher m2 = Pattern.compile("^\\\\(h).*").matcher(str);
				if(m2.find()){
					
					scope = 0;
					
					//indexを更新
					index = m2.start(1)+1;
					//innerから抜けて次の文字列を調べる
					break inner;
				
					
				}
				//￥uを検出する部分
				m2 = Pattern.compile("^\\\\(u).*").matcher(str);
				if(m2.find()){
					
					scope = 1;
					
					index = m2.start(1)+1;

					break inner;
				
					
				}
				//\0を検出する部分
				m2 = Pattern.compile("^\\\\(0).*").matcher(str);
				if(m2.find()){
					
					scope = 0;
					
					index = m2.start(1)+1;

					break inner;
				
					
				}
				//\1を検出する部分
				m2 = Pattern.compile("^\\\\(1).*").matcher(str);
				if(m2.find()){
					
					scope = 1;
					
					index = m2.start(1)+1;

					break inner;
				
					
				}
				//\nを検出する部分
				m2 = Pattern.compile("^\\\\(n).*").matcher(str);
				if(m2.find()){
					
					String talk = "\n";
					
					ic.talk(scope, talk, false);
					index = m2.start(1)+1;

					break inner;
				
					
				}
				//\cを検出する部分
				m2 = Pattern.compile("^\\\\(c).*").matcher(str);
				if(m2.find()){
					
					ic.clearTalk(scope);
					
					index = m2.start(1)+1;

					break inner;
				
				}
				//\xを検出する部分
				m2 = Pattern.compile("^\\\\(x).*").matcher(str);
				if(m2.find()){
					
					/**
					 * クリック待ち。どうやって実装するんだ。
					 */
					
					index = m2.start(1)+1;

					break inner;
				
					
				}
				//\tを検出する部分
				m2 = Pattern.compile("^\\\\(t).*").matcher(str);
				if(m2.find()){
					
					/**
					 * タイムクリティカルセッション。未実装。
					 */
					
					index = m2.start(1)+1;

					break inner;
				
					
				}
				//\s[id]を検出する部分 []がなくても認識する
				m2 = Pattern.compile("^\\\\s\\[?(-?[0-9]+)(\\]?)").matcher(str);
				if(m2.find()){
					
					/**
					 * サーフェスの切り替えを実施
					 * id = -1の時はサーフェスウィンドウを表示しない
					 */
					
					String s_id = m2.group(1);
					int index_d = s_id.length();
					if(-1==Integer.parseInt(s_id)){
						ic.getSW(scope).setVisible(false);
						index = m2.start(1)+index_d+1;
					}else{
						ic.getSW(scope).setVisible(true);
						ic.setEmotion(scope, "surface"+s_id);
						//[]がある場合は、その分のindexを加算する
						int addIndex = m2.group(2).length();
						index = m2.start(1)+index_d+addIndex;
					}
					
					ic.getMGP().getResponder(name).responseOnSurfaceChange(ic.getEmotion(0),
							ic.getEmotion(1));
					break inner;
					
				}
				//\b[id]を検出する部分[]がなくても認識する
				m2 = Pattern.compile("^\\\\b\\[?(-?[0-9]+)(\\]?)").matcher(str);
				if(m2.find()){
					/**
					 * バルーンの切り替えを実施
					 * id = -1の時はバルーンを非表示  未実装
					 */
					
					String b_id = m2.group(1);
					
					int index_d = b_id.length();
					if(-1 == Integer.parseInt(b_id)){
						ic.getSW(scope).getBalloon().setVisible(false);
					}else{
						ic.getSW(scope).getBalloon().setVisible(true);
					}
					index = m2.group(2).length()*2 +2 + index_d;
					break inner;
					
				}
				//\w?を検出する部分
				m2 = Pattern.compile("^\\\\w([0-9]+)").matcher(str);
				if(m2.find()){
					/**
					 * 50*?ミリ秒待機　未実装
					 * どのスレッドがスリープすれば良いのか。
					 * このスクリプト解析部分か？
					 */
					
					int i = Integer.parseInt(m2.group(1));
					if(i>0){
						
					}
					index = 2 + m2.group(1).length();
					break inner;
					
				}
				//\_[?]を検出する部分
				m2 = Pattern.compile("^\\\\_\\[([0-9]+)\\]").matcher(str);
				if(m2.find()){
					/**
					 * ?ミリ秒待機　未実装
					 */
					
					int i = Integer.parseInt(m2.group(1));
					if(i>0){
						
					}
					index = m2.group(1).length();
					break inner;
				}
				//\_qを検出する部分
				m2 = Pattern.compile("^\\\\_q").matcher(str);
				if(m2.find()){
					/**
					 * クイックセクション。セクション中はメッセージがノーウェイトで表示される。　未実装
					 */
					if(q){
						q = false;
					}else{
						q = true;
					}
					index = 3;
					break inner;
				}
				//\_l[x,y]を検出する部分
				m2 = Pattern.compile("^\\\\_l\\[([0-9]+),([0-9]+)\\]").matcher(str);
				if(m2.find()){
					/**
					 * 現スコープのカーソル位置の絶対指定。カーソルが(x,y)に移動する　未実装
					 */
					int x = Integer.parseInt(m2.group(1));
					int y = Integer.parseInt(m2.group(2));
					
					index = m2.group(1).length()+m2.group(2).length()+6;
					
					break inner;
					
				}
				//\4を検出する部分
				m2 = Pattern.compile("^\\\\(4)").matcher(str);
				if(m2.find()){
					/**
					 * \4キャラクタウィンドウを離す
					 */
					
					if(ic.getSW(0).getWP_x()>ic.getSW(1).getWP_x()){
						for(int i=0;i<150;i++){
							ic.getSW(0).moveWindow("RIGHT", 1);
							ic.getSW(1).moveWindow("LEFT", 1);
						}
					}else{
						for(int i=0;i<150;i++){
							ic.getSW(0).moveWindow("LEFT", 1);
							ic.getSW(1).moveWindow("RIGHT", 1);
						}
					}
					index = 2;
					break inner;
					
				}
				//\5を検出する部分
				m2 = Pattern.compile("^\\\\(5)").matcher(str);
				if(m2.find()){
					/**
					 * \5キャラクタウィンドウを近づける
					 */
					
					//ウィンドウ０がウィンドウ１より右側にあるとき
					if(ic.getSW(0).getWP_x()>ic.getSW(1).getWP_x()){
						
						for(int i=0;i<150;i++){
							if(Math.abs(ic.getSW(0).getWP_x()-ic.getSW(1).getWP_x())<20){
								break;
							}
							ic.getSW(0).moveWindow("LEFT", 1);
							ic.getSW(1).moveWindow("RIGHT", 1);
						}
						
					//ウィンドウ０がウィンドウ１より左側にあるとき
					}else{
						
						for(int i=0;i<150;i++){
							if(Math.abs(ic.getSW(0).getWP_x()-ic.getSW(1).getWP_x())<20){
								break;
							}
							ic.getSW(0).moveWindow("RIGHT", 1);
							ic.getSW(1).moveWindow("LEFT", 1);
						}
						
					}
					index = 2;
					break inner;
					
				}
				//\eを検出する部分
				m2 = Pattern.compile("^\\\\(e).*").matcher(str);
				if(m2.find()){
					
					/**
					 * えんいー。スクリプト読み込み終了。
					 */
					
					index = m2.start(1)+1;
					q = false;
					System.out.println("*スクリプト解析終了");
					return 0;
				
				}
				
				/**---選択肢系---*/
				
				//\q[title,id]を検出する部分
				m2 = Pattern.compile("^\\\\q\\[([^]]+),([^]]+)\\]").matcher(str);
				if(m2.find()){
					/**
					 * titleで示されるタイトルを持った選択肢を表示。
					 * 選択後OnChoiceSelectイベントが発生する。
					 * idで指定された選択肢がパラメータとして渡される。
					 */
					
					System.out.println(m2.group(1)+","+m2.group(2));
					
					String title = "<a href=\""+ m2.group(1)+","+m2.group(2) +"\">" + m2.group(1) +"</a>";
					
					ic.talk(scope, title, false);
					
					index = 3+m2.group(1).length()+m2.group(2).length()+2;
					break inner;
				}
				//\q[id][title]を検出する部分
				m2 = Pattern.compile("^\\\\q\\[([^]]+)\\]\\[([^]]+)\\]").matcher(str);
				if(m2.find()){
					/**
					 * 選択肢系。
					 * 旧仕様のため推奨されない。
					 */
					
					String title = "<a href=\""+ m2.group(1)+","+m2.group(2) +"\">" + m2.group(2) +"</a>";
					
					ic.talk(scope, title, false);
					
					
					index = 6+m2.group(1).length()+m2.group(2).length();
					break inner;
				}
				//\*を検出
				m2 = Pattern.compile("^\\\\\\*").matcher(str);
				if(m2.find()){
					/**
					 * \*の次にある選択肢はタイムアウトしない 旧仕様。推奨されない。
					 */
					
					isTimeout = false;
					index = 2;
					break inner;
				}
				//\_a[識別子]を検出
				m2 = Pattern.compile("^\\\\_a\\[(.*)\\]([^\\\\_a\\[\\]]*)\\\\_a\\[\\]").matcher(str);
				if(m2.find()){
					
					String talk = "<a href=\""+ m2.group(1) + "\">" + m2.group(2) +"</a>";
					
					ic.talk(scope, talk, false);
					
					index = 3 + 1 + m2.group(1).length() + 1 + m2.group(2).length() + 5;
					
					break inner;
				}
				
				/**---文字コード関係---*/
				
				/**---その他の実行系---*/
				
				//\-を検出する部分
				m2 = Pattern.compile("^\\\\(-).*").matcher(str);
				if(m2.find()){
					
					/**
					 * 即座にGhostを終了。
					 */
					ic.delShell();
					mgp.delGhost(ic.getSName());
					
					index = m2.start(1)+1;
					
					return 0;
					//break inner;
				}
				
				/**---特殊---*/
				
				
				
			}else{
				
				//通常の文字列を検出する部分
				p = Pattern.compile("^([^\\\\]*)\\\\.*");
				m1 = p.matcher(str);
				if(m1.find()){
					String talk = m1.group(1);
					ic.talk(scope, talk, false);
					index = m1.start(1)+talk.length();
				}
			}
			
		}
		return -1;
	}

	
}
