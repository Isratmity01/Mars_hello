package com.grameenphone.mars.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.grameenphone.mars.R;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.gcm.SinchService;
import com.grameenphone.mars.model.CallDetails;
import com.grameenphone.mars.model.CallEnded;
import com.grameenphone.mars.model.User;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.grameenphone.mars.gcm.SinchService.CALL_ID;

public class IncomingCallScreenActivity extends BaseActivity {

    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
    private String mCallId;
    private AudioPlayer mAudioPlayer;
    String calltype;
    User userdb;
    ImageView userImage;
    DatabaseHelper databaseHelper;
    User user;
    private Call call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.new_incoming);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        Intent intent=getIntent();
         calltype=intent.getStringExtra("IsIncoming");
        mCallId = getIntent().getStringExtra(CALL_ID);
        databaseHelper=new DatabaseHelper(getApplicationContext());
        userImage=(ImageView)findViewById(R.id.friendImageView);
        Button answer = (Button) findViewById(R.id.answerButton);
        answer.setOnClickListener(mClickListener);
        Button decline = (Button) findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);

        mAudioPlayer = new AudioPlayer(this);
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
       switch (audioManager.getRingerMode())
        {
                case AudioManager.RINGER_MODE_NORMAL:
                    mAudioPlayer.playRingtone();
                    break;
            case AudioManager.RINGER_MODE_VIBRATE:
                mAudioPlayer.makevibrate();
                break;
        }

    }

    @Override
    protected void onServiceConnected() {
         call = getSinchServiceInterface().getCall(mCallId);

        if (call != null) {
            user=databaseHelper.getUser(call.getRemoteUserId());
            call.addCallListener(new SinchCallListener());
            TextView remoteUser = (TextView) findViewById(R.id.remoteUser);
            remoteUser.setText(user.getName());
           if(user.getPhotoUrl() != null){
                Glide.with(getApplicationContext())
                        .load(user.getPhotoUrl())
                        .into(userImage);
            } else {
                userImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.ic_account_circle_black_36dp));
            }
        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }
    @Override
    protected void onUserLeaveHint() {
        Toast.makeText(IncomingCallScreenActivity.this,"home clicked",Toast.LENGTH_SHORT).show();
        super.onUserLeaveHint();
    /*
     * Called as part of the activity lifecycle when an activity is about to go into the background
     * as the result of user choice. For example, when the user presses the Home key, onUserLeaveHint() will be called,
     * but when an incoming phone call causes the in-call Activity to be automatically brought to the foreground,
     * onUserLeaveHint() will not be called on the activity being interrupted.
     * In cases when it is invoked, this method is called right before the activity's onPause() callback.
     */
    }
    private void answerClicked() {
        mAudioPlayer.stopRingtone();
       call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            try {
                call.answer();
                Intent intent = new Intent(this, CallScreenActivity.class);
                intent.putExtra(CALL_ID, mCallId);
                intent.putExtra("IsIncoming", "yes");
                this.finish();
                startActivity(intent);
            } catch (MissingPermissionException e) {
                ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
            }
        } else {
            finish();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "You may now answer the call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();

        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            databaseHelper=new DatabaseHelper(getApplicationContext());
           userdb=  databaseHelper.getUser(call.getRemoteUserId());
            if(calltype.equals("yes"))
            {
                if(cause.toString().equals("DENIED")||cause.toString().equals("CANCELED"))
                {
                    final CallDetails user = new CallDetails(userdb.getName(),System.currentTimeMillis(),"Missed",call.getRemoteUserId(),userdb.getPhotoUrl());
                    final DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                    dbHelper.addUserLog(user);
                }
                else {

                    final CallDetails user = new CallDetails(userdb.getName(),System.currentTimeMillis(),"Incoming",call.getRemoteUserId(),userdb.getPhotoUrl());
                    final DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                    dbHelper.addUserLog(user);
                }
            }
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            EventBus.getDefault().post(new CallEnded("yes"));
            mAudioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    answerClicked();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };
}
