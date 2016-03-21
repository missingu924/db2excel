package com.inspur.db2excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.inspur.config.ConfigReader;
import com.inspur.db2excel.obj.Db2Excel;
import com.inspur.db2excel.obj.Db2ExcelConfig;
import com.inspur.db2excel.obj.DbConfig;
import com.inspur.db2excel.util.Custfunc;
import com.inspur.db2excel.util.DigesterUtil;
import com.inspur.db2excel.util.FileUtil;
import com.inspur.db2excel.util.StringUtil;
import com.inspur.util.sms.SmsRmiAdapter;

public class Main
{
	private static Logger log = Logger.getLogger(Main.class);
	private static String configFile;

	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("请指定配置文件");
			System.exit(0);
		} else
		{
			configFile = args[0];
		}
		Pattern pattern = Pattern.compile("(\\w+)\\s*=\\s*([^=]+)\\s+");

		/**
		 * 记录命令行参数
		 */
		StringBuffer argBuffer = new StringBuffer();
		if (args.length > 1)
		{
			for (int i = 1; i < args.length; i++)
			{
				argBuffer.append(args[i]);
				argBuffer.append(" ");
			}
		}
		Matcher matcher = pattern.matcher(argBuffer.toString());
		while (matcher.find())
		{
			String property = matcher.group(1);
			String propertyValue = matcher.group(2).trim();
			Custfunc.argsProperties.put(property, propertyValue);
		}
		// String rawSql = "select * \n" + "from TDMRR.TD_MR_AMRULBLERLOG_CELL
		// log\n"
		// + " where STARTTIME>=to_date('$starttime$','yyyyMMdd')\n"
		// +
		// "STARTTIME>=to_date('$CustFunc:getTime(yyyyMMdd,-D1,starttime)$','yyyyMMdd')\n"
		// + " and
		// STARTTIME<to_date('$CustFunc:getTime(yyyyMMdd,-D0,endtime)$','yyyyMMdd')\n"
		// + " and PROVINCE_ID='$CustFunc:getArgsProperty(SD,PROVINCE_ID)$'\n"
		// + " and
		// PROVINCE_ID='$CustFunc:getArgsProperty(SD,$PROVINCE_ID$)$'\n"+ "
		// group by log.CITY_ID";
		// System.out.println(Custfunc.getEffectSql(rawSql));
		// System.exit(0);

		String[] configFiles = configFile.split(";|,");

		for (int i = 0; i < configFiles.length; i++)
		{
			db2excel(configFiles[i]);
		}
	}

	private static void db2excel(String configFile)
	{
		Connection conn = null;
		try
		{
			// 解析配置文件
			Db2ExcelConfig db2ExcelConfig = (Db2ExcelConfig) DigesterUtil.parseFromURL(Db2ExcelConfig.class, Thread
					.currentThread().getContextClassLoader().getResource(configFile));

			DbConfig dbConfig = (DbConfig) DigesterUtil.parseFromURL(DbConfig.class, Thread.currentThread()
					.getContextClassLoader().getResource("DbConfig.xml"));

			// 建立excel文件
			HSSFWorkbook wk = null;
			String filePath = db2ExcelConfig.getExcelFilePath();
			// 如果有模板文件，则通过模板文件创建新的excel文件
			if (!StringUtil.isEmpty(db2ExcelConfig.getTemplet()))
			{
				String templetPath = "../excel/" + db2ExcelConfig.getDir() + "/" + db2ExcelConfig.getTemplet();
				File templetXls = new File(templetPath);
				if (templetXls.exists())
				{
					FileUtil.copy(templetPath, filePath);
					log.info(configFile + ":=====Excel文件" + filePath + "开始创建，使用模板" + templetPath + "=====");
				} else
				{
					log.error("模板文件" + templetPath + "不存在，请检查。");
				}

				POIFSFileSystem ps = new POIFSFileSystem(new FileInputStream(filePath));
				// 创建excel文档
				wk = new HSSFWorkbook(ps);
			} else
			{
				// 否则直接创建新的excel文件
				log.info(configFile + ":=====Excel文件" + filePath + "开始创建=====");
				wk = new HSSFWorkbook();
			}

			// 获取数据库连接
			conn = dbConfig.getDbInfoById(db2ExcelConfig.getDbId()).getDBConnection();

			// 逐个sheet生成
			List<Db2Excel> db2ExcelList = db2ExcelConfig.getDb2ExcelList();
			for (int i = 0; i < db2ExcelList.size(); i++)
			{
				Db2Excel db2Excel = db2ExcelList.get(i);
				log.info(configFile + ":-----" + db2Excel.getName() + " 生成开始-----");

				String sheetName = db2Excel.getName();
				String sql = db2Excel.getSql();
				oneTable2Excel(conn, sql, wk, i + db2ExcelConfig.getFromSheetNum(), sheetName, db2Excel.getSendSms());

				log.info(configFile + ":-----" + db2Excel.getName() + " 生成完成-----");
			}

			// 保存excel
			saveToFile(wk, filePath);
			log.info(configFile + ":=====Excel文件" + filePath + "保存完成=====");

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (conn != null)
			{
				try
				{
					conn.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private static void oneTable2Excel(Connection conn, String sql, HSSFWorkbook wk, int sheetNum, String sheetName,
			String sendSms) throws SQLException
	{
		log.info(configFile + ":" + sheetName + " sql: " + sql);

		StringBuffer smsContent = new StringBuffer(sheetName + " ");

		LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();
		ResultSet rst = conn.createStatement().executeQuery(sql);
		ResultSetMetaData metadata = rst.getMetaData();
		for (int i = 1; i <= metadata.getColumnCount(); i++)
		{
			String columnLabel = metadata.getColumnLabel(i);
			log.info("columnLabel" + columnLabel);
			props.put(columnLabel, columnLabel);
		}

		// 构造样式
		HSSFCellStyle style = getHeadStyle(wk);
		// 红色字体样式
		HSSFCellStyle redStyle = getRedStyle(wk);
		// 普通样式
		HSSFCellStyle dataStyle = getDataCellStyle(wk);
		// 构造sheet
		HSSFSheet sheet = wk.getSheet(sheetName);
		if (sheet == null)
		{
			sheet = createSheet(wk, sheetNum++, sheetName);
		} else
		{
			// 清空所有行,这个地方要注意，因为多了表头一行，所以在删除的时候，要在记录的条件的基础上+1
			for (int i = 0; i < sheet.getLastRowNum() + 1; i++)
			{
				sheet.removeRow(sheet.getRow(i));
			}
		}

		// 构造表头
		constructDataHead(sheet, props, style, 0, smsContent);

		int i = 1;
		boolean hasRows = false;
		while (rst.next())
		{
			hasRows = true;
			HSSFRow row = sheet.createRow(i++);
			for (int j = 1; j <= metadata.getColumnCount(); j++)
			{
				HSSFCell cell = row.createCell(j - 1);
				cell.setCellStyle(dataStyle);
				int columnType = metadata.getColumnType(j);
				if (columnType == Types.INTEGER || columnType == Types.NUMERIC || columnType == Types.FLOAT
						|| columnType == Types.DOUBLE || columnType == Types.DECIMAL)
				{
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue(rst.getDouble(j));
					smsContent.append(rst.getDouble(j)).append(",");
				} else
				{
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					String str = rst.getString(j);

					if (str != null && str.endsWith("%"))
					{
						try
						{
							double d = Double.parseDouble(str.substring(0, str.length() - 1));
							if (d < 100)
							{
								cell.setCellStyle(redStyle);
							}
						} catch (Exception e)
						{
						}
					}

					cell.setCellValue(str == null ? "" : str);
					smsContent.append(str == null ? "" : str).append(",");
				}
			}
			if ((i - 1) % 100 == 0)
			{
				log.info(configFile + ":" + sheetName + " 保存了" + i + "条记录");
			}

			if (smsContent.indexOf(",") > 0)
			{
				smsContent.replace(smsContent.lastIndexOf(","), smsContent.length(), "");
			}
			smsContent.append(" ");
		}
		log.info(configFile + ":" + sheetName + " 共保存了" + (i - 1) + "条记录");

		// for (int j = 0; j < metadata.getColumnCount(); j++)
		// {
		// sheet.autoSizeColumn(j);
		// }

		if ("TRUE".equalsIgnoreCase(sendSms) && hasRows)
		{
			SmsRmiAdapter.sendSms(ConfigReader.getProperties("sms.mobiles"), smsContent.toString());
		}
	}

	private static HSSFCellStyle getRedStyle(HSSFWorkbook wk)
	{
		HSSFCellStyle s = wk.createCellStyle();
		HSSFFont font = wk.createFont();
		font.setBoldweight((short) 600); // 加粗
		font.setColor(font.COLOR_RED);
		s.setFont(font);

		s.setBorderBottom((short) 1);
		s.setBorderTop((short) 1);
		s.setBorderLeft((short) 1);
		s.setBorderRight((short) 1);
		return s;
	}

	private static HSSFCellStyle getHeadStyle(HSSFWorkbook wk)
	{
		HSSFCellStyle style = wk.createCellStyle();
		// 居中
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// 设置字体
		HSSFFont font = wk.createFont();
		// font.setFontHeightInPoints((short) 10); // 12号字
		font.setBoldweight((short) 600); // 加粗
		style.setFont(font);

		// 设置边框
		style.setBorderBottom((short) 1);
		style.setBorderTop((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);

		// 设置底色，浅绿色
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setFillForegroundColor(new HSSFColor.LIGHT_GREEN().getIndex());

		return style;
	}

	private static HSSFCellStyle getDataCellStyle(HSSFWorkbook wk)
	{
		HSSFCellStyle style = wk.createCellStyle();

		// 设置边框
		style.setBorderBottom((short) 1);
		style.setBorderTop((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);

		return style;
	}

	private static HSSFSheet createSheet(HSSFWorkbook wk, int sheetNum, String sheetName)
	{
		HSSFSheet sheet = wk.createSheet();
		wk.setSheetName(sheetNum, sheetName);
		return sheet;
	}

	private static void constructDataHead(HSSFSheet sheet, LinkedHashMap<String, String> props, HSSFCellStyle style,
			int fromRow, StringBuffer smsContent)
	{
		HSSFRow head = sheet.createRow(fromRow);
		Iterator keys = props.keySet().iterator();
		int i = 0;
		while (keys.hasNext())
		{
			String key = keys.next().toString();
			String name = props.get(key).toString();
			HSSFCell cell = head.createCell(i++);
			cell.setCellStyle(style);
			cell.setCellValue(name);

			smsContent.append(name).append(",");
		}

		if (smsContent.indexOf(",") > 0)
		{
			smsContent.replace(smsContent.lastIndexOf(","), smsContent.length(), "");
		}
		smsContent.append(" ");
	}

	private static void saveToFile(HSSFWorkbook wk, String filePath) throws FileNotFoundException, IOException
	{
		File file = new File(filePath);
		if (!file.getParentFile().exists())
		{
			file.getParentFile().mkdirs();
		}
		FileOutputStream fileOut = new FileOutputStream(file);
		wk.write(fileOut);
		fileOut.close();
	}

	private static String getMobiles()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
