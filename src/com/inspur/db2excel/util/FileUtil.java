package com.inspur.db2excel.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

public class FileUtil
{
	static Logger log = Logger.getLogger(FileUtil.class);

	public static boolean copy(String fileFromPath, String fileToPath)
	{
		try
		{
			FileInputStream in = new java.io.FileInputStream(fileFromPath);
			File fileTo = new File(fileToPath);
			if (!fileTo.getParentFile().exists())
			{
				fileTo.getParentFile().mkdirs();
			}
			FileOutputStream out = new FileOutputStream(fileTo);
			byte[] bt = new byte[1024];
			int count;
			while ((count = in.read(bt)) > 0)
			{
				out.write(bt, 0, count);
			}
			in.close();
			out.close();
			return true;
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
			return false;
		}
	}
}
