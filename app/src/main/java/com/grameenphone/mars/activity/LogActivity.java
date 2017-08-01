package com.grameenphone.mars.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.grameenphone.mars.R;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.fragment.Fragment_Contacts;
import com.grameenphone.mars.fragment.Fragment_PlaceCall;
import com.grameenphone.mars.fragment.Fragment_RecentCalls;
import com.grameenphone.mars.gcm.SinchService;
import com.grameenphone.mars.model.CallDetails;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.navhelper.BottomNavigationViewHelper;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shadman.rahman on 07-Jun-17.
 */

public class LogActivity extends BaseActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton newCall,search;
    User me;
    Call call;
    String myself;
    EventBus myEventBus;
    FrameLayout frameLayout;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.maintab);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        myself = prefs.getString("LoginName", "");
        dbHelper = new DatabaseHelper(getApplicationContext());
        me=dbHelper.getMe();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        newCall=(ImageButton)findViewById(R.id.newcall);
        newCall.setBackgroundResource(R.drawable.ic_new_call);
        frameLayout=(FrameLayout)findViewById(R.id.fragmentholder);
      //  toolbar.setLogo(R.drawable.logo_icon);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.logohdpi);
        ab.setDisplayHomeAsUpEnabled(true);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        newCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeNewCallActivity();
            }
        });










        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation_call);

     BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

   //    BottomNavigationViewHelper.increaseIconSize(getApplicationContext(),bottomNavigationView);

       bottomNavigationView.setSelectedItemId(R.id.action_call);
        Log.d("in log activity + ", String.valueOf(bottomNavigationView.getHeight()));
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_message:
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                             overridePendingTransition(0,0);
                                break;
                            case R.id.action_call:
                                Intent callintent = new Intent( getApplicationContext(), LogActivity.class );
                                startActivity(callintent);
                               overridePendingTransition(0,0);
                                break;
                            case R.id.action_live:
                                Intent intentAddaLive = new Intent(getApplicationContext(),MarsLiveActivity.class);
                                startActivity(intentAddaLive);
                             overridePendingTransition(0,0);
                                break;
                            case R.id.action_settings:
                                Intent intentSettings = new Intent(getApplicationContext(),UserProfileActivity.class);
                                startActivity(intentSettings);
                            overridePendingTransition(0,0);
                                break;
                        }
                        return false;
                    }
                });

    }
  
    private void setupViewPager(final ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),getApplicationContext());
        adapter.addFragment(new Fragment_RecentCalls(), "রিসেন্ট");
        adapter.addFragment(new Fragment_Contacts(),  "কন্টাক্ট");
     //   adapter.addFragment(new Fragment_PlaceCall(), "কল");



        viewPager.setAdapter(adapter);

    }
   public void placeNewCallActivity()
    {
        Fragment_PlaceCall fragment = new Fragment_PlaceCall();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentholder, fragment);
        transaction.addToBackStack(null);
        frameLayout.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        newCall.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        transaction.commit();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private Context context2;
        public ViewPagerAdapter(FragmentManager manager, Context context) {
            super(manager);
            this.context2=context;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }
        @Override
        public int getItemPosition(Object object) {
            if (mFragmentList.contains(object)) {
                return POSITION_UNCHANGED;
            }
            return POSITION_NONE;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    public void callButtonClicked(String remoteuserName,String photourl,String Userid)
    {
        if (remoteuserName.isEmpty()) {
            //Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        try {

            try{
                 call = getSinchServiceInterface().callUser(Userid);
            }catch (Exception e)
            {

            }
            if (call == null) {
             //Service failed for some reason, show a Toast and abort
                Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before "
                        + "placing a call.", Toast.LENGTH_LONG).show();
                return;
            }
            String callId = call.getCallId();
            final CallDetails user = new CallDetails(remoteuserName,System.currentTimeMillis(),"outgoing",Userid,photourl);
            dbHelper.addUserLog(user);
            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra("Photourl", photourl);
            callScreen.putExtra(SinchService.CALL_ID, callId);
            startActivity(callScreen);
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
  //      getSinchServiceInterface().stopClient();
        try {
            unbindService(this);
        }catch (Exception e)
        {

        }
    }

    @Override
    protected void onServiceConnected() {
        String get = null;
        try {
            get = getSinchServiceInterface().getUserName();
        } catch (Exception e) {

        }

        if (TextUtils.isEmpty(get)) {
            if (me.getUid() == null) return;
            else
                getSinchServiceInterface().startClient(me.getUid());


        }

        //else userName.setText(get);
        //   mCallButton.setEnabled(true);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }
}
