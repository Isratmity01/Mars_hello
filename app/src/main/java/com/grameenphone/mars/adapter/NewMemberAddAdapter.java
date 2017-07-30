package com.grameenphone.mars.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grameenphone.mars.R;
import com.grameenphone.mars.activity.EditGroupActivity;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;





public class NewMemberAddAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<User> users;
    private String RoomId;
    private String RoomName;
    private LayoutInflater inflater;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseHelper dbHelper;
    private User me;


    public NewMemberAddAdapter(Context context, ArrayList<User> users, String roomid, String roomname){
        this.context = context;
        this.users = users;
        this.RoomId = roomid;
        this.RoomName = roomname;
        this.mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
        me = dbHelper.getMe();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(context).inflate(R.layout.item_friend, parent, false);
        final FriendViewHolder holder = new FriendViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final FriendViewHolder itemHolder = (FriendViewHolder) holder;
        final User current = users.get(position);

        itemHolder.emailTextView.setText(current.getPhone());
        itemHolder.nameTextView.setText(current.getName());
        itemHolder.times.setVisibility(View.GONE);
        itemHolder.delivery.setVisibility(View.GONE);
        itemHolder.unreadcount.setVisibility(View.GONE);
        if(current.getPhotoUrl() != null){
            Glide.with(context)
                    .load(current.getPhotoUrl())
                    .into(itemHolder.friendImageView);
        } else {
            itemHolder.friendImageView.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_account_circle_black_36dp));
        }


        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFirebaseDatabaseReference.child("group_details").child(RoomId).child("member").child(current.getUid()).setValue(true);

                Chat systemMessage = new Chat();
                systemMessage.setMessage(current.getName()+"\n কে অ্যাড করেছেন");
                systemMessage.setSender(me.getName());
                systemMessage.setSenderUid(me.getUid());
                systemMessage.setMessageType("system");

                long time = System.currentTimeMillis();
                systemMessage.setTimestamp(time);

                mFirebaseDatabaseReference.child("chat_rooms").child(RoomId).push().setValue(systemMessage);

                Intent intent = new Intent( context.getApplicationContext(), EditGroupActivity.class);
                intent.putExtra("room_uid", RoomId);
                intent.putExtra("room_name", RoomName);
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }




    private class FriendViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView emailTextView;
        private CircleImageView friendImageView;
        public TextView unreadcount;
        public ImageView delivery;
        public TextView times;
        private FriendViewHolder(View v) {
            super(v);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            emailTextView = (TextView) itemView.findViewById(R.id.un_read_messaage);
            friendImageView = (CircleImageView) itemView.findViewById(R.id.friendImageView);
            unreadcount = (TextView) itemView.findViewById(R.id.un_read_message_count);
            delivery = (ImageView) itemView.findViewById(R.id.delivery_status);
            times = (TextView) itemView.findViewById(R.id.time_stamp_un_read_message);
        }
    }


}
