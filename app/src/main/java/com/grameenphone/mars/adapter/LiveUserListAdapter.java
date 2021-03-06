package com.grameenphone.mars.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grameenphone.mars.R;
import com.grameenphone.mars.activity.MainActivityHolder;
import com.grameenphone.mars.activity.MarsLiveActivity;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.ChatRoom;
import com.grameenphone.mars.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.grameenphone.mars.activity.GroupAddActivity.addSelectedMember;


public class LiveUserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ChatRoom> users;
    private LayoutInflater inflater;
DatabaseHelper databaseHelper;

    public LiveUserListAdapter(Context context, ArrayList<ChatRoom> users, DatabaseHelper db){
        this.context = context;
        this.users = users;
        databaseHelper=db;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(context).inflate(R.layout.item_friend_for_live, parent, false);
        final FriendViewHolder holder = new FriendViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final FriendViewHolder itemHolder = (FriendViewHolder) holder;
        final ChatRoom current = users.get(position);

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
                ((MainActivityHolder)context).StartP2p(current.getRoomId(), current.getName());

            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }




    private class FriendViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView friendImageView;


        private FriendViewHolder(View v) {
            super(v);

            friendImageView = (CircleImageView) itemView.findViewById(R.id.profile_image_live);

        }
    }


}
