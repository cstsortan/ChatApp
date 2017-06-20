package com.d4vinci.chatapp;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by D4Vinci on 6/20/2017.
 */

public class RadioService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private static final String TAG = "TAG - RadioService";
    MediaPlayer mMediaPlayer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: bound");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: service started");
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource("http://usa2.fastcast4u.com:3684");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onStartCommand: IOException");
            }
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: The MediaPlayer has moved to the Error state, must be reset!");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared: ");
        mp.start();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: destroyed");
        super.onDestroy();
        mMediaPlayer.release();
    }
}
