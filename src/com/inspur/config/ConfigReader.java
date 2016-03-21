/*
 * Copyright (c) 2003 - 2005 Langchao LG Information Systems Co.,Ltd.
 * All Rights Reserved.
 */
package com.inspur.config;

import org.jdom.JDOMException;
import com.inspur.config.XMLProperties;

/**
 * 获取任务调度系统的配置信息
 */
public class ConfigReader
{
	/**
	 * 加载属性文件
	 */
	private synchronized static void loadProperties()
	{
		if (properties == null)
		{
			// Create a manager with the full path to the xml config file.
			try
			{
				// get the config
				String filepath = Thread.currentThread().getContextClassLoader().getResource(CONFIG_PATH).getPath();
				if (filepath == null)
				{
					System.err.println("Classpath中找不到[" + CONFIG_PATH + "]!!");
				}
				properties = new XMLProperties("file:///" + filepath);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获得值 格式为x.y.z 不包括跟元素 <p/> 例: xml 文件根式如下: <root> <x> <y> <z>example</z>
	 * </y> </x> </root> 则调用 getPorperty(X.Y.Z) 返回:example
	 * </p>
	 * 
	 * @param key
	 * @return String the value of the key
	 */
	public static String getProperties(String key)
	{
		if (properties == null)
		{
			loadProperties();
		}
		return properties.getPorperty(key);
	}

	/**
	 * 不用重启 Web Server，只要在jsp中调用该方法即可重新读取配置文件。 added by zhaodp on 2005-10-06
	 */
	public static void clearProperties()
	{
		properties = null;
	}

	private static XMLProperties properties;
	private static final String CONFIG_PATH = "SystemConfig.xml";
}
