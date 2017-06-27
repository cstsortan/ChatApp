package com.d4vinci.chatapp;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by D4Vinci on 6/20/2017.
 */

public class RadioService extends Service {

    static boolean isServiceOn=false;

    private static final String TAG = "TAG - RadioService";
    MediaPlayer mMediaPlayer = null;
    private PhoneStateListener phoneStateListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: bound");
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: service started");
        isServiceOn=true;
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource("http://usa2.fastcast4u.com:3684");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                    Log.d(TAG, "onBufferingUpdate: "+ i + "%");
                }
            });
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
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
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        //Incoming call: Pause music
                        stopService(intent);
                    } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                        //Not in call: Play music

                    } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        //A call is dialing, active or on hold
                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };
            TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if(mgr != null) {
                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void restartService(Intent intent) {
        Log.d(TAG, "restartService: Service restarted with old intent!");
        stopService(intent);
        startService(intent);
    }

    @Override
    public void onDestroy() {
        isServiceOn=false;
        Log.d(TAG, "onDestroy: destroyed");
        super.onDestroy();
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }
}