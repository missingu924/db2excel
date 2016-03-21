package com.inspur.db2excel.obj;

import com.inspur.db2excel.util.Custfunc;

public class Db2Excel
{
	private String name;
	private String sql;
	private String sendSms;

	public String getSendSms()
	{
		return sendSms;
	}

	public void setSendSms(String sendSms)
	{
		this.sendSms = sendSms;
	}

	public String getName()
	{
		return Custfunc.getEffectValue(name);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSql()
	{
		return Custfunc.getEffectValue(sql);
		// return sql;
	}

	public void setSql(String sql)
	{
		this.sql = sql;
	}
}
