package com.grameenphone.mars.utility;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.grameenphone.mars.R;
import com.grameenphone.mars.activity.GroupAddActivity;

/**
 * Created by HP on 7/7/2017.
 */

public class ImageDialog extends AppCompatActivity {

    private ImageView mDialog;
    String  photoUrl;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_dialog);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
         android.support.v7.app.ActionBar ab = getSupportActionBar();
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        ab.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        photoUrl = intent.getStringExtra("photoUrl");

        mDialog = (ImageView)findViewById(R.id.your_image);

        Glide.with(ImageDialog.this)
                .load(photoUrl)
                .into(mDialog);

        //finish the activity (dismiss the image dialog) if the user clicks
        //anywhere on the image


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