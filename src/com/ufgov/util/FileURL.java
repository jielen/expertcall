package com.ufgov.util;

import java.net.URL;

/**
 * 获取文件的url连接 added by mengw 20100803
 */

public class FileURL {
  public FileURL() {

  }

  /**
   * 得到文件的URL连接 ;
   */
  public URL getFileURL(String fileName) {
    ClassLoader loader = this.getClass().getClassLoader();
    URL fileUrl = loader.getResource(fileName);
    return fileUrl;

  }

}