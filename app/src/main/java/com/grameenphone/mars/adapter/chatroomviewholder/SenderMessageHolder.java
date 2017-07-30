package com.grameenphone.mars.adapter.chatroomviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grameenphone.mars.R;

/**
 * Created by fahad on 5/21/17.
 */

public class SenderMessageHolder extends RecyclerView.ViewHolder {
    public TextView sendersMessage;
    public TextView timestamp;
    public ImageView deliveryStatus;
    public SenderMessageHolder(View itemView) {
        super(itemView);
        sendersMessage = (TextView) itemView.findViewById(R.id.sendersMessage);
        timestamp = (TextView) itemView.findViewById(R.id.senderName);

        deliveryStatus=(ImageView)itemView.findViewById(R.id.delivery_status_sendermsg);
    }

    public TextView getSendersMessage() {
        return sendersMessage;
    }

    public void setSendersMessage(TextView sendersMessage) {
        this.sendersMessage = sendersMessage;
    }

    public ImageView getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(ImageView deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public TextView getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(TextView timestamp) {
        this.timestamp = timestamp;
    }
}
