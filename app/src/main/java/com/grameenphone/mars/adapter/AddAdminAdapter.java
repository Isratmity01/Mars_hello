package com.grameenphone.mars.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grameenphone.mars.R;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.Group;
import com.grameenphone.mars.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddAdminAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<User> users;
    private String RoomId;
    private String RoomName;
    private String owner;
    private ArrayList<Group> group;
    private LayoutInflater inflater;
    private DatabaseReference mFirebaseDatabaseReference;

    private DatabaseHelper dbHelper;
    private User me;

    public AddAdminAdapter(Context context, ArrayList<User> users, String roomid, ArrayList<Group> group) {
        this.context = context;
        this.users = users;
        this.RoomId = roomid;
        this.group = group;
        this.mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
        me = dbHelper.getMe();


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(context).inflate(R.layout.item_member, parent, false);
        final FriendViewHolder holder = new FriendViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final FriendViewHolder itemHolder = (FriendViewHolder) holder;
        final User current = users.get(position);

        itemHolder.emailTextView.setText("01XXXYYYZZZ");

        if (me.getUid().equals(current.getUid())) {
            itemHolder.nameTextView.setText("আপনি");
            me.setAdmin(current.isAdmin());
        } else {
            itemHolder.nameTextView.setText(current.getName());
        }

        owner = group.get(0).getOwner();

        if (current.isAdmin()) {
            itemHolder.adminSeal.setVisibility(View.VISIBLE);

        }
        if (current.isAdmin() && me.getUid().equals(current.getUid())) {

            itemHolder.menuViewOption.setVisibility(View.VISIBLE);
        }


        if (current.getPhotoUrl() != null) {
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


                PopupMenu popup = new PopupMenu(context, itemHolder.menuViewOption);
                popup.inflate(R.menu.make_admin_remove_member);


                final Menu popupMenu = popup.getMenu();
                if (current.getUid().equals(owner)) {
                    popupMenu.findItem(R.id.make_admin_menu).setEnabled(false);
                    popupMenu.findItem(R.id.remove_admin_menu).setEnabled(false);
                    popupMenu.findItem(R.id.remove_member_menu).setEnabled(false);
                } else if (current.isAdmin()) {
                    popupMenu.findItem(R.id.make_admin_menu).setEnabled(false);
                } else {
                    popupMenu.findItem(R.id.remove_admin_menu).setEnabled(false);
                }


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.make_admin_menu:

                                mFirebaseDatabaseReference.child("group_details").child(RoomId).child("admin").child(current.getUid()).setValue(true);

                                Chat systemMessage = new Chat();
                                systemMessage.setMessage(current.getName() + "\n কে অ্যাডমিন বানিয়েছেন ");
                                systemMessage.setSender(me.getName());
                                systemMessage.setSenderUid(me.getUid());
                                systemMessage.setMessageType("system");

                                long time = System.currentTimeMillis();
                                systemMessage.setTimestamp(time);

                                mFirebaseDatabaseReference.child("chat_rooms").child(RoomId).push().setValue(systemMessage);

                                itemHolder.adminSeal.setVisibility(View.VISIBLE);
                                users.get(position).setAdmin(true);


                                break;
                            case R.id.remove_member_menu:

                                mFirebaseDatabaseReference.child("group_details").child(RoomId).child("member").child(current.getUid()).removeValue();
                                mFirebaseDatabaseReference.child("group_details").child(RoomId).child("admin").child(current.getUid()).removeValue();
                                Chat systemMessageRemove = new Chat();
                                systemMessageRemove.setMessage(current.getName() + "\n কে রিমুভ করেছেন ");
                                systemMessageRemove.setSender(me.getName());
                                systemMessageRemove.setSenderUid(me.getUid());
                                systemMessageRemove.setMessageType("system");


                                long timeRemove = System.currentTimeMillis();
                                systemMessageRemove.setTimestamp(timeRemove);

                                mFirebaseDatabaseReference.child("chat_rooms").child(RoomId).push().setValue(systemMessageRemove);

                                users.remove(current);
                                notifyDataSetChanged();

                                break;

                            case R.id.remove_admin_menu:

                                mFirebaseDatabaseReference.child("group_details").child(RoomId).child("admin").child(current.getUid()).removeValue();
                                Chat systemMessageRemoveAdmin = new Chat();
                                systemMessageRemoveAdmin.setMessage(current.getName() + "\n কে অ্যাডমিন থেকে রিমুভ করেছেন ");
                                systemMessageRemoveAdmin.setSender(me.getName());
                                systemMessageRemoveAdmin.setSenderUid(me.getUid());
                                systemMessageRemoveAdmin.setMessageType("system");

                                long timeRemoveAdmin = System.currentTimeMillis();
                                systemMessageRemoveAdmin.setTimestamp(timeRemoveAdmin);

                                mFirebaseDatabaseReference.child("chat_rooms").child(RoomId).push().setValue(systemMessageRemoveAdmin);

                                itemHolder.adminSeal.setVisibility(View.INVISIBLE);
                                users.get(position).setAdmin(false);

                                break;

                        }
                        return false;
                    }
                });


                if (me.isAdmin()) {
                    popup.show();
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
        private TextView emailTextView;
        private CircleImageView friendImageView;

        private ImageView adminSeal;

        private ImageView menuViewOption;

        private FriendViewHolder(View v) {
            super(v);
            nameTextView = (TextView) itemView.findViewById(R.id.memberNameTextView);
            emailTextView = (TextView) itemView.findViewById(R.id.memberEmailTextView);
            friendImageView = (CircleImageView) itemView.findViewById(R.id.memberImageView);
            menuViewOption = (ImageView) itemView.findViewById(R.id.menuViewOptions);
            adminSeal = (ImageView) itemView.findViewById(R.id.admin_seal);
        }
    }


}
