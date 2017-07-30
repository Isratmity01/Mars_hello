package com.grameenphone.mars.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grameenphone.mars.R;
import com.grameenphone.mars.adapter.chatroomviewholder.ReceiverImageHolder;
import com.grameenphone.mars.adapter.chatroomviewholder.ReceiverMessageHolder;
import com.grameenphone.mars.adapter.chatroomviewholder.ReceiverStickerHolder;
import com.grameenphone.mars.adapter.chatroomviewholder.SenderImageHolder;
import com.grameenphone.mars.adapter.chatroomviewholder.SenderMessageHolder;
import com.grameenphone.mars.adapter.chatroomviewholder.SenderStickerHolder;
import com.grameenphone.mars.adapter.chatroomviewholder.SystemMessageHolder;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.utility.Compare;
import com.grameenphone.mars.utility.DateTimeUtility;
import com.grameenphone.mars.utility.ImageDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Chat> chats;
    private User sender;
    private Context context;


    private DatabaseHelper dbHelper;


    private String drawable;
    private DatabaseReference mFirebaseDatabaseReference;
    public static String CHAT_ROOMS_CHILD = "chat_rooms";
    public static String MESSAGES_CHILD = "";


    private final int SENDER_IMG = 10;
    private final int RECEIVER_IMG = 12;
    private final int SENDER_MSG = 3;
    private final int RECEIVER_MSG = 4;
    private final int SYSTEM_MSG = 5;
    private final int SENDER_DOC = 6;
    private final int RECEIVER_DOC = 7;
    private final int SENDER_STICKER = 8;
    private final int RECEIVER_STICKER = 9;

    public GroupChatRoomAdapter(Context context, ArrayList<Chat> chats, User sender, String roomId){
        this.context = context;
        this.chats = chats;
        this.sender = sender;

        MESSAGES_CHILD = roomId;


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        dbHelper = new DatabaseHelper(context);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType){
            case RECEIVER_MSG:
                View v1 = layoutInflater.inflate(R.layout.item_receiver_message, viewGroup, false);
                viewHolder = new ReceiverMessageHolder(v1);
                break;
            case SENDER_MSG:
                View v3 = layoutInflater.inflate(R.layout.item_sender_message, viewGroup, false);
                viewHolder = new SenderMessageHolder(v3);
                break;
            case RECEIVER_STICKER:
                View v8 = layoutInflater.inflate(R.layout.item_receiver_sticker, viewGroup, false);
                viewHolder = new ReceiverStickerHolder(v8);
                break;
            case SENDER_STICKER:
                View v9 = layoutInflater.inflate(R.layout.item_sender_sticker, viewGroup, false);
                viewHolder = new SenderStickerHolder(v9);
                break;
            case RECEIVER_IMG:
                View v100 = layoutInflater.inflate(R.layout.item_receiver_img, viewGroup, false);
                viewHolder = new ReceiverImageHolder(v100);
                break;
            case SENDER_IMG:
                View v101 = layoutInflater.inflate(R.layout.item_sender_img, viewGroup, false);
                viewHolder = new SenderImageHolder(v101);
                break;
            default:
                View v5 = layoutInflater.inflate(R.layout.item_system_message, viewGroup, false);
                viewHolder = new SystemMessageHolder(v5);
                break;
        }
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case RECEIVER_MSG:
                ReceiverMessageHolder receiverMessageHolder = (ReceiverMessageHolder) holder;
                configureReceiverMessageHolder(receiverMessageHolder, position);
                break;
            case SENDER_MSG:
                SenderMessageHolder senderMessageHolder = (SenderMessageHolder) holder;
                configureSenderMessageHolder(senderMessageHolder, position);
                break;
            case SENDER_IMG:
                SenderImageHolder senderImageHolder = (SenderImageHolder) holder;
                configureSenderImageHolder(senderImageHolder, position);
                break;
            case RECEIVER_IMG:
                ReceiverImageHolder receiverImageHolder = (ReceiverImageHolder) holder;
                configureReceiverImageHolder(receiverImageHolder, position);
                break;
            case RECEIVER_STICKER:
                ReceiverStickerHolder receiverStickerHolder = (ReceiverStickerHolder) holder;
                configureReceiverStickerHolder(receiverStickerHolder, position);
                break;
            case SENDER_STICKER:
                SenderStickerHolder senderStickerHolder = (SenderStickerHolder) holder;
                configureSenderStickerHolder(senderStickerHolder, position);
                break;
            default:
                SystemMessageHolder systemMessageHolder = (SystemMessageHolder) holder;
                configureSystemMessageHolder(systemMessageHolder, position);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position){
        if(chats.get(position).getMessageType().equals("txt")){
            if (chats.get(position).getMessage() != null && !chats.get(position).getSenderUid().equals(sender.getUid())){
                return RECEIVER_MSG;
            }
            else return SENDER_MSG;
        }
        else if(chats.get(position).getMessageType().equals("stk")){
            if (chats.get(position).getMessage() != null && !chats.get(position).getSenderUid().equals(sender.getUid())){
                return RECEIVER_STICKER;
            }
            else return SENDER_STICKER;
        }
        else if(chats.get(position).getMessageType().equals("img"))
        {
            if( !chats.get(position).getSenderUid().equals(sender.getUid()) ){
                return RECEIVER_IMG;
            }
            else return SENDER_IMG;
        }

        else return SYSTEM_MSG;

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configureSenderImageHolder(final SenderImageHolder senderImageHolder, final int position){

        if(sender != null){
            //    int path= Integer.parseInt(chats.get(position).getFile().getUrl_file());
            if (chats.get(position).getFile().getUrl_file() == null) {
                senderImageHolder.imageFileView.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ic_happy));
            } else {
                Glide.with(context)
                        .load(chats.get(position).getFile().getUrl_file())
                        .into(senderImageHolder.imageFileView);

            }

            //  Glide.with(context).load(chats.get(position).getFile().getUrl_file()).into(senderImageHolder.Img);
            senderImageHolder.getTimestamp()
                    .setText( DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    chats.get(position).getTimestamp()
                            )
                    );
            if (chats.get(position).getReadStatus() == 0) {
                senderImageHolder.deliveryStatus.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ic_delivered));
            } else {
                senderImageHolder.deliveryStatus.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.seen_status));

            }
        }
        senderImageHolder.imageFileView.setOnClickListener(new View.OnClickListener() {
            //Start new list activity

            public void onClick(View v) {
                drawable=chats.get(position).getFile().getUrl_file();
                Intent intent= new Intent(context, ImageDialog.class);
                intent.putExtra("photoUrl", drawable);
                v.getContext().startActivity(intent);

            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configureReceiverMessageHolder(ReceiverMessageHolder receiverMessageHolder, int position){
        Chat receiversChat = chats.get(position);
        if(receiversChat != null){
            if(receiversChat.getPhotoUrl() == null){
                receiverMessageHolder.getCircleImageView()
                        .setImageDrawable(context.getDrawable(R.drawable.ic_account_circle_black_36dp));
            }else{
                Glide.with(context).load(receiversChat.getPhotoUrl()).into(receiverMessageHolder.circleImageView);
            }
            receiverMessageHolder.getReceiversMessage()
                    .setText(
                            receiversChat.getMessage()
                    );

            receiverMessageHolder.getTimestamp()
                    .setText( DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    receiversChat.getTimestamp()
                            )
                    );

            if(receiversChat.getReadStatus() == 0){


                Chat data = chats.get(position);


                data.setReadStatus(1);
                dbHelper.addMessage(MESSAGES_CHILD, data, data.getChatId(), 1);




                Map<String, Object> readstatus = new HashMap<>();
                readstatus.put("readStatus",1);

                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD)
                        .child(MESSAGES_CHILD)
                        .child(data.getChatId())
                        .updateChildren(readstatus);
            }



        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configureReceiverImageHolder(ReceiverImageHolder receiverImageHolder, final int position){
        Chat receiversChat = chats.get(position);
        if(receiversChat != null){
            if(receiversChat.getPhotoUrl() == null){
                receiverImageHolder.getCircleImageView()
                        .setImageDrawable(context.getDrawable(R.drawable.ic_account_circle_black_36dp));
            }else{
                Glide.with(context)
                        .load(receiversChat
                                .getPhotoUrl()).into(receiverImageHolder.circleImageView);


            }
            if (chats.get(position).getFile().getUrl_file() == null) {
                receiverImageHolder.imageFileView.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ic_happy));
            } else {
                Glide.with(context)
                        .load(chats.get(position).getFile().getUrl_file())

                        .into(receiverImageHolder.imageFileView);

            }

            receiverImageHolder.getTimestamp()
                    .setText( DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    chats.get(position).getTimestamp()
                            )
                    );
            receiverImageHolder.imageFileView.setOnClickListener(new View.OnClickListener() {
                //Start new list activity

                public void onClick(View v) {
                    drawable=chats.get(position).getFile().getUrl_file();

                    Intent intent= new Intent(context, ImageDialog.class);
                    intent.putExtra("photoUrl", drawable);
                    v.getContext().startActivity(intent);

                }
            });


            if(chats.get(position).getReadStatus() == 0){


                Chat data = chats.get(position);

                data.setReadStatus(1);
                dbHelper.addMessage(MESSAGES_CHILD, data, data.getChatId(), 1);

                data.setReadStatus(1);
                Map<String, Object> readstatus = new HashMap<>();
                readstatus.put("readStatus",1);

                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD)
                        .child(MESSAGES_CHILD)
                        .child(data.getChatId())
                        .updateChildren(readstatus);

            }



        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configureReceiverStickerHolder(ReceiverStickerHolder receiverStickerHolder, int position){
        Chat receiversChat = chats.get(position);
        if(receiversChat != null){
            if(receiversChat.getPhotoUrl() == null){
                receiverStickerHolder.getCircleImageView()
                        .setImageDrawable(context.getDrawable(R.drawable.ic_account_circle_black_36dp));
            }else{
                Glide.with(context).load(receiversChat.getPhotoUrl()).into(receiverStickerHolder.circleImageView);
            }

            int path = Integer.parseInt(receiversChat.getMessage());

            Glide.with(context).load(path).into(receiverStickerHolder.sticker);

            receiverStickerHolder.getTimestamp()
                    .setText( DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    receiversChat.getTimestamp()
                            )
                    );

            if(chats.get(position).getReadStatus() == 0){


                Chat data = chats.get(position);
                data.setReadStatus(1);


                dbHelper.addMessage(MESSAGES_CHILD, data, data.getChatId(), 1);

                Map<String, Object> readstatus = new HashMap<>();
                readstatus.put("readStatus",1);

                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD)
                        .child(MESSAGES_CHILD)
                        .child(data.getChatId())
                        .updateChildren(readstatus);
            }



        }
    }


    private void configureSenderMessageHolder(SenderMessageHolder senderMessageHolder, int position){
        if(sender != null){
            senderMessageHolder.getSendersMessage()
                    .setText(
                            chats.get(position).getMessage()
                    );
            senderMessageHolder.getTimestamp()
                    .setText( DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    chats.get(position).getTimestamp()
                            )
                    );
            if (chats.get(position).getReadStatus() == 0) {
                senderMessageHolder.deliveryStatus.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ic_delivered));
            } else {
                senderMessageHolder.deliveryStatus.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.seen_status));

            }
        }
    }
    private void configureSenderStickerHolder(SenderStickerHolder senderStickerHolder, int position){
        if(sender != null){
            int path = Integer.parseInt(chats.get(position).getMessage());
            Glide.with(context).load(path).into(senderStickerHolder.sticker);
            senderStickerHolder.getTimestamp()
                    .setText( DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    chats.get(position).getTimestamp()
                            )
                    );
            if (chats.get(position).getReadStatus() == 0) {
                senderStickerHolder.deliveryStatus.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ic_delivered));
            } else {
                senderStickerHolder.deliveryStatus.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.seen_status));

            }
        }
    }
    private void configureSystemMessageHolder(SystemMessageHolder systemMessageHolder, int position){
        if(sender != null){
            String msg = chats.get(position).getMessage() + " " + chats.get(position).getSender();
            systemMessageHolder.getSystemMsg().setText(msg);
        }
    }

}
