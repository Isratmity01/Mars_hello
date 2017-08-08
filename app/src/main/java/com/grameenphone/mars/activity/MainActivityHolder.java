package com.grameenphone.mars.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.grameenphone.mars.R;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.fragment.Fragment_Contacts;
import com.grameenphone.mars.fragment.Fragment_GroupChat;
import com.grameenphone.mars.fragment.Fragment_Live;
import com.grameenphone.mars.fragment.Fragment_PlaceCall;
import com.grameenphone.mars.fragment.Fragment_PrivateChat;
import com.grameenphone.mars.fragment.Fragment_RecentCalls;
import com.grameenphone.mars.fragment.Fragment_UserProfile;
import com.grameenphone.mars.fragment.MessageFragment;
import com.grameenphone.mars.gcm.SinchService;
import com.grameenphone.mars.model.CallDetails;
import com.grameenphone.mars.model.User;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivityHolder extends BaseActivity implements AHBottomNavigation.OnTabSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 180;
    private GoogleApiClient mGoogleApiClient;
    public static AHBottomNavigation bottomNavigation;
    MessageFragment messageFragment;
    DatabaseHelper databaseHelper;
    Call call;
    Boolean firstRun;
    private String mUsername;
    public static final String ANONYMOUS = "anonymous";
    User me;
    String Name, roomID, roomType;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.holder_main);
        Intent intent = getIntent();
        Name = intent.getStringExtra("room_name");
        roomID = intent.getStringExtra("room_uid");
        roomType = intent.getStringExtra("room_type");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        messageFragment = new MessageFragment();
        ActionBar ab = getSupportActionBar();

        databaseHelper = new DatabaseHelper(MainActivityHolder.this);
        me = databaseHelper.getMe();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
            //user is using app for the first time
        }

        ab.setHomeAsUpIndicator(R.drawable.logohdpi);
        ab.setDisplayHomeAsUpEnabled(true);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.myBottomNavigation_ID);
        bottomNavigation.setOnTabSelectedListener(this);
        // bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.isForceTitlesDisplay();


        this.createNavItems();
        if (Name != null && roomID != null && roomType.equals("grp")) {
            startGroupChat(roomID, Name);
        } else if (Name != null && roomID != null && roomType.equals("p2p")) {
            StartP2p(roomID, Name);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(MainActivityHolder.this)
                .enableAutoManage(MainActivityHolder.this /* FragmentActivity */, this  /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    private void createNavItems() {
        //CREATE ITEMS
        AHBottomNavigationItem crimeItem = new AHBottomNavigationItem("ম্যাসেজ", R.drawable.ic_msg);
        AHBottomNavigationItem dramaItem = new AHBottomNavigationItem("কল", R.drawable.ic_call);
        AHBottomNavigationItem docstem = new AHBottomNavigationItem("লাইভ", R.drawable.ic_live);
        AHBottomNavigationItem docstem2 = new AHBottomNavigationItem("প্রোফাইল", R.drawable.ic_more);
        //ADD THEM to bar
        bottomNavigation.addItem(crimeItem);
        bottomNavigation.addItem(dramaItem);
        bottomNavigation.addItem(docstem);
        bottomNavigation.addItem(docstem2);
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.disabled));
        //set properties
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        //set current item
        bottomNavigation.setCurrentItem(0);

    }

    public void StartP2p(String roomId, String name) {
        Bundle bundle = new Bundle();
        bundle.putString("room_uid", roomId);
        bundle.putString("room_name", name);

        Fragment_PrivateChat fragment = new Fragment_PrivateChat();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_id, fragment);

        transaction.addToBackStack("p2p");
        //    transaction.addToBackStack(null);

        transaction.commit();
    }

    public void startGroupChat(String roomId, String name) {
        Bundle bundle = new Bundle();
        bundle.putString("room_uid", roomId);
        bundle.putString("room_name", name);

        Fragment_GroupChat fragment_groupChat = new Fragment_GroupChat();
        fragment_groupChat.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_id, fragment_groupChat);
        bottomNavigation.setVisibility(View.GONE);
        transaction.addToBackStack("grp");
        //    transaction.addToBackStack(null);

        transaction.commit();
    }

    public void SignOut() {
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        mUsername = ANONYMOUS;
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finishAffinity();
        startActivity(intent);
    }

    @Override
    public void onTabSelected(int position, boolean wasSelected) {
        //show fragment
        if (position == 0) {

            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, messageFragment).addToBackStack("msg").commit();

        } else if (position == 1) {
            Fragment_RecentCalls fragment_recentCalls = new Fragment_RecentCalls();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, fragment_recentCalls).addToBackStack("call").commit();

        } else if (position == 2) {
            Fragment_Live fragment_live = new Fragment_Live();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, fragment_live).addToBackStack("live").commit();

        } else if (position == 3) {
            Fragment_UserProfile fragment_userProfile = new Fragment_UserProfile();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, fragment_userProfile).addToBackStack("prfl").commit();
        }


    }

    public void placeNewCallActivity() {
        Fragment_PlaceCall fragment = new Fragment_PlaceCall();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_id, fragment);
        transaction.addToBackStack("newCall");

        transaction.commit();
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {

            finish();
            //additional code
        } else {
          /*  getSupportActionBar().setHomeAsUpIndicator(R.drawable.logohdpi);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            getSupportFragmentManager().popBackStack();
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(count-2);
            String tag = backEntry.getName();

            if(tag=="call")
            {
                bottomNavigation.setCurrentItem(1);
            }
            if(tag=="callog")
            {
                bottomNavigation.setCurrentItem(2);
            }else if(tag=="msg")
            {
                bottomNavigation.setCurrentItem(0);
            }
           else if(tag=="p2p")
            {
                bottomNavigation.setCurrentItem(0);
            }*/

            bottomNavigation.setVisibility(View.VISIBLE);

            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, messageFragment).addToBackStack("msg").commit();

            getSupportActionBar().setHomeAsUpIndicator(R.drawable.logohdpi);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setTitle(" ");
            bottomNavigation.setCurrentItem(0);
        }
    }

    @Override
    protected void onServiceConnected() {
        String get = null;
        try {
            get = getSinchServiceInterface().getUserName();
        } catch (Exception e) {

        }

        if (TextUtils.isEmpty(get)) {

            if (me.getUid() == null) return;
            else
                getSinchServiceInterface().startClient(me.getUid());


        }

        //else userName.setText(get);
        //   mCallButton.setEnabled(true);
    }

    public void callButtonClicked(String remoteuserName, String photourl, String Userid) {
        if (remoteuserName.isEmpty()) {
            //Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        try {

            try {
                call = getSinchServiceInterface().callUser(Userid);
            } catch (Exception e) {

            }
            if (call == null) {
                //Service failed for some reason, show a Toast and abort
                Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before "
                        + "placing a call.", Toast.LENGTH_LONG).show();
                return;
            }
            String callId = call.getCallId();
            final CallDetails user = new CallDetails(remoteuserName, System.currentTimeMillis(), "outgoing", Userid, photourl);
            databaseHelper.addUserLog(user);
            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra("Photourl", photourl);
            callScreen.putExtra(SinchService.CALL_ID, callId);
            startActivity(callScreen);
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void checkPermissions() {
        List<String> permissions = new ArrayList<>();
        String message = "mars permissions:";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_CONTACTS);
            message += "\n to get contacts from phone.";
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_SETTINGS);
            message += "\n to dim display.";
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
            message += "\nfor calling funcion.";
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
            message += "\nfor calling funcion.";
            //requestReadPhoneStatePermission();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
            message += "\nfor calling funcion.";
            //requestReadPhoneStatePermission();
        }

        if (!permissions.isEmpty()) {
            // Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            String[] params = permissions.toArray(new String[permissions.size()]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
        } // else: We already have permissions, so handle as normal
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.MODIFY_AUDIO_SETTINGS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_SETTINGS, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION and WRITE_EXTERNAL_STORAGE
                Boolean recordaudio = perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
                Boolean modaudio = perms.get(Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED;
                Boolean phonestate = perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
                Boolean contactstate = perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
                Boolean writestate = perms.get(Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
                if (recordaudio && modaudio && phonestate && contactstate && writestate) {
                    // All Permissions Granted

                    return;
                    //Toast.makeText(PhoneRegActivity.this, "Thanks for permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
















