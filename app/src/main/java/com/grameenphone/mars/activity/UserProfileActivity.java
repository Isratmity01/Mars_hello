package com.grameenphone.mars.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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

public class UserProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private CardView notificationCardView;
    private CardView privacyCardView;
    private CardView helpAboutCardView, signoutCard;
    public static final String ANONYMOUS = "anonymous";
    private FirebaseAuth mFirebaseAuth;
    public static FirebaseUser mFirebaseUser;
    private ImageView usersPhoto;
    private TextView userName;
    private TextView usersPhoneNumber;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseHelper dbHelper;
    private User me;
    private Switch Lanswitch;
    private String mUsername;
    private Toolbar toolbar;
    ImageButton ProfileEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_user_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.logohdpi);
        ab.setDisplayHomeAsUpEnabled(true);

        notificationCardView = (CardView) findViewById(R.id.notification_card);
        privacyCardView = (CardView) findViewById(R.id.privacy_card);
        helpAboutCardView = (CardView) findViewById(R.id.help_about_card);
        signoutCard = (CardView) findViewById(R.id.signout_card);
        usersPhoto = (ImageView) findViewById(R.id.profile_picture);
        userName = (TextView) findViewById(R.id.profile_name);
        usersPhoneNumber = (TextView) findViewById(R.id.profile_phone_number);
        Lanswitch = (Switch) findViewById(R.id.lang_switcher);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        dbHelper = new DatabaseHelper(getApplicationContext());
        me = dbHelper.getMe();

        if (me != null) {

            if (me.getName() != null) userName.setText(me.getName());

            if (me.getPhone() != null) usersPhoneNumber.setText(me.getPhone());

            if (me.getPhotoUrl() != null) {
                RequestOptions options = new RequestOptions();
                options.transform(new BlurTransformation(UserProfileActivity.this));
                Glide.with(UserProfileActivity.this)
                        .load(me.getPhotoUrl())
                        .into(usersPhoto);


            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, UserProfileActivity.this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        Lanswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(UserProfileActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Lanswitch.setChecked(true);
                    }
                });

                alert.show();
            }
        });

        notificationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotificationSettingsActivity.class);
                startActivity(intent);
            }
        });

        privacyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PrivacySettingsActivity.class);
                startActivity(intent);
            }
        });

        helpAboutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HelpAboutActivity.class);
                startActivity(intent);
            }
        });
        signoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finishAffinity();
                startActivity(intent);
            }
        });

        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation_settings);
        bottomNavigationView.setVisibility(View.VISIBLE);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        // BottomNavigationViewHelper.increaseIconSize(getApplicationContext(),bottomNavigationView);
        bottomNavigationView.setAnimation(null);
        bottomNavigationView.setSelectedItemId(R.id.action_settings);
        Log.d("in settings activity + ", String.valueOf(bottomNavigationView.getHeight()));
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_message:
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.action_call:
                                Intent callintent = new Intent(getApplicationContext(), LogActivity.class);
                                startActivity(callintent);
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.action_live:
                                Intent intentAddaLive = new Intent(getApplicationContext(), MarsLiveActivity.class);
                                startActivity(intentAddaLive);
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.action_settings:
                                Intent intentSettings = new Intent(getApplicationContext(), UserProfileActivity.class);
                                startActivity(intentSettings);
                                overridePendingTransition(0, 0);
                                break;
                        }
                        return false;
                    }
                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_profile:


                Intent intent = new Intent(this, ProfileUpdateActivity.class);
                intent.putExtra("name", me.getName());
                intent.putExtra("photoUrl", me.getPhotoUrl());
                startActivity(intent);
                finish();

                return true;

            case R.id.home:
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
}
