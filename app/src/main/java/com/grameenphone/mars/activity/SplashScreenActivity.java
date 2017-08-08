package com.grameenphone.mars.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.grameenphone.mars.R;

public class SplashScreenActivity extends Activity
{

    private static long SPLASH_MILLIS = 1200;

    private String mClassToLaunchPackage;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.activity_splash_screen, null, false);

        addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mClassToLaunchPackage = getPackageName();


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {

            @Override
            public void run()
            {

                startARActivity();

            }

        }, SPLASH_MILLIS);


    }
    private void startARActivity()
    {
        Intent i = new Intent();
        i.setClassName(mClassToLaunchPackage, "com.grameenphone.mars.activity.MainActivityHolder");
        startActivity(i);
    }
}
