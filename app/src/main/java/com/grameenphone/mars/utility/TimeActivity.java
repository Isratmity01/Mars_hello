package com.grameenphone.mars.utility;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by shadman.rahman on 05-Jun-17.
 */

public class TimeActivity {
    public static String getCurrentTime(Context context) {
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        return mydate;
    }
}
