package com.grameenphone.mars.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.grameenphone.mars.R;
import com.grameenphone.mars.model.SelectUser;
import com.grameenphone.mars.utility.RoundedImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shadman.rahman on 11-Jun-17.
 */

public class ContactListAdapter extends BaseAdapter {

    public List<SelectUser> _data;
    private ArrayList<SelectUser> arraylist;
    Context _c;
    ViewHolder v;
    RoundedImage roundedImage;

    public ContactListAdapter(List<SelectUser> selectUsers, Context context) {
        _data = selectUsers;
        _c = context;
        this.arraylist = new ArrayList<SelectUser>();
        this.arraylist.addAll(_data);
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int i) {
        return _data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.contact_list_item, null);
            Log.e("Inside", "here--------------------------- In view1");
        } else {
            view = convertView;
            Log.e("Inside", "here--------------------------- In view2");
        }

        v = new ViewHolder();

        v.title = (TextView) view.findViewById(R.id.person_name);
   //     v.check = (CheckBox) view.findViewById(R.id.check);
        v.phone = (TextView) view.findViewById(R.id.calltype);

        v.imageView = (CircleImageView) view.findViewById(R.id.person_photo);

        final SelectUser data = (SelectUser) _data.get(i);
        v.title.setText(data.getName());

        v.phone.setText(data.getPhone());

        // Set image if exists
        try {

            if (data.getThumb() != null) {
                Glide.with(v.imageView.getContext())
                        .load(data.getThumb())
                        .into(v.imageView);
            } else {
                v.imageView.setImageResource(
                        R.drawable.user_pic_02);
            }
            // Seting round image

            
        } catch (OutOfMemoryError e) {
            // Add default picture
            v.imageView.setImageResource(R.drawable.user_pic_02);
            e.printStackTrace();
        }

        Log.e("Image Thumb", "--------------" + data.getThumb());

        /*// Set check box listener android
        v.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    data.setCheckedBox(true);
                  } else {
                    data.setCheckedBox(false);
                }
            }
        });*/

        view.setTag(data);
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        _data.clear();
        if (charText.length() == 0) {
            _data.addAll(arraylist);
        } else {
            for (SelectUser wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    _data.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    static class ViewHolder {
        CircleImageView imageView;
        TextView title,phone;

    }
}