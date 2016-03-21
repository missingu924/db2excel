/**
 * 
 */
package com.inspur.db2excel.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * @author caoshl
 * 
 */
public class Custfunc
{
	public static Properties argsProperties = new Properties();
	private static Logger log = Logger.getLogger(Custfunc.class);
	private static Pattern paraPattern = Pattern.compile("\\$\\s*(\\w+)\\s*\\$");
	private static Pattern funcPattern = Pattern.compile("\\$\\s*CustFunc:(\\w+)\\s*\\((.*?)\\)\\s*\\$");
	private static Class cls = null;
	private static Object obj = null;

	public static void main(String[] args)
	{

	}

	private static void init()
	{
		try
		{
			cls = Class.forName(Custfunc.class.getName());
			 obj = cls.newInstance();
		} catch (ClassNotFoundException e)
		{
			log.error("", e);
		} catch (InstantiationException e)
		{
			log.error("", e);
		} catch (IllegalAccessException e)
		{
			log.error("", e);
		}

	}

	public static String getEffectValue(String rawSql)
	{
		String retSql = replacePara(rawSql);
		retSql = replaceFunc(retSql);
		return retSql;

	}

	private static String replacePara(String expression)
	{
		Matcher paraMatcher = paraPattern.matcher(expression);
		StringBuffer paraStrBuf = new StringBuffer();
		while (paraMatcher.find())
		{
			String paraName = paraMatcher.group(1);
			String paraValue = argsProperties.getProperty(paraName);
			if (paraValue == null)
			{
				paraValue = "";
			} else
			{
				paraValue = paraValue.trim();
			}
			paraMatcher.appendReplacement(paraStrBuf, paraValue);
		}
		paraMatcher.appendTail(paraStrBuf);
		expression = paraStrBuf.toString();
		return expression;

	}

	private static String replaceFunc(String expression)
	{

		Matcher funcMatcher = funcPattern.matcher(expression);
		StringBuffer funcStrBuf = new StringBuffer();
		while (funcMatcher.find())
		{
			String funcName = funcMatcher.group(1);
			String paras = funcMatcher.group(2);
			String funcValue = getFunctionValue(funcName, paras);
			if (funcValue == null)
			{
				funcValue = "";
			}
			funcMatcher.appendReplacement(funcStrBuf, funcValue);
		}
		funcMatcher.appendTail(funcStrBuf);
		expression = funcStrBuf.toString();
		return expression;
	}

	/**
	 * 获取函数值
	 * 
	 * @param functionName
	 * @param params
	 * @return
	 */
	private static String getFunctionValue(String functionName, String params)
	{
		Object[] paraArray;
		Class partypes[];
		int paraNum = 0;
		if (params != null && !params.equals(""))
		{
			paraArray = params.split(",");
			paraNum = paraArray.length;
		} else
		{
			paraArray = null;
		}
		log.debug("functionName : " + functionName);
		log.debug("params :" + params);
		if (paraNum != 0)
		{
			partypes = new Class[paraArray.length];
		} else
		{
			partypes = null;
		}
		for (int i = 0; i < partypes.length; i++)
		{
			partypes[i] = String.class;
		}
		Method meth = null;
		String ret = null;
		String mothedKey = functionName + "_$_" + paraNum;
		try
		{
			if (cls == null)
			{
				init();
			}
			meth = cls.getMethod(functionName, partypes);
			ret = (String) meth.invoke(obj, paraArray);
		} catch (NoSuchMethodException e)
		{
			log.error(e.getMessage() + "no such method");
		} catch (IllegalAccessException e)
		{
			log.error(e.getMessage());
		} catch (InvocationTargetException e)
		{
			log.error(e.getMessage());
		}
		return ret;
	}

	/**
	 * 获取时间
	 * 
	 * @param formatType
	 *            返回时间格式
	 * @param delayTime
	 *            相对当前时间调整的时间，如 -D1/-H2/-m3 ，具体时间格式与指标引擎中时间表达式一直
	 * @param propertyId
	 *            属性id，用于手工执行时，通过命令行数据具体的时间， 如propertyId
	 *            未starttime，则在启动命令行中可以输入starttime=2008-12-30 30:00:00
	 * 
	 * @return
	 */
	public String getTime(String formatType, String delayTime, String propertyId)
	{
		String retTime = "";
		if (argsProperties.containsKey(propertyId))
		{
			retTime = argsProperties.getProperty(propertyId);
		} else
		{
			retTime = TimeUtil.computeDate(delayTime, formatType);
		}
		log.info("getTime: " + retTime + " argsProperties.getProperty(propertyId):"
				+ argsProperties.getProperty(propertyId));
		return retTime;
	}

	/**
	 * 获取命令行传递的参数，如果为传递，则返回defaultValue getArgsProperty
	 * 
	 * @param defaultValue
	 * @param propertyId
	 *            属性id，用于手工执行时，通过命令行数据值，如province=shandong
	 * @return
	 */
	public String getArgsProperty(String defaultValue, String propertyId)
	{
		String retArgProperty = "";
		if (argsProperties.containsKey(propertyId))
		{
			retArgProperty = argsProperties.getProperty(propertyId);
		} else
		{
			retArgProperty = defaultValue;
		}
		log.info("getArgsProperty: " + retArgProperty + " argsProperties.getProperty(propertyId):"
				+ argsProperties.getProperty(propertyId));
		return retArgProperty;
	}
}
