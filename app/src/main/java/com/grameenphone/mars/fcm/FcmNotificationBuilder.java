package com.grameenphone.mars.fcm;

import android.util.Log;

import com.grameenphone.mars.model.Chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Author: Kartik Sharma
 * Created on: 10/16/2016 , 1:53 PM
 * Project: FirebaseChat
 */

public class FcmNotificationBuilder {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "FcmNotificationBuilder";
    private static final String SERVER_API_KEY = "AAAAIGkI13Q:APA91bFgVNk-R7gse_e3ERpumy8IDJQZDVsbmbSOlPQMMA0yNMq8pB1wtwbJ9Lo8pJ5CM2IeUgZ573JBkLlV2M-T2TWVqyEJ4a6cg7tpjBGziOxbWEsGjrpuwgO7Q4GGNae7QgYUD9WN";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_KEY = "key=" + SERVER_API_KEY;
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    // json related keys
    private static final String KEY_TO = "to";
    private static final String KEY_NOTIFICATION = "notification";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "text";
    private static final String KEY_DATA = "data";
    private static final String KEY_FCM_TOKEN = "fcm_token";
    private static final String KEY_ROOM = "room_uid";
    private static final String KEY_SENDER = "sender";
    private static final String KEY_CHATS ="whatposted";
    private String mTitle;
    private Chat received;
    private String mMessage;
    private String mFirebaseToken;
    private String mReceiverFirebaseToken;
    private String Sender;
    private String roomUid;

    private FcmNotificationBuilder() {

    }

    public static FcmNotificationBuilder initialize() {
        return new FcmNotificationBuilder();
    }


    public FcmNotificationBuilder setReceived(Chat received) {
        this.received = received;
        return this;
    }

    public FcmNotificationBuilder title(String title) {
        mTitle = title;
        return this;
    }

    public FcmNotificationBuilder message(String message) {
        mMessage = message;
        return this;
    }



    public FcmNotificationBuilder sender(String sender) {
        Sender = sender;
        return this;
    }

    public FcmNotificationBuilder firebaseToken(String firebaseToken) {
        mFirebaseToken = firebaseToken;
        return this;
    }

    public FcmNotificationBuilder receiverFirebaseToken(String receiverFirebaseToken) {
        mReceiverFirebaseToken = receiverFirebaseToken;
        return this;
    }

    public FcmNotificationBuilder roomUid(String roomid) {
        roomUid = roomid;
        return this;
    }

    public void send() {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(AUTHORIZATION, AUTH_KEY)
                .url(FCM_URL)
                .post(requestBody)
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onGetAllUsersFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    private JSONObject getValidJsonBody() throws JSONException {
        JSONObject jsonObjectBody = new JSONObject();
        jsonObjectBody.put(KEY_TO, mReceiverFirebaseToken);

        JSONObject jsonObjectData = new JSONObject();
        jsonObjectData.put(KEY_TITLE, mTitle);
        jsonObjectData.put(KEY_TEXT, mMessage);
        jsonObjectData.put(KEY_SENDER, Sender);
        jsonObjectData.put(KEY_FCM_TOKEN, mFirebaseToken);
        jsonObjectData.put(KEY_ROOM, roomUid);
        JSONObject chat = new JSONObject();
        chat=populateJsonChat(chat);
        jsonObjectData.put(KEY_CHATS, chat);
        jsonObjectBody.put(KEY_DATA, jsonObjectData);
        return jsonObjectBody;
    }
    private JSONObject populateJsonChat ( JSONObject chat) throws JSONException
    {
        chat.put("chatId", received.getChatId());
        chat.put("receiver", received.getReceiver());
        chat.put("receiverUid",received.getReceiverUid());
        chat.put("sender", received.getSender());
        chat.put("messageType", received.getMessageType());
        chat.put("senderUid",received.getSenderUid());
        chat.put("timestamp", received.getTimestamp());
        chat.put("photoUrl", received.getPhotoUrl());
        chat.put("message",received.getMessage());
        chat.put("readStatus",received.getReadStatus());

        if(received.getFile()!=null)

        {
            chat.put("type",received.getFile().getType());
            chat.put("url_file", received.getFile().getUrl_file());
            chat.put("name_file",received.getFile().getName_file());
            chat.put("size_file",received.getFile().getSize_file());
        }

        return chat;
    }
}
