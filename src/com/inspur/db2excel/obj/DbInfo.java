package com.inspur.db2excel.obj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.NamingException;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: Inspur LG Information Systems Co.,Ltd.
 * </p>
 * 
 * @author Wu Yugang
 * @version 1.0
 */
public class DbInfo
{
	private String id;
	private String driverClassName;
	private String url;
	private String username;
	private String password;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getDriverClassName()
	{
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName)
	{
		this.driverClassName = driverClassName;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public Connection getDBConnection() throws SQLException, NamingException, ClassNotFoundException,
			IllegalAccessException, InstantiationException
	{
		Class.forName(driverClassName).newInstance();
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

}
