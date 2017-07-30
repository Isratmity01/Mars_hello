package com.grameenphone.mars;

import android.app.Application;



/**
 * Created by HP on 5/11/2017.
 */

public class ApplicationChat extends Application {
    private static boolean sIsChatActivityOpen = false;

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        ApplicationChat.sIsChatActivityOpen = isChatActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();

     //   RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext()).build();

    }
}