package com.inspur.util.sms;

import java.rmi.Naming;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eoms2.sms.adapter.RmiAdapter;
import com.inspur.config.ConfigReader;

public class SmsRmiAdapter
{
	private static Log log = LogFactory.getLog(SmsRmiAdapter.class);

	public static void sendSms(String mobiles, String content)
	{
		try
		{
			if (mobiles == null || "".equalsIgnoreCase(mobiles))
			{
				log.error("未配置接收短信的手机号码，请到SystemConfig.xml中进行配置。");
				return;
			}

			log.info("---短信RMI连接开始---");
			RmiAdapter smsRmi = (RmiAdapter) Naming.lookup(ConfigReader.getProperties("sms.smsRmiAddress"));
			log.info("---短信RMI连接成功---");

			StringBuilder sms = new StringBuilder();
			sms.append("<message><address>").append(mobiles).append("</address>").append("<content>").append(content)
					.append("</content></message>");

			log.info("---短信内容：" + sms + "---");

			smsRmi.sendSms(sms.toString());

			log.info("---短信发送成功---");
		} catch (Exception e)
		{
			log.error("---短信发送失败---");
			log.error(e.getMessage(), e);
		}

	}

	public static void main(String[] args)
	{
		try
		{
			sendSms("", "");
		} catch (Exception e)
		{
			System.out.println("---短信发送失败!---");
			e.printStackTrace();
		}

	}

}
