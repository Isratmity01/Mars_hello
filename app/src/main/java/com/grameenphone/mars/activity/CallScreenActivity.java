package com.grameenphone.mars.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.grameenphone.mars.R;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.gcm.SinchService;
import com.grameenphone.mars.model.CallEnded;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.utility.TimeActivity;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;



public class CallScreenActivity extends BaseActivity {

    static final String TAG = CallScreenActivity.class.getSimpleName();
    private int flag = 0;
    private int flagmic = 0;
    Handler myHandler;
    Runnable myRunnable;
    String calltype;
    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;
private Button speakerButton,micButton;
    private String mCallId,imgpref;
    AudioManager audioManager ;
    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;
    private ImageView userImage;
    DatabaseHelper databaseHelper;
    WindowManager.LayoutParams params;
    Call call;
    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_call);
        userImage=(ImageView)findViewById(R.id.friendImageView);
      //  Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 10000);
        imgpref = getIntent().getStringExtra("Photourl");
        Intent intent=getIntent();
        calltype=intent.getStringExtra("IsIncoming");
        if(calltype==null)
        {
            calltype="no";
        }
        Glide.with(getApplicationContext())
               .load(imgpref)
             .into(userImage);
        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = (TextView) findViewById(R.id.callDuration);
        mCallerName = (TextView) findViewById(R.id.remoteUser);
        mCallState = (TextView) findViewById(R.id.callState);


        Button endCallButton = (Button) findViewById(R.id.hangupButton);
        speakerButton=(Button)findViewById(R.id.speakerButton);
        micButton=(Button)findViewById(R.id.muteButton);
        micButton.setBackgroundResource(R.drawable.ic_mute_inside);
        endCallButton.setBackgroundResource(R.drawable.ic_decline_call_n);
        speakerButton.setBackgroundResource(R.drawable.ic_speaker);
        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);

        audioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        databaseHelper=new DatabaseHelper(getApplicationContext());
        speakerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    flag = 1; // 1 => Button ON
                    speakerButton.setBackgroundResource(R.drawable.ic_speaker_new);
                    //Toast.makeText(CallScreenActivity.this,"start",Toast.LENGTH_SHORT).show();
                    audioManager.setSpeakerphoneOn(true);
                } else {
                    flag = 0; // 0 => Button OFF
                    speakerButton.setBackgroundResource(R.drawable.ic_speaker);
               //     Toast.makeText(CallScreenActivity.this,"off",Toast.LENGTH_SHORT).show();
                    audioManager.setSpeakerphoneOn(false);
                }
            }
        });
        micButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagmic == 0) {
                    flagmic = 1; // 1 => Button ON
                    micButton.setBackgroundResource(R.drawable.ic_mute_new_off);
                  //  Toast.makeText(CallScreenActivity.this,"start",Toast.LENGTH_SHORT).show();
                    audioManager.setMicrophoneMute(true);
                } else {
                    flagmic = 0; // 0 => Button OFF
                    micButton.setBackgroundResource(R.drawable.ic_mute_inside);
                    //Toast.makeText(CallScreenActivity.this,"off",Toast.LENGTH_SHORT).show();
                    audioManager.setMicrophoneMute(false);
                }
            }
        });

    }

    @Override
    public void onServiceConnected() {
         call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            User user=databaseHelper.getUser(call.getRemoteUserId());
            call.addCallListener(new SinchCallListener());
            mCallerName.setText(user.getName());
            if(user.getPhotoUrl() != null){
               Glide.with(getApplicationContext())
                  .load(user.getPhotoUrl())
                        .into(userImage);
            } else {
              userImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.ic_account_circle_black_36dp));
            }
            if(call.getState().toString().equals("INITIATING")&&calltype.equals("yes"))mCallState.setText("কানেক্ট হচ্ছে..");

           else if(call.getState().toString().equals("INITIATING"))mCallState.setText("কল করা হচ্ছে...");
            else mCallState.setText(call.getState().toString());
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        mDurationTask.cancel();
        mTimer.cancel();
    }
    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
    }
    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
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

    private void endCall() {
        mAudioPlayer.stopProgressTone();//
        mAudioPlayer.ringtone();
        //CallDetails callDetails=realm.createObject(CallDetails.class);
        Call call = getSinchServiceInterface().getCall(mCallId);
        String time= TimeActivity.getCurrentTime(getApplicationContext());
        Log.d("Check", call.getCallId() + call.getRemoteUserId() + mDurationTask.toString()+ time +"Outgoing");
        /*callDetails=new CallDetails(call.getCallId(),call.getRemoteUserId(),mDurationTask.toString(),time,"Outgoing");
        realm.beginTransaction();
        realm.copyToRealm(callDetails);
        realm.commitTransaction();
        realm.close();*/
        if (call != null) {
            call.hangup();
        }
        finish();
    }
    private String formatTimespan(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }
    private void updateCallDuration() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            if (call.getDetails().getDuration() == 0) {
                mCallState.setVisibility(View.VISIBLE);
                mCallDuration.setVisibility(View.GONE);
            } else {
                mCallState.setVisibility(View.GONE);
                mCallDuration.setVisibility(View.VISIBLE);

                mCallDuration.setText(formatTimespan(call.getDetails().getDuration()));
            }
        }
    }
    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call call) {
            Toast.makeText(CallScreenActivity.this,"কল শেষ হয়েছে",Toast.LENGTH_SHORT).show();
            CallEndCause cause = call.getDetails().getEndCause();
            call.getDetails().getDuration();
            EventBus.getDefault().post(new CallEnded("yes"));
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            //Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();
            endCall();
        }
        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            mCallState.setText("কল রিসিভ হয়েছে");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }
        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }
        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }
    }
}
