package com.inspur.db2excel.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class OracleUtil
{
	public static String LOAD_MODE_INSERT = "INSERT";
	public static String LOAD_MODE_APPEND = "APPEND";
	public static String LOAD_MODE_REPLACE = "REPLACE";
	public static String LOAD_MODE_TRUNCATE = "TRUNCATE";

	private static Logger log = Logger.getLogger(OracleUtil.class);

	private static final int ORA_IN_LIMIT = 1000;// oracle in 条件里面数据项上限

	/**
	 * 构造sql里面的in条件，由于oracle里面对于in条件里面的数据项有限制，不能超过1000个，所以对于超过1000个数据项的查询需特殊处理
	 * 
	 * @param neUniqueKeys
	 * @return
	 */
	public static StringBuffer getOraInSql(String columnName, List<String> values)
	{
		StringBuffer sql = new StringBuffer();

		List<String> inList = new ArrayList<String>();
		String inSql = "";
		for (int i = 0; i < values.size(); i++)
		{
			inSql += "'" + values.get(i) + "',";

			// 每1000个处理一次
			if ((i + 1) % ORA_IN_LIMIT == 0 || (i == values.size() - 1))
			{
				inList.add(inSql.substring(0, inSql.length() - 1));// 去掉最后一个逗号
				inSql = "";
			}
		}

		for (int i = 0; i < inList.size(); i++)
		{
			if (i > 0)
			{
				sql.append(" or ");
			}
			sql.append(columnName).append(" in (").append(inList.get(i)).append(") ");
		}

		return sql;
	}

	public static void load(String userid, String tableName, List<String> columns, String loadMode, String dataFilePath)
			throws Exception
	{
		// 1、all files
		File dataFile = new File(dataFilePath);

		File baseDir = dataFile.getParentFile().getParentFile();
		if (!baseDir.exists())
		{
			baseDir.mkdirs();
		}
		String baseFileName = dataFile.getName();

		File controlFile = new File(baseDir + "/oractl/" + baseFileName + ".ctl");
		if (!controlFile.getParentFile().exists())
		{
			controlFile.getParentFile().mkdirs();
		}

		File logFile = new File(baseDir + "/oralog/" + baseFileName + ".log");
		if (!logFile.getParentFile().exists())
		{
			logFile.getParentFile().mkdirs();
		}

		File badFile = new File(baseDir + "/orabad/" + baseFileName + ".bad");
		if (!badFile.getParentFile().exists())
		{
			badFile.getParentFile().mkdirs();
		}
		
		File archiveFile = new File(baseDir + "/oraarc/" + baseFileName + ".arc");
		if (!archiveFile.getParentFile().exists())
		{
			archiveFile.getParentFile().mkdirs();
		}

		// 2、oracle control file
		PrintWriter pw = new PrintWriter(controlFile);
		pw.println(" LOAD DATA");
		pw.println(" CHARACTERSET ZHS16GBK");
		pw.println(" INFILE '" + dataFile.getAbsolutePath() + "'");
		if (StringUtil.isEmpty(loadMode))
		{
			loadMode = LOAD_MODE_INSERT;
		}
		pw.println(loadMode);
		pw.println(" into table " + tableName + " fields terminated by '|' ");
		pw.println(" (");

		for (int i = 0; i < columns.size(); i++)
		{
			pw.print("\t" + columns.get(i));
			if (i != columns.size() - 1)
			{
				pw.println(",");
			}
		}

		pw.println("\n )");
		pw.close();

		// 3、shell
		String shell = "sqlldr userid="
				+ userid
				+ " control="
				+ controlFile.getAbsolutePath()
				+ " log="
				+ logFile.getAbsolutePath()
				+ "  bad= "
				+ badFile.getAbsolutePath()
				+ " parallel=true, bindsize=20000000, readsize=20000000 ,ERRORS=999999999, ROWS=5000,COLUMNARRAYROWS = 10000";

		log.info(dataFile.getAbsoluteFile() + ":" + shell);

		// 4、run
		String exeResult;
		Process process = Runtime.getRuntime().exec(shell);
		BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while ((exeResult = out.readLine()) != null)
		{
			log.info(dataFile.getAbsoluteFile() + ":" + exeResult);
		}
		while ((exeResult = error.readLine()) != null)
		{
			log.error(dataFile.getAbsoluteFile() + ":" + exeResult);
		}
		process.waitFor();
		
		//5、archive
		dataFile.renameTo(archiveFile);

	}
}
