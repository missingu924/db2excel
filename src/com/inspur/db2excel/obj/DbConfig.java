package com.inspur.db2excel.obj;

import java.util.ArrayList;
import java.util.List;

public class DbConfig
{
	private String version;
	private List<DbInfo> dbInfoList = new ArrayList<DbInfo>();

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public List<DbInfo> getDbInfoList()
	{
		return dbInfoList;
	}

	public void setDbInfoList(List<DbInfo> dbInfoList)
	{
		this.dbInfoList = dbInfoList;
	}

	public void addDbInfoList(DbInfo dbInfo)
	{
		this.dbInfoList.add(dbInfo);
	}

	public DbInfo getDbInfoById(String dbId)
	{
		for (int i = 0; i < dbInfoList.size(); i++)
		{
			DbInfo dbInfo = dbInfoList.get(i);
			if (dbInfo.getId().equalsIgnoreCase(dbId))
			{
				return dbInfo;
			}
		}
		return null;
	}
}
