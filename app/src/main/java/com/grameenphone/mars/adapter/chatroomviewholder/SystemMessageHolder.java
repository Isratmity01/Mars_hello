package com.grameenphone.mars.adapter.chatroomviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.grameenphone.mars.R;

/**
 * Created by fahad on 5/21/17.
 */

public class SystemMessageHolder extends RecyclerView.ViewHolder {

    public TextView systemMsg;

    public SystemMessageHolder(View itemView) {
        super(itemView);
        systemMsg = (TextView) itemView.findViewById(R.id.system_message);
    }

    public TextView getSystemMsg() {
        return systemMsg;
    }

    public void setSystemMsg(TextView systemMsg) {
        this.systemMsg = systemMsg;
    }
}
