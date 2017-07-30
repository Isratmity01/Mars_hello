package com.grameenphone.mars.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grameenphone.mars.R;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.Group;
import com.grameenphone.mars.model.User;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatListActivity extends AppCompatActivity {

    private FloatingActionButton addGroup;

    private String GROUP_CHILD = "group_details";

    private DatabaseHelper dbHelper;

    private RecyclerView mFriendRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Group, GroupChatListActivity.GroupViewHolder>
            mFriendAdapter;


    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView emailTextView;
        public CircleImageView friendImageView;

        public TextView unreadcount;
        public ImageView delivery;
        public TextView time;

        public GroupViewHolder(View v) {
            super(v);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            emailTextView = (TextView) itemView.findViewById(R.id.un_read_messaage);
            friendImageView = (CircleImageView) itemView.findViewById(R.id.friendImageView);

            unreadcount = (TextView) itemView.findViewById(R.id.un_read_message_count);

            delivery = (ImageView) itemView.findViewById(R.id.delivery_status);
            time = (TextView) itemView.findViewById(R.id.time_stamp_un_read_message);


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_group_chat_list);

        dbHelper = new DatabaseHelper(getApplicationContext());


        mFriendRecyclerView = (RecyclerView) findViewById(R.id.group_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mFriendRecyclerView.setLayoutManager(mLinearLayoutManager);


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFriendAdapter = new FirebaseRecyclerAdapter<Group, GroupChatListActivity.GroupViewHolder>(
                Group.class,
                R.layout.item_friend,
                GroupChatListActivity.GroupViewHolder.class,
                mFirebaseDatabaseReference.child(GROUP_CHILD)) {

            @Override
            protected void populateViewHolder(GroupChatListActivity.GroupViewHolder viewHolder,
                                              Group group, int position) {

                Map<String, Boolean> members = new HashMap<String, Boolean>();
                members = group.getMember();

                User me = dbHelper.getMe();


                if (members.containsKey(me.getUid())) {
                    viewHolder.nameTextView.setText(group.getName());
                    viewHolder.emailTextView.setVisibility(View.GONE);


                    if (group.getGroupPic() != null) {
                        Glide.with(getApplicationContext())
                                .load(group.getGroupPic())
                                .into(viewHolder.friendImageView);
                    }

                    final Group g = group;
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getApplicationContext(), GroupChatActivity.class);
                            intent.putExtra("roomId", g.getGroupId());
                            intent.putExtra("roomName", g.getName());
                            startActivity(intent);
                            finish();

                        }
                    });


                } else {
                    viewHolder.friendImageView.setVisibility(View.GONE);
                    viewHolder.nameTextView.setVisibility(View.GONE);
                    viewHolder.emailTextView.setVisibility(View.GONE);
                }

                viewHolder.unreadcount.setVisibility(View.GONE);
                viewHolder.delivery.setVisibility(View.GONE);
                viewHolder.time.setVisibility(View.GONE);

            }

        };


        mFriendAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int userCount = mFriendAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (userCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mFriendRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mFriendRecyclerView.setLayoutManager(mLinearLayoutManager);
        mFriendRecyclerView.setAdapter(mFriendAdapter);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


}
