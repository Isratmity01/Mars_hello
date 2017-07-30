package com.grameenphone.mars.model;


/**
 * Created by shadman.rahman on 04-Jun-17.
 */

public class CallDetails{


    private String CallingTo;

    private long CallInTime;

    private String CallType;
    private String imgUrl;
    private String uid;
    public CallDetails(String callingTo, long callInTime, String callType,String Userid,String imgUrl2) {
        CallingTo = callingTo;
        CallInTime = callInTime;
        CallType = callType;
        this.uid=Userid;
        this.imgUrl=imgUrl2;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public CallDetails() {
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCallingTo() {
        return CallingTo;
    }

    public void setCallingTo(String callingTo) {
        CallingTo = callingTo;
    }

    public long getCallInTime() {
        return CallInTime;
    }

    public void setCallInTime(long callInTime) {
        CallInTime = callInTime;
    }

    public String getCallType() {
        return CallType;
    }

    public void setCallType(String callType) {
        CallType = callType;
    }
}
