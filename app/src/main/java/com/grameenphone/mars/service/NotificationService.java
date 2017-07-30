package com.grameenphone.mars.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grameenphone.mars.R;
import com.grameenphone.mars.activity.ChatRoomActivity;
import com.grameenphone.mars.activity.GroupChatActivity;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.ChatRoom;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.utility.Compare;
import com.grameenphone.mars.utility.Constant;

import java.util.ArrayList;
import java.util.List;


public class NotificationService extends Service {

    private DatabaseReference mFirebaseDatabaseReference;
    private static DatabaseHelper dbHelper;
    private static ArrayList<String> rooms = new ArrayList<>();
    User me = new User();





    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Notification Service ", "-------On cereate called------");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Notification Service ", "-------On startcommand called------");
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        dbHelper = new DatabaseHelper(getApplicationContext());
        me = dbHelper.getMe();

        rooms = roomList();







        final ChildEventListener postListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                if(dataSnapshot.hasChildren()) {

                    Chat chat = dataSnapshot.getValue(Chat.class);
                    chat.setChatId(dataSnapshot.getKey());
                    Boolean showNotification = false;

                    String roomId = dataSnapshot.getRef().getParent().getKey();

                    Chat c = dbHelper.getMsg( roomId, dataSnapshot.getKey());

                    if ( c.getMessage() == null ) {



                        dbHelper.addMessage(roomId ,chat, dataSnapshot.getKey(), chat.getReadStatus());

                        if (chat.getReadStatus() == 0 && !chat.getMessageType().equals( "system" ) ) {
                            showNotification = true;
                        }


                    } else {
                        if (chat.getReadStatus() == 0 && !chat.getMessageType().equals( "system" ) ) {
                            showNotification = true;
                        } else {

                            dbHelper.addMessage(roomId ,chat, dataSnapshot.getKey(), chat.getReadStatus());

                            showNotification = false;
                        }
                    }


                    if(dbHelper.roomNotificationState( roomId ) == 0){
                        showNotification = false;
                    }



                    /*

                    ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> foregroundProcessInfo = am.getRunningAppProcesses();
                    Boolean isAppForeground = false;

                    for (ActivityManager.RunningAppProcessInfo processInfo : foregroundProcessInfo) {
                        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            for (String activeProcess : processInfo.pkgList) {
                                if (activeProcess.equals(getApplicationContext().getPackageName())) {
                                    isAppForeground = true;
                                }
                            }
                        }
                    }

                    */


                    if (!chat.getSenderUid().equals(me.getUid()) && showNotification) {




                        ChatRoom chatRoom = dbHelper.getRoom(roomId);

                        Intent intent = null;

                        if(chatRoom.getType().equals("p2p")) {

                            intent = new Intent( getApplicationContext(), ChatRoomActivity.class);

                            intent.putExtra("room_name", chatRoom.getName());
                            intent.putExtra("room_uid", roomId);

                        } else {

                            intent = new Intent( getApplicationContext(), GroupChatActivity.class);

                            intent.putExtra("room_name", chatRoom.getName());
                            intent.putExtra("room_uid", roomId);

                        }


                        PendingIntent pendingintent = PendingIntent.getActivity(getApplicationContext(), 0,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT);


                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                                .setContentTitle(chat.getSender()+" ম্যাসেজ দিয়েছেন" )
                                .setContentText(chat.getMessage())
                                .setTicker("New Message")
                                .setSmallIcon(R.drawable.nicon)
                                .setContentIntent(pendingintent);
                        if(!chatRoom.getType().equals("p2p"))
                        {
                            notificationBuilder.setContentTitle(chatRoom.getName());
                            if (chat.getMessage().matches("[0-9]+") && chat.getMessage().length() > 5) {
                                notificationBuilder.setContentText( chat.getSender() + " : স্টিকার পাঠিয়েছেন");
                            }
                            else   if (chat.getMessage().equals("Image")) {
                                notificationBuilder.setContentText(chat.getSender() + " : ছবি পাঠিয়েছেন");
                            }
                            else {
                                notificationBuilder.setContentTitle(chatRoom.getName() );
                                notificationBuilder.setContentText( chat.getSender() + " : " +chat.getMessage());

                            }
                        }
                        else {
                            if (chat.getMessage().matches("[0-9]+") && chat.getMessage().length() > 5) {
                                notificationBuilder.setContentTitle(chat.getSender());
                                notificationBuilder.setContentText("স্টিকার পাঠিয়েছেন");
                            }

                            else   if (chat.getMessage().equals("Image")) {
                                notificationBuilder.setContentTitle(chat.getSender());
                                notificationBuilder.setContentText("ছবি পাঠিয়েছেন");
                            }
                            else {
                                notificationBuilder.setContentTitle(chat.getSender()+" ম্যাসেজ দিয়েছেন" );
                                notificationBuilder.setContentText(chat.getMessage());
                            }
                        }
                        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

                        notificationBuilder.setAutoCancel(true);

                        NotificationManager notificationmanager = (NotificationManager) getApplicationContext()
                                .getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationmanager.notify(0, notificationBuilder.build());


                    }




                }



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.hasChildren()) {


                    Chat chat = dataSnapshot.getValue(Chat.class);
                    chat.setChatId(dataSnapshot.getKey());

                    String roomId = dataSnapshot.getRef().getParent().getKey();

                    dbHelper.addMessage(roomId, chat, roomId, chat.getReadStatus());




                }


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        for(int i = 0; i < rooms.size(); i++) {
            System.out.println("Notification Service "+ rooms.get(i) );
            mFirebaseDatabaseReference.child("chat_rooms").child( rooms.get(i) ).addChildEventListener(postListener);
        }




















        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("Notification Service ", "-------On destroyed called------");
    }



    private ArrayList<String> roomList(){

        ArrayList<String> allroom = new ArrayList<>();
        ArrayList<ChatRoom> friends = dbHelper.getAllRoom();



        for(int i=0; i<friends.size(); i++){
            allroom.add( friends.get(i).getRoomId() );
        }



        return allroom;

    }





}
