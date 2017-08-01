package com.grameenphone.mars.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grameenphone.mars.ApplicationChat;
import com.grameenphone.mars.R;
import com.grameenphone.mars.activity.ChatRoomActivity;
import com.grameenphone.mars.activity.GroupChatActivity;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.events.PushNotificationEvent;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.ChatSent;
import com.grameenphone.mars.model.ChatSent2;
import com.grameenphone.mars.model.FileModel;
import com.grameenphone.mars.utility.Constant;
import com.sinch.gson.Gson;
import com.sinch.gson.JsonElement;
import com.sinch.gson.JsonObject;
import com.sinch.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private DatabaseHelper databaseHelper;
    private static final String TAG = "MyFirebaseMsgService";
    EventBus eventBus;
    String ReceivedRoomId;

    public String getReceivedRoomId() {
        return ReceivedRoomId;
    }

    public void setReceivedRoomId(String receivedRoomId) {
        ReceivedRoomId = receivedRoomId;
    }
    int count=1;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        eventBus= EventBus.getDefault();
        databaseHelper = new DatabaseHelper(getApplicationContext());
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String chats = remoteMessage.getData().get("whatposted");
            if (chats == null) {

            } else {
                String title = remoteMessage.getData().get("title");
                String message = remoteMessage.getData().get("text");
                String sender = remoteMessage.getData().get("sender");
                String fcmToken = remoteMessage.getData().get("fcm_token");
                String roomid = remoteMessage.getData().get("room_uid");
                databaseHelper.addUnreadCount(roomid,0);
                Gson gson = new Gson();
                Chat staff = gson.fromJson(chats, Chat.class);
                if(title!=sender)
                {
                    if (staff.getType() == null) {

                        Chat receivedchat = new Chat(staff.getSender(), staff.getReceiver(),
                                staff.getSenderUid(), staff.getReceiverUid(),
                                staff.getMessage(),
                                staff.getTimestamp(), staff.getMessageType());

                        databaseHelper.addMessage(roomid ,receivedchat, receivedchat.getChatId(), receivedchat.getReadStatus());


                    } else {
                        FileModel fileModel = new FileModel(staff.getType(), staff.getUrl_file(), staff.getName_file(), staff.getSize_file());
                        Chat receivedchat2 = new Chat(staff.getSender(), staff.getReceiver(),
                                staff.getSenderUid(), staff.getReceiverUid(), staff.getPhotoUrl(), "Image", staff.getTimestamp()
                                , fileModel, staff.getType());
                        databaseHelper.addMessage(roomid ,receivedchat2, receivedchat2.getChatId(), receivedchat2.getReadStatus());


                    }
                }
            else {
                    if (staff.getType() == null) {
                        Chat receivedchat = new Chat(staff.getSender(), staff.getReceiver(),
                                staff.getSenderUid(), staff.getReceiverUid(),
                                staff.getMessage(),
                                staff.getTimestamp(), staff.getMessageType());
                        databaseHelper.addMessage(receivedchat, staff.getChatId());

                    } else {
                        FileModel fileModel = new FileModel(staff.getType(), staff.getUrl_file(), staff.getName_file(), staff.getSize_file());
                        Chat receivedchat2 = new Chat(staff.getSender(), staff.getReceiver(),
                                staff.getSenderUid(), staff.getReceiverUid(), staff.getPhotoUrl(), "Image", staff.getTimestamp()
                                , fileModel, staff.getType());
                        databaseHelper.addMessage(receivedchat2, staff.getChatId());

                    }
                }


                if (!ApplicationChat.isChatActivityOpen()) {

                    sendNotification(title,
                            message,
                            sender,
                            fcmToken,
                            roomid);
                } else {
                    EventBus.getDefault().post(new PushNotificationEvent(title,
                            message,
                            sender,
                            fcmToken,
                            roomid));
                }
               /* sendNotification(title,
                        message,
                        sender,
                        fcmToken,
                        roomid);*/

            }
        }
            /*
            // Don't show notification if chat activity is open.
            if ( ChatRoomActivity.isChatActivityOpen() && ChatRoomActivity.getChatRoomName().equals(title)) {

                EventBus.getDefault().post(new PushNotificationEvent(title,
                        message,
                        sender,
                        fcmToken,
                        roomid));

            } else {

                sendNotification(title,
                        message,
                        sender,
                        fcmToken,
                        roomid);

            }
            */

        }


    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String title,
                                  String message,
                                  String sender,
                                  String firebaseToken,
                                  String roomuid) {


        Intent intent;
        EventBus.getDefault().post(new PushNotificationEvent(title,
                message,
                sender,
                firebaseToken,
                roomuid));
        if(!title.equals(sender)){
            message = sender + " : " + message;
            intent = new Intent(this, GroupChatActivity.class);
        } else {
            intent = new Intent(this, ChatRoomActivity.class);
        }


        intent.putExtra(Constant.Notification.ARG_FIREBASE_TOKEN, firebaseToken);



        intent.putExtra("room_uid",roomuid);
        intent.putExtra("room_name",title);





      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.nicon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


}