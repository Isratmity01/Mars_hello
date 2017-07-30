package com.grameenphone.mars.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.grameenphone.mars.R;

public class PrivacySettingsActivity extends AppCompatActivity {
    Switch Contact;
    RelativeLayout blocklayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_privacy_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        ab.setHomeAsUpIndicator(upArrow);
        Contact=(Switch)findViewById(R.id.contacts_switch);
        setActionBarTitle("প্রাইভেসি সেটিংস");
        blocklayout=(RelativeLayout)findViewById(R.id.blocklist);
        Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(PrivacySettingsActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Contact.setChecked(true);
                    }
                });

                alert.show();
            }
        });
        blocklayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(PrivacySettingsActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
