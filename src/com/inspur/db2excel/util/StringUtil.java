package com.inspur.db2excel.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil
{

	/**
	 * 根据String List获得用逗号分割的字符串,每个数据项都用单引号引起来
	 * 
	 * @param stringList
	 * @return
	 */
	public static String getStringByListWithQuotation(List stringList)
	{
		return getStringByList(stringList, true);
	}

	/**
	 * 根据String List获得用逗号分割的字符串,每个数据项都不用单引号引起来
	 * 
	 * @param stringList
	 * @return
	 */
	public static String getStringByListNoQuotation(List stringList)
	{
		return getStringByList(stringList, false);
	}

	/**
	 * 根据String List获得用逗号分割的字符串，如果withQuotation=true则每个数据项都用单引号引起来
	 * 
	 * @param stringList
	 * @param withQuotation
	 * @return
	 */
	public static String getStringByList(List stringList, boolean withQuotation)
	{
		if (stringList == null || stringList.size() == 0)
		{
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < stringList.size(); i++)
		{
			if (withQuotation)
			{
				sb.append("'");
			}
			sb.append(stringList.get(i));
			if (withQuotation)
			{
				sb.append("'");
			}
			if (i != stringList.size() - 1)
			{
				sb.append(",");
			}
		}

		return sb.toString();
	}
	/**
	 * 根据String List获得用separator分割的字符串
	 * 
	 * @param stringList
	 * @param withQuotation
	 * @return
	 */
	public static String getStringByList(List stringList,String separator)
	{
		if (stringList == null || stringList.size() == 0)
		{
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < stringList.size(); i++)
		{
			sb.append(stringList.get(i));
			if (i != stringList.size() - 1)
			{
				sb.append(separator);
			}
		}

		return sb.toString();
	}
	
	/**
	 * 根据逗号后者分号将字符串拆分为list，注意：逗号和分号都会认作是分割符
	 * @param listString
	 * @return
	 */
	public static List<String> getStringListByString(String listString)
	{
		List<String> stringList=new ArrayList<String>();
		if (!isEmpty(listString))
		{
			String[] temp=listString.split(",|;");
			for (int i = 0; i < temp.length; i++)
			{
				if (!isEmpty(temp[i]))
				{
					stringList.add(temp[i]);
				}
			}
		}
		
		return stringList;
	}
	
	/**
	 * 判断字符串是否为null或为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str)
	{
		return str==null||str.trim().equals("");
	}
}
