package com.grameenphone.mars.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grameenphone.mars.ApplicationChat;
import com.grameenphone.mars.R;
import com.grameenphone.mars.adapter.RoomListAdapter;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.events.PushNotificationEvent;
import com.grameenphone.mars.model.CallEnded;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.ChatRoom;
import com.grameenphone.mars.model.ChatSent;
import com.grameenphone.mars.model.ChatSent2;
import com.grameenphone.mars.model.Group;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.navhelper.BottomNavigationViewHelper;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 180;
    User me;
    private android.support.v7.widget.SearchView mSearchView;
    private MenuItem searchMenuItem;
    SwipeRefreshLayout mSwipeRefreshLayout;
    android.support.v7.widget.SearchView.OnQueryTextListener listener;
    String ReceivedRoomId=null;
    public final int[] counts = new int[10];
    int countw=0;
    public String getReceivedRoomId() {
        return ReceivedRoomId;
    }

    public void setReceivedRoomId(String receivedRoomId) {
        ReceivedRoomId = receivedRoomId;
    }

    private static final String TAG = "MainActivity";
    public static final String USERS_CHILD = "users_chat_room";
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    Chat IsitLast=null;

    public Chat getIsitLast() {
        return IsitLast;
    }

    public void setIsitLast(Chat isitLast) {
        IsitLast = isitLast;
    }

    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    private RecyclerView mFriendRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;

    private FirebaseAuth mFirebaseAuth;
    public static FirebaseUser mFirebaseUser;

    ViewGroup.MarginLayoutParams marginLayoutParams;
    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference1;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseHelper dbHelper;
    private Toolbar toolbar;


    BottomNavigationView bottomNavigationView;
    private RoomListAdapter roomListAdapter;

    private static ArrayList<ChatRoom> chatRooms = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.logohdpi);
        ab.setDisplayHomeAsUpEnabled(true);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
            //user is using app for the first time
        }
        dbHelper = new DatabaseHelper(getApplicationContext());
        me = dbHelper.getMe();
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);


        chatRooms = populateChatRoomArraylist();


        mFriendRecyclerView = (RecyclerView) findViewById(R.id.friendListRecyclerView);
        roomListAdapter = new RoomListAdapter(MainActivity.this, chatRooms);


        mFriendRecyclerView.setAdapter(roomListAdapter);


        roomListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int userCount = roomListAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager
                        .findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (userCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mFriendRecyclerView.scrollToPosition(positionStart);
                }
            }
        });


        mLinearLayoutManager = new LinearLayoutManager(this);
        marginLayoutParams = (ViewGroup.MarginLayoutParams) mFriendRecyclerView.getLayoutParams();
        mFriendRecyclerView.setLayoutManager(mLinearLayoutManager);
        marginLayoutParams.setMargins(0, 0, 0, 110);
        mFriendRecyclerView.setLayoutParams(marginLayoutParams);


        mFirebaseDatabaseReference1 = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference1.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getUid().equals(mFirebaseUser.getUid())) {
                    dbHelper.addMe(user);
                } else {
                    dbHelper.addUser(user);
                }
                chatRooms.addAll(populateChatRoomArraylist());
                roomListAdapter.refresh();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.child(USERS_CHILD).child(mFirebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatRoom chatroom = dataSnapshot.getValue(ChatRoom.class);
                dbHelper.addRoom(chatroom.getRoomId(), chatroom.getName(), chatroom.getPhotoUrl(), chatroom.getType());

                if(!chatroom.getRoomId().equals("p2p")){

                    mFirebaseDatabaseReference
                            .child("group_details")
                            .child(chatroom.getRoomId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChildren()){
                                        Group group = dataSnapshot.getValue(Group.class);
                                        dbHelper.addGroup(group);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                }



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {


                chatRooms.clear();
                chatRooms.addAll(populateChatRoomArraylist());


                roomListAdapter.refresh();

            }
        }, 10 * 1000);


        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        //  BottomNavigationViewHelper.increaseIconSize(getApplicationContext(),bottomNavigationView);
        bottomNavigationView.setAnimation(null);

        Log.d("in main activity + ", String.valueOf(bottomNavigationView.getHeight()));
        bottomNavigationView.setSelectedItemId(R.id.action_message);
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


        listener = new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // newText is text entered by user to SearchView

                roomListAdapter.getFilter().filter(newText);
                return true;
            }
        };

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


    @Override
    public void onStart() {
        super.onStart();
EventBus.getDefault().register(this);
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

        try {
            unbindService(this);
        }catch (Exception e)
        {

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (android.support.v7.widget.SearchView) searchMenuItem.getActionView();

        View searchplate = (View) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchplate.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
        EditText searchEditText = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(getResources().getColor(R.color.disabled));
        searchEditText.setHint("লিখে খুঁজুন");
        mSearchView.setOnQueryTextListener(listener);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed

                getSupportActionBar().setHomeAsUpIndicator(R.drawable.logohdpi);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                setItemsVisibility(menu, searchMenuItem, true);
                bottomNavigationView.setVisibility(View.VISIBLE);
                marginLayoutParams.setMargins(0, 0, 0, 110);
                mFriendRecyclerView.setLayoutParams(marginLayoutParams);
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                getSupportActionBar().setHomeAsUpIndicator(null);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
                upArrow.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                setItemsVisibility(menu, searchMenuItem, false);
                bottomNavigationView.setVisibility(View.GONE);
                marginLayoutParams.setMargins(0, 0, 0, 46);
                mFriendRecyclerView.setLayoutParams(marginLayoutParams);
                return true;  // Return true to expand action view
            }
        });

        // Detect SearchView close


        return super.onCreateOptionsMenu(menu);
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.new_message:
                Intent intentmsg = new Intent(getApplicationContext(), NewMessageActivity.class);
                startActivity(intentmsg);
                return true;

            case R.id.new_group_message_menu:
                Intent intent = new Intent(getApplicationContext(), GroupAddActivity.class);
                startActivity(intent);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {

        chatRooms.clear();

        chatRooms.addAll(populateChatRoomArraylist());
        roomListAdapter.refresh();
    }
    @Subscribe
    public void onEvent(ChatSent event){
        // your implementation
      chatRooms.clear();
        chatRooms.addAll(populateChatRoomArraylist());
        roomListAdapter.refresh();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private ArrayList<ChatRoom> populateChatRoomArraylist() {
        ArrayList<ChatRoom> cRooms = dbHelper.getAllRoom();

        for (ChatRoom cr : cRooms) {

            int count=1;
            cr.setLastChat(dbHelper.getLastMsg(cr.getRoomId()));
            if(cr.getLastChat()==null)
            {
                cr.setUnreadMessageCount("0");
            }
            else {
                if(getIsitLast()!=cr.getLastChat())
                setIsitLast(cr.getLastChat());

                cr.setUnreadMessageCount(String.valueOf(count++));
            }
            //cr.setUnreadMessageCount(dbHelper.getUnreadMsgCount(cr.getRoomId(), me.getUid()));


        }

        Collections.sort(cRooms, new ChatRoomComparator());

        return cRooms;
    }


    public class ChatRoomComparator implements Comparator<ChatRoom> {
        @Override
        public int compare(ChatRoom o1, ChatRoom o2) {

            if (o1.getLastChat() != null && o2.getLastChat() != null) {

                return (o2.getLastChat().getTimestamp() + "").compareTo(o1.getLastChat().getTimestamp() + "");

            }

            if (o1.getLastChat() == null && o2.getLastChat() != null) {
                return 10;
            }

            if (o1.getLastChat() != null && o2.getLastChat() == null) {
                return -10;
            }


            return o1.getName().compareTo(o2.getName());
        }


    }


}





