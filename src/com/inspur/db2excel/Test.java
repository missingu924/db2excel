package com.inspur.db2excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class Test
{
	public static void main(String[] args)
	{

		try
		{
//			POIFSFileSystem ps = new POIFSFileSystem(new FileInputStream("c:/test.xls"));
//			// 创建excel文档
//			HSSFWorkbook wk = new HSSFWorkbook(ps);
//
//			oneTable2Excel(wk,1,"test");
//
//			// 保存excel
//			saveToFile(wk, "c:/test.xls");
			
			StringBuffer smsContent=new StringBuffer("a,b,c,");
			System.out.println(smsContent.replace(smsContent.lastIndexOf(","), smsContent.length(), ""));
			System.out.println(smsContent);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void oneTable2Excel(HSSFWorkbook wk, int sheetNum, String sheetName)
			throws SQLException
	{
		LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();

		// 构造样式
		HSSFCellStyle style = getHeadStyle(wk);
		// 构造sheet
		HSSFSheet sheet = createSheet(wk, sheetNum++, sheetName);
		// 构造表头
		constructDataHead(sheet, props, style, 0);

		int i = 1;

		HSSFRow row = sheet.createRow(i++);

		HSSFCell cell = row.createCell(1);
		cell.setCellStyle(getDataCellStyle(wk));

		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		cell.setCellValue(100);

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
			int fromRow)
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
		}
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
}
