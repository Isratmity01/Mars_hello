package com.grameenphone.mars.utility;

/**
 * Created by fahad on 3/27/17.
 */

public class Compare {

    public static String getRoomName(String sender, String receiver){
        String chatroom = "";
        int compare = sender.compareTo(receiver);
        if(compare < 0){
            chatroom = sender+"_"+receiver;
        }
        else chatroom = receiver+"_"+sender;

        return chatroom;
    }
}
