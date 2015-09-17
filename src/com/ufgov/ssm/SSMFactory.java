package com.ufgov.ssm;

import org.xvolks.jnative.JNative;
import org.xvolks.jnative.Type;
import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;

public class SSMFactory {
  public static int ssmPlayFile(int ch, String pszFileName, int nFormat, int dwStartPos, int dwLen)
    throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmPlayFile");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + ch);
      n.setParameter(1, Type.STRING, pszFileName);
      n.setParameter(2, Type.INT, "" + nFormat);
      n.setParameter(3, Type.INT, "" + dwStartPos);
      n.setParameter(4, Type.INT, "" + dwLen);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static Pointer creatPointer(int size) throws NativeException, IllegalAccessException {
    Pointer pointer = new Pointer(MemoryBlockFactory.createMemoryBlock(size));
    pointer.setIntAt(0, size);
    return pointer;
  }

  public static int ssmGet1stDtmfClr(int ch, Pointer pcDtmf) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmGet1stDtmfClr");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + ch);
      n.setParameter(1, pcDtmf);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmAutoDial(int ch, String phNum) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmAutoDial");
      n.setRetVal(Type.INT);
      int i = 0;
      n.setParameter(i++, Type.INT, "" + ch);
      n.setParameter(i++, Type.STRING, phNum);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmChkAutoDial(int ch) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmChkAutoDial");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + ch);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmCheckPlay(int ch) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmCheckPlay");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + ch);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmClearRxDtmfBuf(int ch) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmClearRxDtmfBuf");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + ch);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmHangup(int ch) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmHangup");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + ch);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmGetChState(int ch) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmGetChState");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + ch);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmSearchIdleCallOutCh(int wSearchMode, int dwPrecedence) throws NativeException,
    IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmSearchIdleCallOutCh");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + wSearchMode);
      n.setParameter(1, Type.INT, "" + dwPrecedence);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmPickup(int ch) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmPickup");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + ch);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmGetMaxCh() throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmGetMaxCh");
      n.setRetVal(Type.INT);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmGetChType(int ch) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmGetChType");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + ch);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static void ssmGetLastErrMsg(Pointer errMsgStr) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmGetLastErrMsg");
      n.setRetVal(Type.VOID);
      n.setParameter(0, errMsgStr);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public static int ssmGetAMDResult(int ch) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmGetAMDResult");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, ch + "");
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmClearAMDResult(int ch) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmClearAMDResult");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + ch);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static void ssmStopPlay(int ch) throws NativeException, IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmStopPlay");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + ch);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public static int ssmSetHangupStopPlayFlag(int ch, int bHangupStopPlayFlag) throws NativeException,
    IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmSetHangupStopPlayFlag");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + ch);
      n.setParameter(1, Type.INT, "" + bHangupStopPlayFlag);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmWaitForEvent(int dwTimeOut, Pointer pEvent) throws NativeException,
    IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmWaitForEvent");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, "" + dwTimeOut);
      n.setParameter(1, pEvent);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

  public static int ssmStartCti(String shconfig, String shindex) throws NativeException,
    IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmStartCti");
      n.setRetVal(Type.INT);
      int i = 0;
      n.setParameter(i++, Type.STRING, shconfig);
      n.setParameter(i++, Type.STRING, shindex);
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }
  

  public static int SsmSetDtmfStopPlay(int ch, boolean bTerminatePlaybackOnDTMF) throws NativeException,
    IllegalAccessException {
    JNative n = null;
    try {
      n = new JNative("SHP_A3", "SsmSetDtmfStopPlay");
      n.setRetVal(Type.INT);
      int i = 0;
      n.setParameter(i++, Type.INT, ""+ch);
      n.setParameter(i++, Type.INT, ""+(bTerminatePlaybackOnDTMF==true?1:0));
      n.invoke();
    } catch (NativeException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return Integer.parseInt(n.getRetVal());
  }

}
