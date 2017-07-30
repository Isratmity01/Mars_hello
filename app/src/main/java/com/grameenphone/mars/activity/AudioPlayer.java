package com.grameenphone.mars.activity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import com.grameenphone.mars.R;

import java.io.FileInputStream;
import java.io.IOException;

public class AudioPlayer {

    static final String LOG_TAG = AudioPlayer.class.getSimpleName();

    private Context mContext;
    Vibrator vibrator;
    private MediaPlayer mPlayer;

    private AudioTrack mProgressTone;

    private final static int SAMPLE_RATE = 16000;

    public AudioPlayer(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void playRingtone() {

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_RING);

        try {
            mPlayer.setDataSource(mContext,
                    Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.nice_sms_tone));
            mPlayer.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not setup media player for ringtone");
            mPlayer = null;
            return;
        }
        mPlayer.setLooping(true);
        mPlayer.start();


    }
    public void makevibrate() {

        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

// Start without a delay
// Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};

// The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
        vibrator.vibrate(pattern, 3);


    }
    public void stopRingtone() {

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        if(vibrator!=null)

            vibrator.cancel();

    }

    public void playProgressTone() {
        stopProgressTone();
        try {
            mProgressTone = createProgressTone(mContext);
            mProgressTone.play();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not play progress tone", e);
        }
    }

    public void stopProgressTone() {
        if (mProgressTone != null) {
            mProgressTone.stop();
            mProgressTone.release();
            mProgressTone = null;
        }
    }
    public void ringtone(){

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_RING);

        try {
            mPlayer.setDataSource(mContext,
                    Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.graceful));
            mPlayer.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not setup media player for ringtone");
            mPlayer = null;
            return;
        }
        mPlayer.start();
        // wait 5 sec... then stop the player.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayer.stop();
            }
        }, 1000);//millisec.
    }
    private static AudioTrack createProgressTone(Context context) throws IOException {
        AssetFileDescriptor fd = context.getResources().openRawResourceFd(R.raw.progress_tone);
        int length = (int) fd.getLength();

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, length, AudioTrack.MODE_STATIC);

        byte[] data = new byte[length];
        readFileToBytes(fd, data);

        audioTrack.write(data, 0, data.length);
        audioTrack.setLoopPoints(0, data.length / 2, 30);

        return audioTrack;
    }

    private static void readFileToBytes(AssetFileDescriptor fd, byte[] data) throws IOException {
        FileInputStream inputStream = fd.createInputStream();

        int bytesRead = 0;
        while (bytesRead < data.length) {
            int res = inputStream.read(data, bytesRead, (data.length - bytesRead));
            if (res == -1) {
                break;
            }
            bytesRead += res;
        }
    }
}
