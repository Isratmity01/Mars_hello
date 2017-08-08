package com.grameenphone.mars.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.grameenphone.mars.R;
import com.grameenphone.mars.activity.GroupAddActivity;
import com.grameenphone.mars.activity.NewMessageActivity;
import com.grameenphone.mars.activity.SignInActivity;
import com.grameenphone.mars.adapter.RoomListAdapter;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.events.PushNotificationEvent;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.ChatRoom;
import com.grameenphone.mars.model.ChatSent;
import com.grameenphone.mars.model.Group;
import com.grameenphone.mars.model.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by Oclemmy on 5/10/2016 for ProgrammingWizards Channel and http://www.Camposha.com.
 * Fragment shown when crim navigation item is clicked.
 */
public class MessageFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
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
    boolean doubleBackToExitPressedOnce = false;
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
    private SwipeRefreshLayout swipeContainer;
    private FirebaseAuth mFirebaseAuth;
    public static FirebaseUser mFirebaseUser;
    private boolean firstRun;
    ViewGroup.MarginLayoutParams marginLayoutParams;
    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference1;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseHelper dbHelper;
    private Toolbar toolbar;
    SharedPreferences sharedPreferences;
    View fragmentView;
    BottomNavigationView bottomNavigationView;
    private RoomListAdapter roomListAdapter;
    EventBus myEventBus;
    private static ArrayList<ChatRoom> chatRooms = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        myEventBus = EventBus.getDefault();
        EventBus.getDefault().register(this);




    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentView == null){


            fragmentView = inflater.inflate(R.layout.fragment_message,
                    container, false);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle( "Messages");
            bindViews(fragmentView);
             sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());


        }


        return fragmentView;
    }
    private void bindViews(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          //  checkPermissions();
            //user is using app for the first time
        }
        dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        me = dbHelper.getMe();
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(getActivity().getApplicationContext(), SignInActivity.class));
            getActivity().finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        mFriendRecyclerView = (RecyclerView) view.findViewById(R.id.friendListRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        marginLayoutParams=(ViewGroup.MarginLayoutParams)swipeContainer.getLayoutParams();
        mFriendRecyclerView.setLayoutManager(mLinearLayoutManager);
        marginLayoutParams.setMargins(0, 0, 0, 50);
        swipeContainer.setLayoutParams(marginLayoutParams);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {

                // Your code to refresh the list here.

                // Make sure you call swipeContainer.setRefreshing(false)

                // once the network request has completed successfully.

                chatRooms.clear();
                roomListAdapter.refresh();
                callfirebasefunction();


            }

        });
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.logohdpi);
        init();
    }
    public void init()
    {

        firstRun= sharedPreferences.getBoolean("fRun", false);
        chatRooms = populateChatRoomArraylist();


        roomListAdapter = new RoomListAdapter(getActivity(), chatRooms,dbHelper,me);


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
        mFirebaseDatabaseReference1 = FirebaseDatabase.getInstance().getReference();


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

        if(firstRun==false)
        {
            Toast.makeText(getActivity(),"ডাটা লোড হচ্ছে, দয়া করে অপেক্ষা করুন",Toast.LENGTH_LONG).show();
            callfirebasefunction();
            sharedPreferences.edit().putBoolean("fRun", true).apply();
        }

    }
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (android.support.v7.widget.SearchView) searchMenuItem.getActionView();

        View searchplate = (View) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchplate.setBackgroundColor(Color.TRANSPARENT);
       // searchplate.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
        EditText searchEditText = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(getResources().getColor(R.color.disabled));
        searchEditText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        searchEditText.setHint("লিখে খুঁজুন");
        mSearchView.setOnQueryTextListener(listener);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed

                ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.logohdpi);
               ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                setItemsVisibility(menu, searchMenuItem, true);
                getActivity().findViewById(R.id.myBottomNavigation_ID).setVisibility(View.VISIBLE);

                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);
                ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                final Drawable upArrow = ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.abc_ic_ab_back_material);
                upArrow.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.icons), PorterDuff.Mode.SRC_ATOP);
                ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);
                setItemsVisibility(menu, searchMenuItem, false);
                getActivity().findViewById(R.id.myBottomNavigation_ID).setVisibility(View.GONE);
                return true;  // Return true to expand action view
            }
        });

        // Detect SearchView close

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.new_message:
                Intent intentmsg = new Intent(getActivity().getApplicationContext(), NewMessageActivity.class);
                startActivity(intentmsg);
                return true;

            case R.id.new_group_message_menu:
                Intent intent = new Intent(getActivity().getApplicationContext(), GroupAddActivity.class);
                startActivity(intent);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
    private void callfirebasefunction()
    {
        mFirebaseDatabaseReference1.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getUid().equals(mFirebaseUser.getUid())) {
                    dbHelper.addMe(user);
                } else {
                    dbHelper.addUser(user);
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
        mFirebaseDatabaseReference.child(USERS_CHILD).child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("We're done loading the initial "+dataSnapshot.getChildrenCount()+" items");
                Toast.makeText(getActivity().getApplicationContext(),"লোড হয়েছে ",Toast.LENGTH_SHORT).show();
                chatRooms.addAll(populateChatRoomArraylist());
                roomListAdapter.refresh();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

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
}
