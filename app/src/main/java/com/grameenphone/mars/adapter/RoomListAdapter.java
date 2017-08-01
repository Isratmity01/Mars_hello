package com.grameenphone.mars.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grameenphone.mars.R;
import com.grameenphone.mars.activity.ChatRoomActivity;
import com.grameenphone.mars.activity.GroupChatActivity;
import com.grameenphone.mars.activity.MainActivity;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.ChatRoom;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.utility.DateTimeUtility;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class RoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>implements Filterable {



    private Context context;
    private ArrayList<ChatRoom> rooms=new ArrayList<ChatRoom>();
    private ArrayList<ChatRoom> allrooms=new ArrayList<ChatRoom>();
    private ArrayList<ChatRoom> filteredList=new ArrayList<ChatRoom>();
    private CustomFilter mFilter;
    private LayoutInflater inflater;
    private DatabaseHelper dbHelper;
    private User me;


    public RoomListAdapter(Context context, ArrayList<ChatRoom> rooms){
        this.context = context;
        allrooms=rooms;
        this.rooms = rooms;
        dbHelper = new DatabaseHelper(context);

        me = dbHelper.getMe();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(context).inflate(R.layout.item_friend, parent, false);
        final ChatRoomHolder holder = new ChatRoomHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ChatRoomHolder itemHolder = (ChatRoomHolder) holder;
        final ChatRoom current = rooms.get(position);


        itemHolder.nameTextView.setText(current.getName());

        Chat lastMessage = current.getLastChat();

        if(lastMessage != null) {


            String Message = lastMessage.getMessage();
            if (Message.matches("[0-9]+") && Message.length() > 5) {
                itemHolder.message.setText("স্টিকার পাঠানো হয়েছে");
            } else if (Message.equals("Image")) {
                itemHolder.message.setText("ছবি পাঠানো হয়েছে");
            } else {
                itemHolder.message.setText(lastMessage.getMessage());
            }

            itemHolder.nameTextView.setTextColor(context.getResources().getColor(R.color.seen_message_color));
            itemHolder.message.setTextColor(context.getResources().getColor(R.color.seen_message_color));
            itemHolder.timeStamp.setTextColor(context.getResources().getColor(R.color.seen_message_color));

            itemHolder.timeStamp.setText(DateTimeUtility.getFormattedTimeFromTimestamp(lastMessage.getTimestamp()));



            if(!lastMessage.getSenderUid().equals(me.getUid())){
                if(lastMessage.getReadStatus()==1)
                {
                    current.setUnreadMessageCount("0");
                    itemHolder.deliveryStatus.setVisibility(View.VISIBLE);
                    itemHolder.deliveryStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.seen_status));
                    itemHolder.unReadMessageCount.setVisibility(View.INVISIBLE);
                    itemHolder.message.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    itemHolder.nameTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
                else {
                    itemHolder.deliveryStatus.setVisibility(View.INVISIBLE);

                    if (current.getUnreadMessageCount() != null && !current.getUnreadMessageCount().equals("0")) {
                        itemHolder.unReadMessageCount.setText(current.getUnreadMessageCount());
                        itemHolder.unReadMessageCount.setVisibility(View.INVISIBLE);


                        itemHolder.message.setTextColor(context.getResources().getColor(R.color.unseen_message_color));
                        itemHolder.nameTextView.setTextColor(context.getResources().getColor(R.color.unseen_message_color));

                        itemHolder.message.setTypeface(Typeface.DEFAULT_BOLD);
                        itemHolder.nameTextView.setTypeface(Typeface.DEFAULT_BOLD);

                    } else {
                        itemHolder.unReadMessageCount.setVisibility(View.INVISIBLE);
                        itemHolder.message.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                        itemHolder.nameTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    }

                }

            } else {
                if(lastMessage.getReadStatus()==0){
                    itemHolder.deliveryStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_delivered));
                } else {
                    itemHolder.deliveryStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.seen_status));
                }

                itemHolder.deliveryStatus.setVisibility(View.VISIBLE);
                itemHolder.unReadMessageCount.setVisibility(View.INVISIBLE);
            }








        } else {


            String lilname=current.getName().trim().split("\\s+")[0];
            itemHolder.message.setText(lilname+ "-কে হ্যালো বলুন");
            itemHolder.message.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            itemHolder.nameTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            itemHolder.message.setTextColor(context.getResources().getColor(R.color.seen_message_color));
            itemHolder.timeStamp.setText("");
            itemHolder.nameTextView.setTextColor(context.getResources().getColor(R.color.seen_message_color));
            itemHolder.deliveryStatus.setVisibility(View.GONE);
            itemHolder.unReadMessageCount.setVisibility(View.GONE);

        }







        if(current.getPhotoUrl() != null){
            Glide.with(context)
                    .load(current.getPhotoUrl())
                    .into(itemHolder.roomImage);
        } else {
            itemHolder.roomImage.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.hello1));
        }


        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(current.getType().equals("p2p")) {
                    startp2pChat(current.getRoomId(), current.getName());
                } else {
                    startGroupChat(current.getRoomId(), current.getName());
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }


    @Override
    public Filter getFilter() {
        if (mFilter == null)
            mFilter = new CustomFilter();

        return mFilter;
    }

    //return the filter class object

    private class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // Create a FilterResults object
            FilterResults results = new FilterResults();

            // If the constraint (search string/pattern) is null
            // or its length is 0, i.e., its empty then
            // we just set the `values` property to the
            // original contacts list which contains all of them
            if (constraint == null || constraint.length() == 0) {
                results.values = allrooms;
                results.count = allrooms.size();
            }
            else {
                // Some search copnstraint has been passed
                // so let's filter accordingly
                ArrayList<ChatRoom> filteredContacts = new ArrayList<ChatRoom>();

                // We'll go through all the contacts and see
                // if they contain the supplied string
                for (ChatRoom c : rooms) {
                    if (c.getName().toUpperCase().contains( constraint.toString().toUpperCase() )) {
                        // if `contains` == true then add it
                        // to our filtered list
                        filteredContacts.add(c);
                    }
                }

                // Finally set the filtered values and size/count
                results.values = filteredContacts;
                results.count = filteredContacts.size();
            }

            // Return our FilterResults object
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            rooms = (ArrayList<ChatRoom>) results.values;
            notifyDataSetChanged();
        }

        public void updateList(ArrayList<ChatRoom>data) {
            rooms = data;
            notifyDataSetChanged();
        }
    }




    private class ChatRoomHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView message;
        private TextView unReadMessageCount;
        private TextView timeStamp;
        private CircleImageView roomImage;
        private ImageView deliveryStatus;

        private ChatRoomHolder(View v) {
            super(v);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            message = (TextView) itemView.findViewById(R.id.un_read_messaage);
            unReadMessageCount = (TextView) itemView.findViewById(R.id.un_read_message_count);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp_un_read_message);
            roomImage = (CircleImageView) itemView.findViewById(R.id.friendImageView);
            deliveryStatus = (ImageView) itemView.findViewById(R.id.delivery_status);
        }
    }





    private void startp2pChat(String roomId, String name){



        Intent intent = new Intent( context, ChatRoomActivity.class);

        intent.putExtra("room_name", name);
        intent.putExtra("room_uid", roomId);

        context.startActivity(intent);


    }


    private void startGroupChat(String roomId, String name){



        Intent intent = new Intent( context, GroupChatActivity.class);

        intent.putExtra("room_name", name);
        intent.putExtra("room_uid", roomId);

        context.startActivity(intent);


    }



    public void refresh(){
        //manipulate list
        notifyDataSetChanged();

    }
    public int refreshfromswipe(){
        //manipulate list
        notifyDataSetChanged();
        return 1;
    }

}
