package com.grameenphone.mars.activity;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grameenphone.mars.R;
import com.grameenphone.mars.model.User;

import java.util.ArrayList;






public class MemberChipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<User> users;
    private LayoutInflater inflater;


    public MemberChipsAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.from(context).inflate(R.layout.contact_chip, parent, false);
        ChipViewHolder holder = new ChipViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ChipViewHolder itemHolder = (ChipViewHolder) holder;
        User current = users.get(position);


        itemHolder.nameTextView.setText(current.getName());


    }

    @Override
    public int getItemCount() {
        if(users == null ) return 0;
        return users.size();
    }




    private class ChipViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;


        private ChipViewHolder(View v) {
            super(v);
            nameTextView = (TextView) itemView.findViewById(R.id.members_name);
        }
    }


}