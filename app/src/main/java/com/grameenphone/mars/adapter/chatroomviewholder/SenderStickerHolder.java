package com.grameenphone.mars.adapter.chatroomviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grameenphone.mars.R;

/**
 * Created by fahad on 5/21/17.
 */

public class SenderStickerHolder extends RecyclerView.ViewHolder {
    public ImageView sticker;
    public TextView timestamp;
    public ImageView deliveryStatus;
    public SenderStickerHolder(View itemView) {
        super(itemView);
        sticker = (ImageView) itemView.findViewById(R.id.stickerimage);
        timestamp = (TextView) itemView.findViewById(R.id.sendersticker);
        deliveryStatus=(ImageView)itemView.findViewById(R.id.delivery_status_senderstk);
    }

    public ImageView getSendersMessage() {
        return sticker;
    }

    public void setSendersMessage(ImageView sendersMessage) {
        this.sticker = sendersMessage;
    }

    public TextView getTimestamp() {
        return timestamp;
    }

    public ImageView getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(ImageView deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public void setTimestamp(TextView timestamp) {
        this.timestamp = timestamp;
    }
}
