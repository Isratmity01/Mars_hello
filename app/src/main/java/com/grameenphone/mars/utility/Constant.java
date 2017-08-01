package com.grameenphone.mars.utility;


public class Constant {
    public static class Database {
        public static final String DATABASE_NAME = "mars-e7047";
        public static final int DATABASE_VERSION = 1;
        public static final String TABLE_USER = "users";
        public static final String TABLE_CHAT_ROOMS = "chat_rooms";
        public static final String TABLE_CALLLOG = "userlog";
        public static final String TABLE_ROOM_LIST = "room_list";
        public static final String TABLE_CHATROOM_LOG = "room_unreadcount";
        public static final String TABLE_GROUP_DETAILS = "group_details";


        public static class User {
            public static final String UID = "uid";
            public static final String EMAIL = "email";
            public static final String NAME = "name";
            public static final String PHOTO_URL = "photoUrl";
            public static final String IS_ME = "isMe";
            public static final String FIREBASE_TOKEN = "firebaseToken";
        }

        public static class Chatroom {
            public static String INDEX = "idx";
            public static String CHATROOM = "chatroom";
            public static String SENDER = "sender";
            public static String RECEIVER = "receiver";
            public static String SENDER_UID = "senderUid";
            public static String RECEIVER_UID = "receiverUid";
            public static String MSG = "message";
            public static String MSG_TYPE = "messageType";
            public static String TIMESTAMP = "string";
            public static String PHOTO_URL = "photoUrl";
            public static String READ_STATUS = "readStatus";
        }


        public static class CallLog {
            public static String CALLTO = "rid";
            public static String INITIATEDAT = "initiatedat";
            public static String CALLTYPE = "calltype";
            public static String UID = "userid";
            public static String URL = "userimg";
        }


        public static class Group {
            public static final String GROUP_ID = "groupId";
            public static final String OWNER = "owner";
            public static final String ADMIN = "admin";
            public static final String NAME = "name";
            public static final String MEMBER = "member";
            public static final String PHOTO_URL = "photoUrl";
        }

        public static class RoomDetail {
            public static final String ROOM_ID = "groupId";
            public static final String PHOTO_URL = "photoUrl";
            public static final String NAME = "name";
            public static final String TYPE = "type";
            public static final String NOTIFICATION_ON_OFF = "notification";
        }
        public static class RoomLogCount {
            public static final String ROOM_ID = "roomId";
            public static final String UNREAD = "unreadcount";
        }


    }

    public static class Storage {
        public static final String STORAGE_URL = "gs://mars-e7047.appspot.com";
        public static final String ATTACHMENT = "attachments";
    }


    public static class Notification {
        public static final String ARG_USERS = "users";
        public static final String ARG_RECEIVER = "receiver";
        public static final String ARG_RECEIVER_UID = "receiver_uid";
        public static final String ARG_CHAT_ROOMS = "chat_rooms";
        public static final String ARG_FIREBASE_TOKEN = "firebaseToken";
        public static final String ARG_FRIENDS = "friends";
        public static final String ARG_UID = "uid";
    }


}