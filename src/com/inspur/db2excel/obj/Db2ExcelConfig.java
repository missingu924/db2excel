package com.inspur.db2excel.obj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.inspur.db2excel.util.Custfunc;
import com.inspur.db2excel.util.FileUtil;
import com.inspur.db2excel.util.StringUtil;
import com.inspur.db2excel.util.TimeUtil;

public class Db2ExcelConfig
{
	private Logger log=Logger.getLogger(getClass());
	
	private final String MONTH = "MONTH";
	private final String DAY = "DAY";
	private final String HOUR = "HOUR";

	private String version;

	private String dbId;

	private String dir;

	private String subDirType;

	private String name;

	private String templet;

	private int fromSheetNum;

	private List<Db2Excel> db2ExcelList = new ArrayList<Db2Excel>();

	public String getName()
	{
		return Custfunc.getEffectValue(name);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<Db2Excel> getDb2ExcelList()
	{
		return db2ExcelList;
	}

	public void setDb2ExcelList(List<Db2Excel> db2ExcelList)
	{
		this.db2ExcelList = db2ExcelList;
	}

	public void addDb2Excel(Db2Excel db2Excel)
	{
		this.db2ExcelList.add(db2Excel);
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getDir()
	{
		return Custfunc.getEffectValue(dir);
	}

	public void setDir(String dir)
	{
		this.dir = dir;
	}

	public String getDbId()
	{
		return Custfunc.getEffectValue(dbId);
	}

	public void setDbId(String dbId)
	{
		this.dbId = dbId;
	}

	public String getSubDirType()
	{
		return subDirType;
	}

	public void setSubDirType(String subDirType)
	{
		this.subDirType = subDirType;
	}

	public String getExcelFilePath()
	{
		String subDir = "";
		if (this.MONTH.equalsIgnoreCase(this.subDirType))
		{
			subDir = TimeUtil.nowTime2str("yyyyMM");
		} else if (this.DAY.equalsIgnoreCase(this.subDirType))
		{
			subDir = TimeUtil.nowTime2str("yyyyMMdd");
		} else if (this.HOUR.equalsIgnoreCase(this.subDirType))
		{
			subDir = TimeUtil.nowTime2str("yyyyMMddHH");
		}

		String filePath = "../excel/" + getDir() + "/" + subDir + "/" + getName() + "-"
				+ TimeUtil.nowTime2str("yyyyMMdd_HHmmss") + ".xls";

		
		return filePath;
	}

	public int getFromSheetNum()
	{
		return fromSheetNum;
	}

	public void setFromSheetNum(int fromSheetNum)
	{
		this.fromSheetNum = fromSheetNum;
	}

	public String getTemplet()
	{
		return templet;
	}

	public void setTemplet(String templet)
	{
		this.templet = templet;
	}
}
