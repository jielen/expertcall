package com.ufgov.util;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * added by mengw 20100730 日志记录组建，默认在c盘的system.log  下
 * */

public class LogWritter {
  public static Logger log = null;
  static {
    if (log == null) {
      log = Logger.getLogger("LogWritter.class");
      SimpleLayout layout = new SimpleLayout();
      FileAppender appender = null;
      try {
        appender = new FileAppender(layout, "c:/system.log", false);
      } catch (Exception e) {
      }
      log.addAppender(appender);

    }
  }

  public static void println(String str) {
    System.out.println(str);
    log.info(str);
  }

  public static void println(String str, int code) {

    System.out.println("线程" + code + "日志：" + str);
    log.info("线程" + code + "日志：" + str);
  }

}
