package com.grameenphone.mars.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grameenphone.mars.R;
import com.grameenphone.mars.activity.LogActivity;
import com.grameenphone.mars.activity.MainActivityHolder;
import com.grameenphone.mars.activity.NewMessageActivity;
import com.grameenphone.mars.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private ArrayList<User> callDetailses;
    Context mContext;
    Boolean NewCall = true;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genre;
        ImageView callicon, type;
        LinearLayout pa;
        CircleImageView userImage;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.person_name);
            genre = (TextView) view.findViewById(R.id.calltype);
            callicon = (ImageView) view.findViewById(R.id.person_call);
            userImage = (CircleImageView) view.findViewById(R.id.person_photo);
            pa = (LinearLayout) view.findViewById(R.id.parent);
            view.setTag(view);
            // view.setOnClickListener((View.OnClickListener) this);
            // year = (TextView) view.findViewById(R.id.year);

        }


    }


    public UserAdapter(Context context, ArrayList<User> callDetailses2, Boolean value) {
        this.mContext = context;
        this.callDetailses = callDetailses2;
        this.NewCall = value;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);

        return new MyViewHolder(itemView);
    }


    public void updateList(ArrayList<User> data) {
        callDetailses = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final User user = callDetailses.get(position);
        String name = user.getName();
        holder.title.setText(name);
        String lilname = name.trim().split("\\s+")[0];
        if (!NewCall) holder.genre.setText(lilname + " কে ম্যাসেজ করুন");
        else {
            holder.genre.setText(lilname + " কে ভয়েস কল করুন");
        }

        if (user.getPhotoUrl() != null) {
            Glide.with(holder.userImage.getContext())
                    .load(user.getPhotoUrl())
                    .into(holder.userImage);
        } else {
            holder.userImage.setBackgroundResource(
                    R.drawable.ic_user_pic_02);
        }
        if (!NewCall) holder.callicon.setImageDrawable(null);
        else holder.callicon.setBackgroundResource(
                R.drawable.ic_call_icon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NewCall) {
                    ((MainActivityHolder) mContext).callButtonClicked(callDetailses.get(position).getName(),
                            callDetailses.get(position).getPhotoUrl(), callDetailses.get(position).getUid());
                    ;
                } else {
                    if (mContext.getClass().getName().contains("MainActivityHolder")) {
                        ((MainActivityHolder) mContext).StartP2p(callDetailses.get(position).getUid(), callDetailses.get(position).getName());
                    }

                    if (mContext.getClass().getName().contains("NewMessageActivity")) {
                        ((NewMessageActivity) mContext).StartP2p(callDetailses.get(position).getUid(), callDetailses.get(position).getName());
                    }



                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return callDetailses.size();
    }
}
