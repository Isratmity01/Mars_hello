package com.grameenphone.mars.adapter.chatroomviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grameenphone.mars.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class ReceiverStickerHolder extends RecyclerView.ViewHolder {

    public CircleImageView circleImageView;
    public ImageView sticker;
    public TextView timestamp;

    public ReceiverStickerHolder(View itemView) {
        super(itemView);
        circleImageView = (CircleImageView) itemView.findViewById(R.id.receiversImage);
        sticker = (ImageView) itemView.findViewById(R.id.stickerimage_recevier);
        timestamp = (TextView) itemView.findViewById(R.id.receiversstkName);
    }

    public CircleImageView getCircleImageView() {
        return circleImageView;
    }

    public void setCircleImageView(CircleImageView circleImageView) {
        this.circleImageView = circleImageView;
    }

    public ImageView getReceiversMessage() {
        return sticker;
    }

    public void setReceiversMessage(ImageView receiversMessage) {
        this.sticker = receiversMessage;
    }

    public TextView getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(TextView timestamp) {
        this.timestamp = timestamp;
    }

}
