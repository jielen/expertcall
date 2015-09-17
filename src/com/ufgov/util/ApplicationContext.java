package com.ufgov.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class ApplicationContext {

  // ≈‰÷√Œƒº˛√˚
  public static final String propName = "emc.properties";

  private static Map contextPool = null;

  private static ApplicationContext applicationContext = null;

  private ApplicationContext() {
    init();
  }

  private void init() {
    contextPool = new HashMap();
    loadProperties();

  }

  private void loadProperties() {
    Properties props = this.getProperties();
    Iterator iter = props.keySet().iterator();
    while (iter != null && iter.hasNext()) {
      String key = (String) iter.next();
      String value = props.getProperty(key);
      synchronized (contextPool) {
        try {
          contextPool.put(key, new String(value.getBytes("ISO-8859-1"), "GBK"));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private Properties getProperties() {
    Properties props = new Properties();
    try {
      props.load(getClass().getClassLoader().getResourceAsStream(propName));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }
    return props;
  }

  public static ApplicationContext singleton() {
    if (applicationContext == null) {
      applicationContext = new ApplicationContext();

    }
    return applicationContext;
  };

  public Object getValue(String key) {
    return contextPool.get(key);
  }

  public String getValueAsString(String key) {
    return (String) this.getValue(key);
  }

}