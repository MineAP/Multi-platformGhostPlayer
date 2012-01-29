package org.mineap.mpgp.config;

import java.io.File;

/**
 * �e��ݒ����ێ�����N���X
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
	 * �B��� {@link ConfigManager} �̃C���X�^���X��Ԃ��܂�
	 * 
	 * @return
	 */
	public static ConfigManager getInstance()
	{
		return manager;
	}
	
	/**
	 * �R���X�g���N�^
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
	 * ���\�[�X�t�@�C�������݂���f�B���N�g����Ԃ��܂�
	 * 
	 * @return
	 */
	public File getResourceDir()
	{
		return this.resourceDir;
	}
	
}
