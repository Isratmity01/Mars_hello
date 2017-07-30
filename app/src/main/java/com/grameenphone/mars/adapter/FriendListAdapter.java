package com.grameenphone.mars.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grameenphone.mars.R;
import com.grameenphone.mars.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.grameenphone.mars.activity.GroupAddActivity.addSelectedMember;


public class FriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<User> users;
    private LayoutInflater inflater;
    List<Integer> selectedlist = new ArrayList<Integer>();


    public FriendListAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(context).inflate(R.layout.item_friend_for_group, parent, false);
        final FriendViewHolder holder = new FriendViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final FriendViewHolder itemHolder = (FriendViewHolder) holder;
        final User current = users.get(position);
        itemHolder.setIsRecyclable(false);
        itemHolder.phoneTextView.setText(current.getPhone());
        itemHolder.nameTextView.setText(current.getName());

        if(current.getPhotoUrl() != null){
            Glide.with(context)
                    .load(current.getPhotoUrl())
                    .into(itemHolder.friendImageView);
        } else {
            itemHolder.friendImageView.setBackgroundResource(
                    R.drawable.ic_user_pic_02);
        }

        if(selectedlist.contains(position))
        {
            itemHolder.selected.setChecked(true);
        }
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(addSelectedMember(current.getUid())){
                    itemHolder.selected.setChecked(true);
                    selectedlist.add(position);

                } else {

                    itemHolder.selected.setChecked(false);
                    for(int i=0;i<selectedlist.size();i++)
                    {
                        if(selectedlist.get(i) ==position)
                        {
                            selectedlist.remove(i);
                        }
                    }


                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }




    private class FriendViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView phoneTextView;
        private CircleImageView friendImageView;
        private CheckBox selected;

        private FriendViewHolder(View v) {
            super(v);
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            phoneTextView = (TextView) itemView.findViewById(R.id.phone);
            friendImageView = (CircleImageView) itemView.findViewById(R.id.profile_image);
            selected = (CheckBox) itemView.findViewById(R.id.selected);
        }
    }


}
