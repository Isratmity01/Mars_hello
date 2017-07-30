package com.grameenphone.mars.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grameenphone.mars.R;
import com.grameenphone.mars.adapter.FriendListAdapter;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.User;

import java.util.ArrayList;

public class GroupAddActivity extends AppCompatActivity {

    private String GROUP_CHILD = "group_details";

    private DatabaseHelper dbHelper;

    RecyclerView friend;
    static MemberChipsAdapter memberAdapter;
    FriendListAdapter friendAdapter;
    public static ArrayList<String> selectedMember = new ArrayList<>();

    FloatingActionButton createGroup;




    private DatabaseReference mFirebaseDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_group_add);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();





        dbHelper = new DatabaseHelper(getApplicationContext());

        ArrayList<User> alluser = dbHelper.getAllUser();



        if(selectedMember.size() > 0){
            selectedMember.clear();
        }




        friend = (RecyclerView) findViewById(R.id.friend);
        friendAdapter = new FriendListAdapter(this, alluser);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        friend.setAdapter(friendAdapter);
        friend.setLayoutManager(llm);



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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_next:

                if(selectedMember.size() > 1) {
                    Intent groupDetailIntent = new Intent(this, NewGroupDetailsActivity.class);
                    groupDetailIntent.putStringArrayListExtra("users_uid", selectedMember);
                    startActivity(groupDetailIntent);
                } else {
                    Toast.makeText( getApplicationContext(), "please select more then 1 user", Toast.LENGTH_SHORT ).show();
                }

                return true;

            case R.id.home:
                onBackPressed();
                return true;




            default:
                return super.onOptionsItemSelected(item);
        }

    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

    }

    public static boolean addSelectedMember (String uid){
        boolean alReadySelected = false;

        for( int i=0; selectedMember.size()>i; i++){
            if( uid.equals( selectedMember.get(i) )){
                alReadySelected = true;
                selectedMember.remove(i);
            }
        }

        if(!alReadySelected) {
            selectedMember.add(uid);
        }

        return !alReadySelected;
    }










}
