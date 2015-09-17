package com.ufgov.util;

import java.util.Stack;

/**
 * added by mengw 20100823 限制多线程不同时调用同一个数据
 * */
public class StackUtil {

  public static Stack stackData;

  public static Stack stackCh;

  public static Stack stackRing;
  static {
    if (stackData == null) {
      stackData = new Stack();

    }
    if (stackCh == null) {
      stackCh = new Stack();

    }
    if (stackRing == null) {
      stackRing = new Stack();

    }
  }

  public static void pushData(Object o) {

    stackData.push(o);
  }

  public static void removeData(Object o) {

    stackData.remove(o);
  }

  public static void removeCh(Object o) {

    stackCh.remove(o);
  }

  public static void pushCh(Object o) {

    stackCh.push(o);
  }

  public static void removeRing(Object o) {

    stackRing.remove(o);
  }

  public static void pushRing(Object o) {

    stackRing.push(o);
  }
}
