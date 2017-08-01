package com.grameenphone.mars.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grameenphone.mars.model.CallDetails;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.ChatRoom;
import com.grameenphone.mars.model.Group;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.model.UserCalls;
import com.grameenphone.mars.utility.Compare;
import com.grameenphone.mars.utility.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseHelper extends SQLiteOpenHelper {



    public static final String DB_NAME = "mars";
    private static final String TAG = "DatabaseHelper";

    public static String DB_PATH;
    private SQLiteDatabase database;
    private Context context;





    public DatabaseHelper(Context context){


        super(context, DB_NAME, null, 1);
        this.context = context;

        DB_PATH = context.getFilesDir().getPath() + "/databases/";
        this.database = openDatabase();

    }





    public SQLiteDatabase openDatabase(){
        if(database==null){
            createDatabase();
            Log.e(getClass().getName(), "Database created...");
        }

        return database;
    }


    private void createDatabase(){
        boolean dbExists = checkDB();
        if(!dbExists){
            this.getReadableDatabase();
            database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            createTable();
            Log.e(getClass().getName(),"No Database");
        }

    }




    public SQLiteDatabase getDatabase(){
        String path = DB_PATH + DB_NAME;
        database = SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READWRITE);
        return database;
    }







    public void createTable() {
        String CREATE_CHAT_ROOMS = "CREATE TABLE IF NOT EXISTS " + Constant.Database.TABLE_CHAT_ROOMS + "("
                + Constant.Database.Chatroom.INDEX + " VARCHAR PRIMARY KEY UNIQUE,"
                + Constant.Database.Chatroom.CHATROOM + " TEXT,"
                + Constant.Database.Chatroom.SENDER + " TEXT,"
                + Constant.Database.Chatroom.RECEIVER + " TEXT,"
                + Constant.Database.Chatroom.SENDER_UID + " TEXT,"
                + Constant.Database.Chatroom.RECEIVER_UID + " TEXT,"
                + Constant.Database.Chatroom.MSG + " TEXT,"
                + Constant.Database.Chatroom.MSG_TYPE + " TEXT,"
                + Constant.Database.Chatroom.TIMESTAMP + " TEXT,"
                + Constant.Database.Chatroom.READ_STATUS + " INTEGER DEFAULT 0"
                + ")";


        String CREATE_USER = "CREATE TABLE IF NOT EXISTS " + Constant.Database.TABLE_USER + "("
                + Constant.Database.User.UID + " VARCHAR PRIMARY KEY UNIQUE,"
                + Constant.Database.User.NAME + " TEXT,"
                + Constant.Database.User.EMAIL + " TEXT,"
                + Constant.Database.User.PHOTO_URL + " TEXT,"
                + Constant.Database.User.IS_ME + " INTEGER DEFAULT 0,"
                + Constant.Database.User.FIREBASE_TOKEN + " TEXT"
                + ")";




        String CREATE_ROOM_LIST = "CREATE TABLE IF NOT EXISTS " + Constant.Database.TABLE_ROOM_LIST + "("
                + Constant.Database.RoomDetail.ROOM_ID + " VARCHAR PRIMARY KEY UNIQUE,"
                + Constant.Database.RoomDetail.NAME + " TEXT,"
                + Constant.Database.RoomDetail.PHOTO_URL + " TEXT,"
                + Constant.Database.RoomDetail.TYPE + " TEXT,"
                + Constant.Database.RoomDetail.NOTIFICATION_ON_OFF + " INTEGER DEFAULT 1"
                + ")";


        String CREATE_GROUP_DETAIL = "CREATE TABLE IF NOT EXISTS " + Constant.Database.TABLE_GROUP_DETAILS + "("
                + Constant.Database.Group.GROUP_ID + " VARCHAR PRIMARY KEY UNIQUE,"
                + Constant.Database.Group.NAME + " TEXT,"
                + Constant.Database.Group.PHOTO_URL + " TEXT,"
                + Constant.Database.Group.OWNER + " TEXT,"
                + Constant.Database.Group.ADMIN + " TEXT,"
                + Constant.Database.Group.MEMBER + " TEXT"
                + ")";


        String CREATE_USERCALLDETAILS = "CREATE TABLE IF NOT EXISTS " + Constant.Database.TABLE_CALLLOG + "("
                + Constant.Database.CallLog.CALLTO + " VARCHAR ,"
                + Constant.Database.CallLog.INITIATEDAT + " INTEGER ,"
                + Constant.Database.CallLog.CALLTYPE + " VARCHAR ,"
                + Constant.Database.CallLog.UID + " VARCHAR ,"
                + Constant.Database.CallLog.URL + " VARCHAR "
                + ")";

        String CREATE_CHATROOM_COUNT = "CREATE TABLE IF NOT EXISTS " + Constant.Database.TABLE_CHATROOM_LOG + "("
                + Constant.Database.RoomLogCount.ROOM_ID + " VARCHAR ,"
                + Constant.Database.RoomLogCount.UNREAD + " INTEGER "

                + ")";

        try {
            database.execSQL( CREATE_CHAT_ROOMS );
            database.execSQL( CREATE_USER );
            database.execSQL( CREATE_USERCALLDETAILS );
            database.execSQL( CREATE_ROOM_LIST );
            database.execSQL( CREATE_GROUP_DETAIL );
            database.execSQL( CREATE_CHATROOM_COUNT );
            database.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "Error Creating Table");
        }
    }








    public void addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Database.User.UID, user.getUid());
        values.put(Constant.Database.User.NAME, user.getName());
        values.put(Constant.Database.User.EMAIL, user.getPhone());
        values.put(Constant.Database.User.PHOTO_URL, user.getPhotoUrl());
        values.put(Constant.Database.User.IS_ME, 0);
        values.put(Constant.Database.User.FIREBASE_TOKEN, user.getFirebaseToken());


        db.insertWithOnConflict(Constant.Database.TABLE_USER, Constant.Database.User.UID , values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void addMe(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Database.User.UID, user.getUid());
        values.put(Constant.Database.User.NAME, user.getName());
        values.put(Constant.Database.User.EMAIL, user.getPhone());
        values.put(Constant.Database.User.PHOTO_URL, user.getPhotoUrl());
        values.put(Constant.Database.User.IS_ME, 1);
        values.put(Constant.Database.User.FIREBASE_TOKEN, user.getFirebaseToken());

        db.insertWithOnConflict(Constant.Database.TABLE_USER, Constant.Database.User.UID , values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }






    public void addMessage(String roomid, Chat chat, String idx, Integer status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Database.Chatroom.INDEX, idx);
        values.put(Constant.Database.Chatroom.CHATROOM, roomid);
        values.put(Constant.Database.Chatroom.SENDER, chat.getSender());
        values.put(Constant.Database.Chatroom.SENDER_UID, chat.getSenderUid());
        values.put(Constant.Database.Chatroom.RECEIVER, chat.getReceiver());
        values.put(Constant.Database.Chatroom.RECEIVER_UID, chat.getReceiverUid());
        values.put(Constant.Database.Chatroom.MSG, chat.getMessage());
        values.put(Constant.Database.Chatroom.READ_STATUS, status);
        values.put(Constant.Database.Chatroom.TIMESTAMP, chat.getTimestamp());
        values.put(Constant.Database.Chatroom.MSG_TYPE, chat.getMessageType());


        db.insertWithOnConflict(Constant.Database.TABLE_CHAT_ROOMS, Constant.Database.Chatroom.INDEX , values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }



    public void addMessage(Chat chat, String idx){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Database.Chatroom.INDEX, idx);
        values.put(Constant.Database.Chatroom.CHATROOM, Compare.getRoomName(chat.getSenderUid(), chat.getReceiverUid()));
        values.put(Constant.Database.Chatroom.SENDER, chat.getSender());
        values.put(Constant.Database.Chatroom.SENDER_UID, chat.getSenderUid());
        values.put(Constant.Database.Chatroom.RECEIVER, chat.getReceiver());
        values.put(Constant.Database.Chatroom.RECEIVER_UID, chat.getReceiverUid());
        values.put(Constant.Database.Chatroom.MSG, chat.getMessage());
        values.put(Constant.Database.Chatroom.READ_STATUS, chat.getReadStatus());
        values.put(Constant.Database.Chatroom.TIMESTAMP, chat.getTimestamp());
        values.put(Constant.Database.Chatroom.MSG_TYPE, chat.getMessageType());


        db.insertWithOnConflict(Constant.Database.TABLE_CHAT_ROOMS, Constant.Database.Chatroom.INDEX , values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void addUnreadCount(String RoomID, int count){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constant.Database.RoomLogCount.ROOM_ID, RoomID);
        values.put(Constant.Database.RoomLogCount.UNREAD, count);


        db.insertOrThrow(Constant.Database.TABLE_CHATROOM_LOG, Constant.Database.RoomLogCount.ROOM_ID , values);
        db.close();
    }








    public User getMe(){

        User user = new User();



        String selectAll = "SELECT * FROM "+ Constant.Database.TABLE_USER + " WHERE "+ Constant.Database.User.IS_ME + " = 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);




        try {
            if (cursor.moveToFirst()) {
                do {

                    user.setUid(cursor.getString(0));
                    user.setName(cursor.getString(1));
                    user.setPhone(cursor.getString(2));
                    user.setPhotoUrl(cursor.getString(3));
                    user.setFirebaseToken(cursor.getString(5));

                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }



        return user;

    }







    public User getUser( String uid){

        User user = new User();

        String selection = Constant.Database.User.UID+" = ? ";
        String[] selectionArgs = new String[] {uid};


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_USER, null,  selection, selectionArgs, null,  null, null, null);




        try {
            if (cursor.moveToFirst()) {
                do {

                    user.setUid(cursor.getString(0));
                    user.setName(cursor.getString(1));
                    user.setPhone(cursor.getString(2));
                    user.setPhotoUrl(cursor.getString(3));
                    user.setFirebaseToken(cursor.getString(5));

                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }



        return user;

    }













    public ArrayList<User> getAllUser(){

        ArrayList<User> alluser = new ArrayList<>();



        String selectAll = "SELECT * FROM "+ Constant.Database.TABLE_USER + " WHERE "+ Constant.Database.User.IS_ME + " = 0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);




        try {
            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setUid(cursor.getString(0));
                    user.setName(cursor.getString(1));
                    user.setPhone(cursor.getString(2));
                    user.setPhotoUrl(cursor.getString(3));
                    user.setFirebaseToken(cursor.getString(5));

                    alluser.add(user);

                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }



        return alluser;

    }


    public void updateMsg(String chatRoomID, String chatId, Integer status ){



        String where = Constant.Database.Chatroom.CHATROOM+" = ? AND " + Constant.Database.Chatroom.INDEX+ " = ? ";
        String[] whereArgs = new String[] {chatRoomID,chatId};

        ContentValues values = new ContentValues();
        values.put(Constant.Database.Chatroom.READ_STATUS, status);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(Constant.Database.TABLE_CHAT_ROOMS, values, where, whereArgs);

        db.close();


    }


    public Chat getMsg(String chatRoomID, String senderUid, String timeStamp ){

        Chat chat = new Chat();

        String selection = Constant.Database.Chatroom.CHATROOM+" = ? AND " + Constant.Database.Chatroom.SENDER_UID+ " = ? AND " + Constant.Database.Chatroom.TIMESTAMP+ " = ? ";
        String[] selectionArgs = new String[] {chatRoomID,senderUid,timeStamp};



        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_CHAT_ROOMS, null,  selection, selectionArgs, null,  null, null, null);


        try{
            if(cursor.moveToFirst()){
                do{
                    chat = new Chat();
                    chat.setChatId(cursor.getString(0));
                    chat.setSender(cursor.getString(2));
                    chat.setReceiver(cursor.getString(3));
                    chat.setSenderUid(cursor.getString(4));
                    chat.setReceiverUid(cursor.getString(5));
                    chat.setMessage(cursor.getString(6));
                    chat.setTimestamp(Long.parseLong(cursor.getString(7)));
                    chat.setReadStatus(cursor.getInt(8));

                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(TAG,"No Chat found");
        }finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }






        return chat;
    }











    public ArrayList<Chat> getAllMsg(String senderUid, String receiverUid){
        String chatRoomID = Compare.getRoomName(senderUid,receiverUid);
        ArrayList<Chat> allChat = new ArrayList<>();


        String selection = Constant.Database.Chatroom.CHATROOM + " = ? ";
        String[] selectionArgs = new String[] {chatRoomID};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_CHAT_ROOMS, null,  selection, selectionArgs, null,  null, null, null);





        try {
            if (cursor.moveToFirst()) {
                do {
                    Chat chat = new Chat();

                    /*

                    Log.d(TAG, "Sender name : " + cursor.getString(3));
                    Log.d(TAG, "receiver name : " + cursor.getString(2));
                    Log.d(TAG, "Sender uid : " + cursor.getString(5));
                    Log.d(TAG, "receiver uid : " + cursor.getString(4));
                    Log.d(TAG, "message : " + cursor.getString(6));
                    Log.d(TAG, "time : " + cursor.getString(8));
                    Log.d(TAG, "readstatus : " + cursor.getString(9));
                    Log.d(TAG, "type : " + cursor.getString(7));

                    */

                    chat.setChatId(cursor.getString(0));
                    chat.setSender(cursor.getString(2));
                    chat.setReceiver(cursor.getString(3));
                    chat.setSenderUid(cursor.getString(4));
                    chat.setReceiverUid(cursor.getString(5));
                    chat.setMessage(cursor.getString(6));
                    chat.setTimestamp(Long.parseLong(cursor.getString(8)));
                    chat.setReadStatus(Integer.parseInt(cursor.getString(9)) );
                    chat.setMessageType(cursor.getString(7));

                    allChat.add(chat);

                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }





        return allChat;
    }




    public Chat getLastMsg(String chatRoomID){
        Chat chat = new Chat();
        String selection = Constant.Database.Chatroom.CHATROOM + " = ? ";
        String[] selectionArgs = new String[] {chatRoomID};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_CHAT_ROOMS, null,  selection, selectionArgs, null,  null, null, null);

        if(cursor.getCount()==0){
            chat = null;
        }

        try {
            if (cursor.moveToLast()) {
                do {



                    chat.setChatId(cursor.getString(0));
                    chat.setSender(cursor.getString(2));
                    chat.setReceiver(cursor.getString(3));
                    chat.setSenderUid(cursor.getString(4));
                    chat.setReceiverUid(cursor.getString(5));
                    chat.setMessage(cursor.getString(6));
                    chat.setTimestamp(Long.parseLong(cursor.getString(8)));
                    chat.setReadStatus(Integer.parseInt(cursor.getString(9)) );

                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return chat;
    }



    public Chat getMsg(String chatRoomID, String idx){
        Chat chat = new Chat();

        String selection = Constant.Database.Chatroom.CHATROOM + " = ? AND " + Constant.Database.Chatroom.INDEX + " = ?" ;
        String[] selectionArgs = {chatRoomID,idx};
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query( Constant.Database.TABLE_CHAT_ROOMS, null,  selection, selectionArgs, null,  null, null, null);


        try{
            if(cursor.moveToFirst()){
                do{
                    chat = new Chat();
                    chat.setChatId(cursor.getString(0));
                    chat.setSender(cursor.getString(2));
                    chat.setReceiver(cursor.getString(3));
                    chat.setSenderUid(cursor.getString(4));
                    chat.setReceiverUid(cursor.getString(5));
                    chat.setMessage(cursor.getString(6));
                    chat.setTimestamp(Long.parseLong(cursor.getString(8)));
                    chat.setReadStatus(cursor.getInt(9));

                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(TAG,"No Chat found");
        }finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }




        return chat;
    }










    public String getUnreadMsgCount(String chatRoomID, String senderUid){

        String selection = Constant.Database.Chatroom.CHATROOM + " = ? AND "
                +Constant.Database.Chatroom.SENDER_UID + " != ? AND "
                +Constant.Database.Chatroom.MSG_TYPE + " != ? AND "
                +Constant.Database.Chatroom.READ_STATUS+" = ? ";

        String[] selectionArgs = new String[] {chatRoomID,senderUid, "system","0"};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_CHAT_ROOMS, null,  selection, selectionArgs, null,  null, null, null);

        int count = 0;

        try{
            count = cursor.getCount();
        }catch (Exception e){
            Log.d(TAG,"No Chat found");
        }finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }


        return count+"";
    }








    public void addUserLog(CallDetails callDetails){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Database.CallLog.CALLTO, callDetails.getCallingTo());
        values.put(Constant.Database.CallLog.INITIATEDAT, callDetails.getCallInTime());
        values.put(Constant.Database.CallLog.CALLTYPE, callDetails.getCallType());
        values.put(Constant.Database.CallLog.UID, callDetails.getUid());
        values.put(Constant.Database.CallLog.URL, callDetails.getImgUrl());


        db.insertWithOnConflict(Constant.Database.TABLE_CALLLOG, Constant.Database.CallLog.CALLTO , values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }












    public ArrayList<CallDetails> getAllUserLog(){

        ArrayList<CallDetails> alluser = new ArrayList<>();
        String selectAll = "SELECT userlog.rid,userlog.initiatedat,userlog.calltype,userlog.userid, "
               + "users.photoUrl " +" FROM userlog JOIN users ON "+" userlog.userid=users.uid ORDER BY userlog.initiatedat DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    CallDetails user = new CallDetails();
                    user.setCallingTo(cursor.getString(0));
                    user.setCallInTime(cursor.getLong(1));
                    user.setCallType(cursor.getString(2));
                    user.setUid(cursor.getString(3));
                    user.setImgUrl(cursor.getString(4));
                    alluser.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return alluser;

    }








    public void addRoom(String roomId, String name, String photoUrl, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Database.RoomDetail.ROOM_ID, roomId);
        values.put(Constant.Database.RoomDetail.NAME, name);
        values.put(Constant.Database.RoomDetail.PHOTO_URL, photoUrl);
        values.put(Constant.Database.RoomDetail.TYPE, type);

        db.insertWithOnConflict(Constant.Database.TABLE_ROOM_LIST, Constant.Database.RoomDetail.ROOM_ID , values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }


    public void updateNotificationStateOfRoom(String chatRoomID, int status){



        String where = Constant.Database.RoomDetail.ROOM_ID+" = ? ";
        String[] whereArgs = new String[] {chatRoomID};

        ContentValues values = new ContentValues();
        values.put(Constant.Database.RoomDetail.NOTIFICATION_ON_OFF, status);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(Constant.Database.TABLE_ROOM_LIST, values, where, whereArgs);

        db.close();

    }




    public int roomNotificationState( String chatRoomID ){

        String selection = Constant.Database.RoomDetail.ROOM_ID+" = ? ";
        String[] selectionArgs = new String[] {chatRoomID};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_ROOM_LIST, null,  selection, selectionArgs, null,  null, null, null);

        int state = 1;

        try{

            if(cursor.moveToFirst()){
                state = cursor.getInt(4);
            }



        } catch (Exception e){
            Log.d(TAG, "chatroom nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }




        return state;
    }


    public ArrayList<ChatRoom> getAllRoom(){

        ArrayList<ChatRoom> allChatrooms = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_ROOM_LIST, null,  null, null, null,  null, null, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String roomid = cursor.getString(0);
                    String name = cursor.getString(1);
                    String photourl = cursor.getString(2);
                    String type = cursor.getString(3);


                    /*

                    Log.d(TAG, "name : " + cursor.getString(1));
                    Log.d(TAG, "room id : " + cursor.getString(0));
                    Log.d(TAG, "photo : " + cursor.getString(2));
                    Log.d(TAG, "type : " + cursor.getString(3));

                    */

                    ChatRoom c = new ChatRoom(roomid,name,photourl,type);
                    allChatrooms.add(c);



                } while ( cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "chatroom nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }

        return allChatrooms;

    }




    public ChatRoom getRoom(String chatRoomID){

        ChatRoom chatRoom = new ChatRoom();

        String selection = Constant.Database.RoomDetail.ROOM_ID+" = ? ";
        String[] selectionArgs = new String[] {chatRoomID};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_ROOM_LIST, null,  selection, selectionArgs, null,  null, null, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String roomid = cursor.getString(0);
                    String name = cursor.getString(1);
                    String photourl = cursor.getString(2);
                    String type = cursor.getString(3);


                    /*

                    Log.d(TAG, "name : " + cursor.getString(1));
                    Log.d(TAG, "room id : " + cursor.getString(0));
                    Log.d(TAG, "photo : " + cursor.getString(2));
                    Log.d(TAG, "type : " + cursor.getString(3));

                    */

                    chatRoom = new ChatRoom(roomid,name,photourl,type);




                } while ( cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "chatroom nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }

        return chatRoom;

    }



    public void addGroup(Group group){

        String admin = "";
        for (String k : group.getAdmin().keySet() ){
            admin = admin + "," + k;
        }


        String member = "";
        for (String k : group.getMember().keySet() ){
            member = member + "," + k;
        }



        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constant.Database.Group.GROUP_ID, group.getGroupId());
        values.put(Constant.Database.Group.NAME, group.getName());
        values.put(Constant.Database.Group.PHOTO_URL, group.getGroupPic());
        values.put(Constant.Database.Group.OWNER, group.getOwner());
        values.put(Constant.Database.Group.ADMIN, admin);
        values.put(Constant.Database.Group.MEMBER, member);

        db.insertWithOnConflict(Constant.Database.TABLE_GROUP_DETAILS, Constant.Database.Group.GROUP_ID , values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }


    public Group getGroup(String groupID){

        Group group = new Group();

        String selection = Constant.Database.Group.GROUP_ID+" = ? ";
        String[] selectionArgs = new String[] {groupID};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_GROUP_DETAILS, null,  selection, selectionArgs, null,  null, null, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String groupid = cursor.getString(0);
                    String name = cursor.getString(1);
                    String photourl = cursor.getString(2);
                    String owner = cursor.getString(3);

                    String admin = cursor.getString(4);
                    String member = cursor.getString(5);


                    Map<String,Boolean> admins = new HashMap<>();
                    Map<String,Boolean> members = new HashMap<>();

                    for(String i : admin.split(",")){
                        admins.put(i,true);
                    }


                    for(String i : member.split(",")){
                        members.put(i,true);
                    }



                    /*

                    Log.d(TAG, "name : " + cursor.getString(1));
                    Log.d(TAG, "group id : " + cursor.getString(0));
                    Log.d(TAG, "photo : " + cursor.getString(2));
                    Log.d(TAG, "owner : " + cursor.getString(3));

                    */

                    group = new Group(groupid,name,owner,admins,members,photourl);




                } while ( cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "chatroom nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }

        return group;

    }















    private boolean checkDB(){
        String path = DB_PATH + DB_NAME;
        File file = new File(path);
        if(file.exists()){
            return true;
        }
        return false;
    }




    public synchronized void close(){
        if(this.database != null){
            this.database.close();
        }
    }








    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Constant.Database.TABLE_CHAT_ROOMS);
        onCreate(db);
    }


}
