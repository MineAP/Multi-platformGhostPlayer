package org.mineap.mpgp.config;

import java.io.File;

/**
 * 各種設定情報を保持するクラス
 * 
 * @author shiraminekeisuke(MineAP)
 *
 */
public class ConfigManager
{

	private static final ConfigManager manager = new ConfigManager();
	
	/**
	 * 
	 */
	private File resourceDir = null;
	
	/**
	 * 唯一の {@link ConfigManager} のインスタンスを返します
	 * 
	 * @return
	 */
	public static ConfigManager getInstance()
	{
		return manager;
	}
	
	/**
	 * コンストラクタ
	 */
	private ConfigManager()
	{
	}
	
	/**
	 * @param file
	 */
	public boolean setResourceDir(File file)
	{
		if (file != null && file.isDirectory())
		{
			this.resourceDir = file;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * リソースファイルが存在するディレクトリを返します
	 * 
	 * @return
	 */
	public File getResourceDir()
	{
		return this.resourceDir;
	}
	
}
