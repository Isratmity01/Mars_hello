package com.grameenphone.mars.adapter.chatroomviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grameenphone.mars.R;

/**
 * Created by fahad on 5/21/17.
 */

public class SenderImageHolder extends RecyclerView.ViewHolder {
    public ImageView imageFileView;
    public TextView timestamp;
    public ImageView deliveryStatus;
    public SenderImageHolder(View itemView) {
        super(itemView);
        imageFileView = (ImageView) itemView.findViewById(R.id.senderrimage);
        timestamp = (TextView) itemView.findViewById(R.id.senderName);
        deliveryStatus=(ImageView)itemView.findViewById(R.id.delivery_status_senderimg);
    }

    public ImageView getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(ImageView deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public ImageView getSendersMessage() {
        return imageFileView;
    }

    public void setSendersMessage(ImageView sendersMessage) {
        this.imageFileView = sendersMessage;
    }

    public TextView getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(TextView timestamp) {
        this.timestamp = timestamp;
    }
}
