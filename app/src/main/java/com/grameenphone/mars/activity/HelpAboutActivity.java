package com.grameenphone.mars.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.grameenphone.mars.R;

public class HelpAboutActivity extends AppCompatActivity {

    CardView terms;
    CardView privacypolicy;
    CardView questions;
    CardView license;
    CardView legal;
    TextView version;
    String versionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_help_about);
        getSupportActionBar().setTitle("হেল্প ও অ্যাবাউট");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        ab.setHomeAsUpIndicator(upArrow);
        terms = (CardView) findViewById(R.id.terms_card);
        privacypolicy = (CardView) findViewById(R.id.privacy_policy_card);
        questions = (CardView) findViewById(R.id.question_card);
        license = (CardView) findViewById(R.id.license_card);
        legal = (CardView) findViewById(R.id.legal_card);
        version=(TextView)findViewById(R.id.version);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
       version.setText(versionCode);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(HelpAboutActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                alert.show();
            }
        });

        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(HelpAboutActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                alert.show();
            }
        });

        questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(HelpAboutActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                alert.show();
            }
        });

        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(HelpAboutActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                alert.show();
            }
        });

        legal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(HelpAboutActivity.this);
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

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
