package com.grameenphone.mars.model;


public class ChatRoom {

    private String roomId;
    private String name;
    private String photoUrl;
    private String type;

    private Chat lastChat;
    private String unreadMessageCount;





    public ChatRoom() {
    }

    public ChatRoom(String roomId, String name, String photoUrl, String type) {
        this.roomId = roomId;
        this.name = name;
        this.photoUrl = photoUrl;
        this.type = type;
    }


    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Chat getLastChat() {
        return lastChat;
    }

    public void setLastChat(Chat lastChat) {
        this.lastChat = lastChat;
    }

    public String getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(String unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

}
