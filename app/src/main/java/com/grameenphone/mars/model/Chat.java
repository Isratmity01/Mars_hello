package com.grameenphone.mars.model;


public class Chat {
    public String chatId;
    public String sender;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String message;
    public long timestamp;
    public String photoUrl;
    public int deliveryStatus;
    public int readStatus;
    private String type;
    private String url_file;
    private String name_file;
    private String size_file;
    public FileModel file;
    public String messageType;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl_file() {
        return url_file;
    }

    public void setUrl_file(String url_file) {
        this.url_file = url_file;
    }

    public String getName_file() {
        return name_file;
    }

    public void setName_file(String name_file) {
        this.name_file = name_file;
    }

    public String getSize_file() {
        return size_file;
    }

    public void setSize_file(String size_file) {
        this.size_file = size_file;
    }

    public Chat() {}

    public Chat(String sender, String receiver, String senderUid, String receiverUid, String message, long timestamp, String messageType) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
        this.messageType = messageType;
    }
    public Chat(String chatID,String sender, String receiver, String senderUid, String receiverUid, String message, long timestamp, String messageType) {
       this.chatId=chatID;
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
        this.messageType = messageType;
    }

    public Chat(String sender, String senderUid, String message, long timestamp, String photoUrl, String messageType) {
        this.sender = sender;
        this.senderUid = senderUid;
        this.message = message;
        this.timestamp = timestamp;
        this.photoUrl = photoUrl;
        this.messageType = messageType;
    }


    public Chat(String sender, String receiver, String senderUid, String receiverUid, String photoUrl, String message, long timestamp, FileModel file, String messageType) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.photoUrl = photoUrl;
        this.message = message;
        this.timestamp = timestamp;
        this.file = file;
        this.messageType = messageType;
    }


    public Chat(String sender, String receiver, String senderUid, String receiverUid, long timestamp, String photoUrl, FileModel file) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.timestamp = timestamp;
        this.photoUrl = photoUrl;
        this.file = file;
    }

    public FileModel getFile() {
        return file;
    }

    public void setFile(FileModel file) {
        this.file = file;
    }




    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }


    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }


}