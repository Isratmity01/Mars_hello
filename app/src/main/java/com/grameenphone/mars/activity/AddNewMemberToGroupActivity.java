package com.grameenphone.mars.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grameenphone.mars.R;
import com.grameenphone.mars.adapter.NewMemberAddAdapter;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.User;

import java.util.ArrayList;

public class AddNewMemberToGroupActivity extends AppCompatActivity {

    private Bundle bundle = new Bundle();
    private DatabaseReference mFirebaseDatabaseReference;

    RecyclerView addMember;
    NewMemberAddAdapter addMemberAdapter;

    private String roomId;
    private String roomName;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_add_new_member_to_group);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        bundle = getIntent().getExtras();

        ArrayList<String> members = bundle.getStringArrayList("members");
        roomId = bundle.getString("room_uid");
        roomName = bundle.getString("roomName");

        dbHelper = new DatabaseHelper(getApplicationContext());
        ArrayList<User> alluser =  dbHelper.getAllUser();

        for(String uid: members){
            int sz = alluser.size();
            for( int i=0; i< sz; i++){
                User user = alluser.get(i);
                if(user.getUid().equals(uid)){
                    alluser.remove(user);
                    break;
                }
            }
        }

        addMember = (RecyclerView) findViewById(R.id.add_new_member);
        addMemberAdapter = new NewMemberAddAdapter(this, alluser,roomId,roomName);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        addMember.setAdapter(addMemberAdapter);
        addMember.setLayoutManager(llm);




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),EditGroupActivity.class);
        intent.putExtra("room_uid", roomId);
        intent.putExtra("roomName", roomName);
        startActivity(intent);
        finish();
    }
}
