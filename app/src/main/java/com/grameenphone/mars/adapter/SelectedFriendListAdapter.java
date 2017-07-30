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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.grameenphone.mars.activity.NewGroupDetailsActivity.addSelectedMember;


public class SelectedFriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<User> users;
    private LayoutInflater inflater;


    public SelectedFriendListAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(context).inflate(R.layout.item_friend_for_group, parent, false);
        final SelectedFriendViewHolder holder = new SelectedFriendViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final SelectedFriendViewHolder itemHolder = (SelectedFriendViewHolder) holder;
        final User current = users.get(position);

        itemHolder.phoneTextView.setText(current.getPhone());
        itemHolder.nameTextView.setText(current.getName());
        itemHolder.selected.setChecked(true);

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


                if(addSelectedMember(current)){
                    itemHolder.selected.setChecked(true);
                } else {

                    itemHolder.selected.setChecked(false);


                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }




    private class SelectedFriendViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView phoneTextView;
        private CircleImageView friendImageView;
        private CheckBox selected;

        private SelectedFriendViewHolder(View v) {
            super(v);
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            phoneTextView = (TextView) itemView.findViewById(R.id.phone);
            friendImageView = (CircleImageView) itemView.findViewById(R.id.profile_image);
            selected = (CheckBox) itemView.findViewById(R.id.selected);
        }
    }


}
