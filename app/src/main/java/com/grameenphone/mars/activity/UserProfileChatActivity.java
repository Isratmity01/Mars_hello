package com.grameenphone.mars.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.grameenphone.mars.R;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.navhelper.BottomNavigationViewHelper;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class UserProfileChatActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener{

    private CardView notificationCardView;
    private CardView privacyCardView;
    private CardView helpAboutCardView,signoutCard;
    public static final String ANONYMOUS = "anonymous";
    private FirebaseAuth mFirebaseAuth;
    public static FirebaseUser mFirebaseUser;
    private ImageView usersPhoto;
    private TextView userName;
    private TextView usersPhoneNumber;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseHelper dbHelper;
    private  TextView language,lanen,lanbn,sharedphotos,groupchatwithuser,block,deletechat;
    private User me;
    private Switch Lanswitch,notificationswitch;
    private String mroomId;
    private Toolbar toolbar;
    private ImageView firsticon,secondicon,thirdicon,fourthicon,fifthicon;
    ImageButton ProfileEdit;
String rname,rphone,rphotourl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_user_profile_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbar);



        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar ();
        final Drawable upArrow = ContextCompat.getDrawable(UserProfileChatActivity.this,R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(UserProfileChatActivity.this,R.color.icons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        actionbar.setDisplayHomeAsUpEnabled ( true );

        Bundle b = getIntent().getExtras();

        rname = b.getString("receiver_name");
        rphone = b.getString("receiver_phone");
        rphotourl = b.getString("receiver_photo_url");
        mroomId=b.getString("room_uid");
        notificationCardView = (CardView) findViewById(R.id.notification_card);
        firsticon=(ImageView)findViewById(R.id.lang_icon);
        firsticon.setImageResource(R.drawable.ic_notifications_black_24dp);

        secondicon=(ImageView)findViewById(R.id.notification_ico);
        secondicon.setImageResource(R.drawable.ic_fill_424);

        thirdicon=(ImageView)findViewById(R.id.privacy_icon);
        thirdicon.setImageResource(R.drawable.ic_fill_115);

        fourthicon=(ImageView)findViewById(R.id.help_icon);
        fourthicon.setImageResource(R.drawable.ic_fill_145);

        fifthicon=(ImageView)findViewById(R.id.signout_icon);
        fifthicon.setImageResource(R.drawable.ic_fill_delete);

        privacyCardView = (CardView) findViewById(R.id.privacy_card);
        helpAboutCardView = (CardView) findViewById(R.id.help_about_card);
        signoutCard = (CardView) findViewById(R.id.signout_card);
        usersPhoto = (ImageView) findViewById(R.id.profile_picture);
        userName = (TextView) findViewById(R.id.profile_name);
        usersPhoneNumber = (TextView) findViewById(R.id.profile_phone_number);
        Lanswitch=(Switch)findViewById(R.id.lang_switcher);
        notificationswitch=(Switch)findViewById(R.id.sw);
        Lanswitch.setVisibility(View.GONE);
        notificationswitch.setVisibility(View.VISIBLE);
        language=(TextView)findViewById(R.id.lable_language);
        lanen=(TextView)findViewById(R.id.label_eng);
        lanbn=(TextView)findViewById(R.id.lable_ban);
        lanbn.setTextSize(0);
        lanbn.setText("");
        lanbn.setVisibility(View.INVISIBLE);

        lanen.setVisibility(View.GONE);
        language.setText("নোটিফিকেশন");
        sharedphotos=(TextView)findViewById(R.id.lable_notification);
        groupchatwithuser=(TextView)findViewById(R.id.lable_privacy);
        groupchatwithuser.setText(rname+"-এর সাথে গ্রুপ চ্যাট");
        sharedphotos.setText("শেয়ার করা ছবি");
        block=(TextView)findViewById(R.id.lable_help_about);
        deletechat=(TextView)findViewById(R.id.signout_label);
        deletechat.setText("ডিলিট চ্যাট");
        block.setText("ব্লক "+rname);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        dbHelper = new DatabaseHelper(getApplicationContext());




            userName.setText(rname);

             usersPhoneNumber.setText(rphone);
                Glide.with(UserProfileChatActivity.this)
                        .load(rphotourl)
                        .into(usersPhoto);





        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, UserProfileChatActivity.this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        notificationswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(UserProfileChatActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        notificationswitch.setChecked(true);
                    }
                });

                alert.show();
            }
        });

        notificationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(UserProfileChatActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                alert.show();
            }
        });

        privacyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(UserProfileChatActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                alert.show();
            }
        });

        helpAboutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(UserProfileChatActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                alert.show();
            }
        });
        signoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(UserProfileChatActivity.this);
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;




            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(" ", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {

        finish();
    }
}
