package com.grameenphone.mars.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grameenphone.mars.R;
import com.grameenphone.mars.adapter.AddAdminAdapter;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.Group;
import com.grameenphone.mars.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditGroupActivity extends AppCompatActivity {

    private Bundle bundle = new Bundle();

    private DatabaseReference mFirebaseDatabaseReference;

    private TextView editGroupName;
    private CircleImageView groupPic;
    private CardView addMember, leaveGroup;
    private RecyclerView addAdmin;

    private ImageView leaveGroupIcon;

    private TextView leaveGroupText;

    private String roomId;
    private String roomName;

    private User me;
    private Group group;

    private DatabaseHelper dbHelper;

    private View.OnClickListener leaveListener, deleteListener;

    private AddAdminAdapter addAdminAdapter;



    private ArrayList<String> members = new ArrayList<>();
    private ArrayList<String> admins = new ArrayList<>();
    private ArrayList<User> alluser = new ArrayList<>();
    private ArrayList<Group> groups = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_edit_group);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        dbHelper = new DatabaseHelper(getApplicationContext());
        me = dbHelper.getMe();

        bundle = getIntent().getExtras();

            roomId = bundle.getString("room_uid");
            roomName = bundle.getString("roomName");
          //  MESSAGES_CHILD = b.getString("room_uid");




        getSupportActionBar().setTitle(roomName);




        editGroupName = (TextView) findViewById(R.id.group_name_edit);
        groupPic = (CircleImageView) findViewById(R.id.group_image_view);
        groupPic.setImageResource(R.drawable.hello1);
        leaveGroupIcon = (ImageView) findViewById(R.id.leave_group_icon);


        addMember = (CardView) findViewById(R.id.add_new_member);
        leaveGroup = (CardView) findViewById(R.id.leave_group);

        leaveGroupText = (TextView) findViewById(R.id.leave_group_text);


        addAdmin = (RecyclerView) findViewById(R.id.add_admin);

        addAdminAdapter = new AddAdminAdapter(this, alluser, roomId, groups);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        addAdmin.setAdapter(addAdminAdapter);
        addAdmin.setLayoutManager(llm);


        mFirebaseDatabaseReference.child("group_details").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                group = dataSnapshot.getValue(Group.class);

                boolean isAdmin = false;

                groups.add(group);

                if(group.getGroupPic()!=null){
                    Glide.with(getApplicationContext())
                            .load(group.getGroupPic())
                            .into(groupPic);
                }

                for (String key : group.getMember().keySet()) {
                    members.add(key);
                }

                for (String key : group.getAdmin().keySet()) {
                    admins.add(key);
                }


                for (String uid : members) {
                    User user = dbHelper.getUser(uid);
                    user.setAdmin(false);
                    for (String u : admins) {
                        if (u.equals(uid)) {
                            user.setAdmin(true);
                        }
                    }

                    alluser.add(user);
                }


                addAdminAdapter.notifyDataSetChanged();


                if (group.getOwner().equals(me.getUid())) {
                    leaveGroupText.setText("ডিলিট গ্রুপ");
                    leaveGroupIcon.setImageDrawable( getResources().getDrawable(R.drawable.ic_leave_group));
                    leaveGroup.setOnClickListener(deleteListener);
                } else {
                    leaveGroup.setOnClickListener(leaveListener);
                }

                editGroupName.setText(group.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddNewMemberToGroupActivity.class);

                intent.putExtra("members", members);
                intent.putExtra("room_uid", roomId);
                intent.putExtra("roomName", roomName);

                startActivity(intent);
                finish();

            }
        });


        leaveListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mFirebaseDatabaseReference.child("group_details").child(roomId).child("member").child(me.getUid()).removeValue();

                Chat systemMessage = new Chat();
                systemMessage.setMessage(me.getName() + " গ্রুপ থেকে লিভ করেছেন ");
                systemMessage.setSender(" ");
                systemMessage.setSenderUid("system");
                systemMessage.setMessageType("system");

                long time = System.currentTimeMillis();
                systemMessage.setTimestamp(time);

                mFirebaseDatabaseReference.child("chat_rooms").child(roomId).push().setValue(systemMessage);


                Intent intent = new Intent(getApplicationContext(), GroupChatListActivity.class);
                startActivity(intent);
                finish();


            }
        };

        deleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mFirebaseDatabaseReference.child("group_details").child(roomId).removeValue();
                mFirebaseDatabaseReference.child("chat_rooms").child(roomId).removeValue();
                Intent intent = new Intent(getApplicationContext(), GroupChatListActivity.class);
                startActivity(intent);
                finish();


            }
        };


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

                if(group!= null && group.getName()!= null) {
                    Intent intent = new Intent(this, GroupNameAndPhotoUpdateActivity.class);
                    intent.putExtra("name", group.getName());
                    intent.putExtra("room_uid", group.getGroupId());
                    intent.putExtra("photoUrl", group.getGroupPic());
                    startActivity(intent);
                    finish();
                }

                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }





    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), GroupChatActivity.class);
        intent.putExtra("room_uid", roomId);
        intent.putExtra("room_name", roomName);
        startActivity(intent);
        finish();
    }


}
