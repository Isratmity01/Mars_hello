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

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;

import github.ankushsachdeva.emojicon.emoji.Emojicon;
import github.ankushsachdeva.emojicon.emoji.People;
import github.ankushsachdeva.emojicon.utils.AppConstants;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 * @author 	Ankush Sachdeva (sankush@yahoo.co.in)
 */
public class StickerGridView {
	public View rootView;
	EmojiconsPopup mEmojiconPopup;
    StickerRecents mRecents;
    Emojicon[] mData;

    public StickerGridView(Context context, Emojicon[] emojicons, StickerRecents recents, EmojiconsPopup emojiconPopup) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		mEmojiconPopup = emojiconPopup;
		rootView = inflater.inflate(R.layout.sticker_grid, null);
		setRecents(recents);
		 GridView gridView = (GridView) rootView.findViewById(R.id.Emoji_GridView);
	        if (emojicons== null) {
	            mData = AppConstants.VHUTO;
	        } else {
	            //Object[] o = (Object[]) emojicons;
	            mData = emojicons;
	        }
		StickerAdapter mAdapter = new StickerAdapter(rootView.getContext(), mData);
	        mAdapter.seStickerClickListener(new EmojiconGridView.OnEmojiconClickedListener()  {
				
				@Override
				public void onEmojiconClicked(Emojicon emojicon) {
					if (mEmojiconPopup.onEmojiconClickedListener != null) {
			            mEmojiconPopup.onEmojiconClickedListener.onEmojiconClicked(emojicon);
			        }
			        if (mRecents != null) {
			            //mRecents.addRecentSticker(rootView.getContext(), emojicon);
			        }
				}
			});
	        gridView.setAdapter(mAdapter);
	}
    
	private void setRecents(StickerRecents recents) {
        mRecents = recents;
    }



	public interface OnStickerClickedListener {
		void onEmojiconClicked(Emojicon emojicon);
	}
    
}
