package com.inspur.config;

import com.inspur.config.ConfigReader;
import com.inspur.config.FileWatchdog;

public class ConfigFileWatchDog
    extends FileWatchdog
{
   public ConfigFileWatchDog(String filename)
   {
      super(filename);
   }

   protected void doOnChange()
   {
      System.out.println("ConfigFileWatchdog发现配置文件发生改变,weoweo... :)");
      ConfigReader.clearProperties();
   }
}
