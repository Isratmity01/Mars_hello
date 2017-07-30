package com.grameenphone.mars.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grameenphone.mars.R;
import com.grameenphone.mars.adapter.UserAdapter;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.model.Usersecond;
import com.grameenphone.mars.utility.Compare;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


public class NewMessageActivity extends AppCompatActivity {

    private String GROUP_CHILD = "group_details";

    private DatabaseHelper dbHelper;

    RecyclerView friend;
    static MemberChipsAdapter memberAdapter;
    UserAdapter userAdapter;
    public static ArrayList<String> selectedMember = new ArrayList<>();

    FloatingActionButton createGroup;
    EventBus myEventBus;



    private DatabaseReference mFirebaseDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.fragment_placecall);
        myEventBus = EventBus.getDefault();
        EventBus.getDefault().register(this);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();





        dbHelper = new DatabaseHelper(getApplicationContext());

        ArrayList<User> alluser = dbHelper.getAllUser();



        if(selectedMember.size() > 0){
            selectedMember.clear();
        }
        friend = (RecyclerView) findViewById(R.id.allusers);

        try {

            userAdapter=new UserAdapter(NewMessageActivity.this,alluser,false);
            friend.setAdapter(userAdapter);
            friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }catch (Exception e)
        {

        }


       // friendAdapter = new FriendListAdapter(this, alluser);
       // LinearLayoutManager llm = new LinearLayoutManager(this);
       // friend.setAdapter(friendAdapter);
       // friend.setLayoutManager(llm);



        /*
        createGroup = (FloatingActionButton) findViewById(R.id.create_group);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if (selectedMember.size()>1) {
                    User me = dbHelper.getMe();
                    long time = System.currentTimeMillis();

                    Map<String, Boolean> admin = new HashMap<String, Boolean>();
                    admin.put( me.getUid(),true);

                    Map<String, Boolean> members = new HashMap<String, Boolean>();
                    members.put( me.getUid(),true);

                    for( int i=0; selectedMember.size()>i; i++){
                        members.put( selectedMember.get(i).getUid(),true );
                    }







                    Intent intent = new Intent( getApplicationContext(), AddaLiveActivity.class);

                    startActivity(intent);
                    finish();


                } else {

                    if( selectedMember.size()<2 ){
                        Toast.makeText(getApplicationContext(),"Please Select more than 1 member", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        */





    }
    @Subscribe
    public void onEvent(Usersecond event){
        // your implementation

        startp2pChat(event.getName(),event.getUid());
    }

    public void startp2pChat(String name,String receiverUid){


           User me= dbHelper.getMe();
       String UID= Compare.getRoomName(me.getUid(),receiverUid);
        Intent intent = new Intent(NewMessageActivity.this, ChatRoomActivity.class);

        intent.putExtra("room_name", name);
        intent.putExtra("room_uid", UID);

        startActivity(intent);
        finish();

    }




    @Override
    public void onBackPressed() {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

    }











}
