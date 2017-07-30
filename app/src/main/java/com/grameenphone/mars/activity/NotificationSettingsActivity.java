package com.grameenphone.mars.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.grameenphone.mars.R;

public class NotificationSettingsActivity extends AppCompatActivity {
    Switch notificationswitch,soundswitch,vibrationswitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_notification_settings);
        notificationswitch=(Switch)findViewById(R.id.notification_switch);
        soundswitch=(Switch)findViewById(R.id.sound_switch);
        vibrationswitch=(Switch)findViewById(R.id.vibration_switch);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        ab.setHomeAsUpIndicator(upArrow);
        setActionBarTitle("নোটিফিকেশন সেটিংস");
        notificationswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(NotificationSettingsActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        notificationswitch.setChecked(false);
                    }
                });

                alert.show();
            }
        });
        soundswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(NotificationSettingsActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        soundswitch.setChecked(false);
                    }
                });

                alert.show();
            }
        });
        vibrationswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(NotificationSettingsActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        vibrationswitch.setChecked(false);
                    }
                });

                alert.show();
            }
        });
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
