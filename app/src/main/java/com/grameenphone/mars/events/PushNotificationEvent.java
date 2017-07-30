package com.grameenphone.mars.events;



public class PushNotificationEvent {
    private String title;
    private String message;
    private String sender;
    private String roomid;
    private String fcmToken;
    public PushNotificationEvent() {
    }

    public PushNotificationEvent(String title, String message, String sender, String roomid, String fcmToken) {
        this.title = title;
        this.message = message;
        this.sender = sender;
        this.roomid = roomid;
        this.fcmToken = fcmToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}