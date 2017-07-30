package com.grameenphone.mars.adapter.chatroomviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.grameenphone.mars.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class ReceiverMessageHolder extends RecyclerView.ViewHolder {

    public CircleImageView circleImageView;
    public TextView receiversMessage;
    public TextView timestamp;

    public ReceiverMessageHolder(View itemView) {
        super(itemView);
        circleImageView = (CircleImageView) itemView.findViewById(R.id.receiversImage);
        receiversMessage = (TextView) itemView.findViewById(R.id.receiversMessage);
        timestamp = (TextView) itemView.findViewById(R.id.receiversmsgName);
    }

    public CircleImageView getCircleImageView() {
        return circleImageView;
    }

    public void setCircleImageView(CircleImageView circleImageView) {
        this.circleImageView = circleImageView;
    }

    public TextView getReceiversMessage() {
        return receiversMessage;
    }

    public void setReceiversMessage(TextView receiversMessage) {
        this.receiversMessage = receiversMessage;
    }

    public TextView getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(TextView timestamp) {
        this.timestamp = timestamp;
    }

}
