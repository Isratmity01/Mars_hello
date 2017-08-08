package com.grameenphone.mars.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import com.grameenphone.mars.model.CallDetails;
import com.grameenphone.mars.utility.DateTimeUtility;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserCallAdapter extends RecyclerView.Adapter<UserCallAdapter.MyViewHolder> {

    private ArrayList<CallDetails> callDetailses;
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder  {
        public TextView title, genre;
        ImageView callicon,type;
        CircleImageView userImg;
        LinearLayout all;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.person_name);
            genre = (TextView) view.findViewById(R.id.calltype);
            callicon=(ImageView)view.findViewById(R.id.person_call);
            type=(ImageView)view.findViewById(R.id.typeicon);
            all=(LinearLayout)view.findViewById(R.id.parent);
            userImg=(CircleImageView)view.findViewById(R.id.person_photo);
            view.setTag(view);
           // view.setOnClickListener((View.OnClickListener) this);
           // year = (TextView) view.findViewById(R.id.year);
        }




    }


    public UserCallAdapter(Context context, ArrayList<CallDetails>callDetailses2)
    {
        this.mContext=context;
        this.callDetailses=callDetailses2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.caller_list_item, parent, false);

        return new MyViewHolder(itemView);
    }


    public void updateList(ArrayList<CallDetails>data) {
        callDetailses = data;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final CallDetails callDetails = callDetailses.get(position);
        holder.title.setText(callDetails.getCallingTo());
        String time= DateTimeUtility.returnTime(callDetails.getCallInTime());

        holder.genre.setText(time);

        if(callDetails.getImgUrl() != null){
            Glide.with(holder.userImg.getContext())
                    .load(callDetails.getImgUrl())
                    .into(holder.userImg);
        } else {
            holder.userImg.setBackgroundResource(R.drawable.ic_user_pic_02);
        }
        if(callDetails.getCallType().equals("outgoing"))
        {
            holder.type.setBackground(null);
            holder.type.setImageResource(R.drawable.ic_outgoing);
        }
        else if(callDetails.getCallType().equals("Missed")) {
            holder.type.setBackground(null);
            holder.type.setImageResource(R.drawable.ic_missed);
        }
       else if(callDetails.getCallType().equals("Incoming")){
            holder.type.setBackground(null);
            holder.type.setImageResource(R.drawable.ic_incoming);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivityHolder)mContext).callButtonClicked(callDetails.getCallingTo(),callDetails.getImgUrl(),
                        callDetails.getUid());
                  }
        });

    }

    @Override
    public int getItemCount() {
        return callDetailses.size();
    }
}
