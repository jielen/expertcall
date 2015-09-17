package com.ufgov.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
import com.ufgov.server.CallServer;

public class ExpertUtil {
  
	/*
	 * 等待打电话的专家信息
	 */
  private static  List waitingCallExpertList=new ArrayList();
  
  /*
   * 正在拨打电话的专家信息
   */
  private static Hashtable callingExpertHt=new Hashtable();
  
  /**
   * 获取等待打电话的专家
 * @param callServer 
   * @return
   * @throws Exception
   */
  public static synchronized Map<String, String>  getWaitingCallExpert()throws Exception {
    Map<String, String> expertInfo=new HashMap<String, String>();
    if(waitingCallExpertList==null || waitingCallExpertList.size()==0){
    	Object[] params = new Object[] { CallServer.CALL_NUM, 0 };
        waitingCallExpertList = DAOFactory.queryToListMap(CallServer.GET_EM_CALL_SERVER_LIST, params); 
    }
    
    if (waitingCallExpertList ==null || waitingCallExpertList.size() ==0)
    	return null;
        
    expertInfo=(HashMap)waitingCallExpertList.get(waitingCallExpertList.size()-1);
    waitingCallExpertList.remove(waitingCallExpertList.size()-1);
    String key=expertInfo.get("OBJID");
    if(callingExpertHt.containsKey(key)){
    	return null;
    }else{
    	callingExpertHt.put(key, expertInfo);
    }
    return expertInfo;
  }
  
  /**
   * 判断这个单据是否有电话在拨打
   * @param emBillCode
   * @return
   */
  public static synchronized boolean isCalling(String emBillCode){
	  if(waitingCallExpertList!=null){
		  for(int i=0;i<waitingCallExpertList.size();i++){			  
				  Map<String, String> expertInfo=(HashMap)waitingCallExpertList.get(i);
				  if(emBillCode.equals(expertInfo.get("EM_BILL_CODE"))){
					  return true;
				  }
		  }
	  }
	  
	  if(callingExpertHt!=null){
		  Enumeration<String> keys=callingExpertHt.keys();
		  while(keys.hasMoreElements()){
			  String key=keys.nextElement();
			  Map<String, String> expertInfo=(HashMap)callingExpertHt.get(key);
			  if(expertInfo!=null && emBillCode.equals(expertInfo.get("EM_BILL_CODE"))){
				  return true;
			  }
		  }
	  }	  
	  return false;
  }
  
  public static  synchronized void compeletCalling(String objID){
	  callingExpertHt.remove(objID);
  }

}
