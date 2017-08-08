package com.grameenphone.mars.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.grameenphone.mars.R;
import com.grameenphone.mars.activity.HelpAboutActivity;
import com.grameenphone.mars.activity.MainActivityHolder;
import com.grameenphone.mars.activity.NotificationSettingsActivity;
import com.grameenphone.mars.activity.PrivacySettingsActivity;
import com.grameenphone.mars.activity.ProfileUpdateActivity;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.User;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class Fragment_UserProfile extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private CardView notificationCardView;
    private CardView privacyCardView;
    private CardView helpAboutCardView, signoutCard;
    public static final String ANONYMOUS = "anonymous";

    public static FirebaseUser mFirebaseUser;
    private ImageView usersPhoto;
    private TextView userName;
    private TextView usersPhoneNumber;

    private DatabaseHelper dbHelper;
    private User me;
    private Switch Lanswitch;

    private Toolbar toolbar;
    ImageButton ProfileEdit;
    View fragmentView;
    LinearLayout linearLayout1;
    public Fragment_UserProfile() {

        // Required empty public constructor
    }
    public static Fragment_UserProfile newInstance() {
        Fragment_UserProfile f = new Fragment_UserProfile();
        return f;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setHasOptionsMenu(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator ( R.drawable.ic_backiconsmall );

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("প্রোফাইল ও সেটিংস্‌");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (fragmentView == null){


            fragmentView = inflater.inflate(R.layout.activity_user_profile,
                    container, false);
        bindViews(fragmentView);
        }


        return fragmentView;
}

    private void bindViews(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        notificationCardView = (CardView) view.findViewById(R.id.notification_card);
        privacyCardView = (CardView) view.findViewById(R.id.privacy_card);
        helpAboutCardView = (CardView) view.findViewById(R.id.help_about_card);
        signoutCard = (CardView) view.findViewById(R.id.signout_card);
        usersPhoto = (ImageView) view.findViewById(R.id.profile_picture);
        userName = (TextView) view.findViewById(R.id.profile_name);
        usersPhoneNumber = (TextView) view.findViewById(R.id.profile_phone_number);
        Lanswitch = (Switch) view.findViewById(R.id.lang_switcher);

        dbHelper = new DatabaseHelper(getActivity());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }
    public void init()
    {

        me = dbHelper.getMe();

        if (me != null) {

            if (me.getName() != null) userName.setText(me.getName());

            if (me.getPhone() != null) usersPhoneNumber.setText(me.getPhone());

            if (me.getPhotoUrl() != null) {
                RequestOptions options = new RequestOptions();
                options.transform(new BlurTransformation(getActivity()));
                Glide.with(getActivity())
                        .load(me.getPhotoUrl())
                        .into(usersPhoto);


            }
        }



        Lanswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Lanswitch.setChecked(true);
                    }
                });

                alert.show();
            }
        });

        notificationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), NotificationSettingsActivity.class);
                startActivity(intent);
            }
        });

        privacyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), PrivacySettingsActivity.class);
                startActivity(intent);
            }
        });

        helpAboutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), HelpAboutActivity.class);
                startActivity(intent);
            }
        });
        signoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivityHolder)getActivity()).SignOut();
            }
        });
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.group_edit_profile, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_profile:


                Intent intent = new Intent(getActivity(), ProfileUpdateActivity.class);
                intent.putExtra("name", me.getName());
                intent.putExtra("photoUrl", me.getPhotoUrl());
                startActivity(intent);


                return true;

            case R.id.home:
                getActivity().onBackPressed();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }



}
