package com.grameenphone.mars.gcm;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


public class StickyService extends Service implements ServiceConnection {

    public StickyService() {
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
        getApplicationContext().bindService(new Intent(StickyService.this, SinchService.class),  this,
                BIND_AUTO_CREATE);
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("Notification Service ", "-------On destroyed called------");
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
