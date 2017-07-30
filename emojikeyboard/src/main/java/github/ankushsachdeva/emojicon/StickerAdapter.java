/*
 * Copyright 2014 Ankush Sachdeva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.ankushsachdeva.emojicon;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

class StickerAdapter extends ArrayAdapter<Emojicon> {
	OnEmojiconClickedListener emojiClickListener;
    Context context;



    public StickerAdapter(Context context, List<Emojicon> mData) {
        super(context, R.layout.emojicon_item, mData);
    }
    public StickerAdapter(Context context, Emojicon[] data) {

        super(context, R.layout.emojicon_item, data);
        this.context=context;
    }

    public void setEmojiClickListener(OnEmojiconClickedListener listener){
    	this.emojiClickListener = listener;
    }
    public void seStickerClickListener(OnEmojiconClickedListener onEmojiconClickedListener){
        this.emojiClickListener = onEmojiconClickedListener;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.sticker_item, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = (TextView) v.findViewById(R.id.emojicon_icon);
            holder.stickers=(ImageView)v.findViewById(R.id.stickerholder);
            v.setTag(holder);
        }
        Emojicon emoji = getItem(position);
        Log.d("Drawable", String.valueOf(emoji.getEmojiId()));

        ViewHolder holder = (ViewHolder) v.getTag();
        if(emoji.getEmoji()==null)
        {   holder.stickers.setVisibility(View.VISIBLE);
            int path=emoji.getEmojiId();
            holder.stickers.setVisibility(View.VISIBLE);
            Glide.with(context.getApplicationContext()).load(path).into(holder.stickers);

            holder.stickers.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    emojiClickListener.onEmojiconClicked(getItem(position));
                }
            });
        }
        else {
            holder.icon.setText(emoji.getEmoji());
            holder.icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    emojiClickListener.onEmojiconClicked(getItem(position));
                }
            });
        }
        return v;
    }




    class ViewHolder {
        TextView icon;
        ImageView stickers;
    }
}