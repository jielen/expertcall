/**
 * 
 */
package com.ufgov.second;

/**
 * 专家呼叫对象，包括专家、电话、短信、语音文本、专家类别等
 * @author Administrator
 */
public class ExpertCallInfo {

  private String objid;

  private String expertCode;

  private String mobile;

  private String callMsg;

  private int isCall;

  private String billCode;

  private String messageMsg;

  private String expertTypeCode;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((objid == null) ? 0 : objid.hashCode());
    result = prime * result + ((expertCode == null) ? 0 : expertCode.hashCode());
    result = prime * result + ((mobile == null) ? 0 : mobile.hashCode());
    result = prime * result + ((callMsg == null) ? 0 : callMsg.hashCode());
    result = prime * result + isCall;
    result = prime * result + ((billCode == null) ? 0 : billCode.hashCode());
    result = prime * result + ((messageMsg == null) ? 0 : messageMsg.hashCode());
    result = prime * result + ((expertTypeCode == null) ? 0 : expertTypeCode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
    
    ExpertCallInfo e=(ExpertCallInfo)obj;
    
    if(objid==null){
      if(e.objid!=null){
        return false;
      }
    }else if(!objid.equals(e.objid)){
      return false;
    }
    
    if(expertCode==null){
      if(e.expertCode!=null){
        return false;
      }
    }else if(!expertCode.equals(e.expertCode)){
      return false;
    }
    
    if(mobile==null){
      if(e.mobile!=null){
        return false;
      }
    }else if(!mobile.equals(e.mobile)){
      return false;
    }
    
    if(callMsg==null){
      if(e.callMsg!=null){
        return false;
      }
    }else if(!callMsg.equals(e.callMsg)){
      return false;
    }
    
    if(isCall!=e.isCall){
      return false;
    }
    
    if(billCode==null){
      if(e.billCode!=null){
        return false;
      }
    }else if(!billCode.equals(e.billCode)){
      return false;
    }
    
    if(messageMsg==null){
      if(e.messageMsg!=null){
        return false;
      }
    }else if(!messageMsg.equals(e.messageMsg)){
      return false;
    }
    
    if(expertTypeCode==null){
      if(e.expertTypeCode!=null){
        return false;
      }
    }else if(!expertTypeCode.equals(e.expertTypeCode)){
      return false;
    }
    
    return true;
  }

  @Override
  public String toString() {
    StringBuffer sb=new StringBuffer();
    sb.append("objid=").append(objid);
    sb.append(",expertCode=").append(expertCode);
    sb.append(",mobile=").append(mobile);
    sb.append(",callMsg=").append(callMsg);
    sb.append(",isCall=").append(isCall);
    sb.append(",billCode=").append(billCode);
    sb.append(",messageMsg=").append(messageMsg);
    sb.append(",expertTypeCode=").append(expertTypeCode);
    return sb.toString();
  }

  public String getObjid() {
    return objid;
  }

  public void setObjid(String objid) {
    this.objid = objid;
  }

  public String getExpertCode() {
    return expertCode;
  }

  public void setExpertCode(String expertCode) {
    this.expertCode = expertCode;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getCallMsg() {
    return callMsg;
  }

  public void setCallMsg(String callMsg) {
    this.callMsg = callMsg;
  }

  public int getIsCall() {
    return isCall;
  }

  public void setIsCall(int isCall) {
    this.isCall = isCall;
  }

  public String getBillCode() {
    return billCode;
  }

  public void setBillCode(String billCode) {
    this.billCode = billCode;
  }

  public String getMessageMsg() {
    return messageMsg;
  }

  public void setMessageMsg(String messageMsg) {
    this.messageMsg = messageMsg;
  }

  public String getExpertTypeCode() {
    return expertTypeCode;
  }

  public void setExpertTypeCode(String expertTypeCode) {
    this.expertTypeCode = expertTypeCode;
  }

}
