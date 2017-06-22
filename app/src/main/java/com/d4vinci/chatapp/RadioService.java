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
    public int onStartCommand(final Intent intent, int flags, int startId) {
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
            mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    Log.d(TAG, "onBufferingUpdate: "+percent);
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d(TAG, "onError: what="+what);
                    Log.d(TAG, "onError: extra="+extra);
                    if(what==MediaPlayer.MEDIA_ERROR_UNKNOWN && extra == -2147483648) {
                        restartService(intent);
                        return true;
                    } else if(what == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                        restartService(intent);
                        return true;
                    }
                    return false;
                }
            });
            mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case 703:
                            Log.d(TAG, "onInfo: 703, network brandwidth: " + extra);
                            restartService(intent);
                            return true;
                    }
                    return false;
                }
            });
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void restartService(Intent intent) {
        Log.d(TAG, "restartService: Service restarted with old intent!");
        stopService(intent);
        startService(intent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: The MediaPlayer has moved to the Error state, must be reset!");
        mp.reset();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared: after this, it starts");
        mp.start();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: destroyed");
        super.onDestroy();
        mMediaPlayer.release();
    }
}
