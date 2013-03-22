
package com.hurray.landlord.utils;

/***
 * SoundUtil
 * author: linxin
 */

import com.hurray.landlord.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

public class SoundUtil implements OnCompletionListener,
        OnErrorListener {

    private static final String TAG = "SoundUtil";

    private static SoundUtil mSingleton;

    private static Context mAppContext;

    public static int mVolStep = 7;

    public static void init(Context ctx) {
        mAppContext = ctx.getApplicationContext();
    }

    private Hashtable<Integer, MediaPlayer> mSoundPlayerMap;

    private Hashtable<MediaPlayer, OnSoundListener> mSoundListenerMap;

    private VolChangeReceiver mVolChangeReceiver;

    private SoundUtil() {
        mSoundPlayerMap = new Hashtable<Integer, MediaPlayer>();
        mSoundListenerMap = new Hashtable<MediaPlayer, OnSoundListener>();
        mVolChangeReceiver = new VolChangeReceiver();
        IntentFilter intentfilter = new IntentFilter(Constants.VOL_CHANGE_ACTION);
        mAppContext.registerReceiver(mVolChangeReceiver, intentfilter);
    }

    public synchronized static SoundUtil getSingleton() {
        if (mSingleton == null) {
            mSingleton = new SoundUtil();
        }
        return mSingleton;
    }

    public void play(int resId) {
        play(resId, true, null);
    }

    public void playRepeat(final int resId, final int repeat) {
        OnSoundListener l = new OnSoundListener() {

            int count = 1;

            @Override
            public void onError(int errCode) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onCompletion() {
                if (count < repeat) {
                    play(resId, this);
                    count++;
                }

            }
        };

        play(resId, l);
    }

    public void playSerialized(final int[] resIdArray) {

        final int len = resIdArray.length;

        OnSoundListener l = new OnSoundListener() {

            int idx = 1;

            @Override
            public void onError(int errCode) {

            }

            @Override
            public void onCompletion() {
                LogUtil.d(TAG, "onCompletion idx=" + idx);
                if (idx < len) {
                    play(resIdArray[idx], this);
                    idx++;
                }

            }
        };

        play(resIdArray[0], l);
    }

    public void play(int resId, OnSoundListener listener) {
        play(resId, true, listener);
    }

    public void playButIgnorePlaying(int resId) {
        play(resId, false, null);
    }

    private void play(int resId, boolean stopPlaying,
            OnSoundListener listener) {
        if (resId <= 0) {
            if (listener != null)
                listener.onError(OnSoundListener.ERR_BAD_RES_ID);
            return;
        }

        if (!SoundSwitch.isSoundOn()) {
            if (listener != null)
                listener.onError(OnSoundListener.ERR_MUTE);
            return;
        }

        if (stopPlaying) {
            stop(resId);
        } else {
            if (isPlaying(resId)) {
                if (listener != null)
                    listener.onError(OnSoundListener.ERR_WONT_STOP_CURR_PLAYING);
                return;
            }
        }

        release(resId);

        createMediaPlayer(resId, listener);
    }

    private synchronized void createMediaPlayer(int resId, OnSoundListener listener) {
        try {
            MediaPlayer mp = MediaPlayer.create(mAppContext, resId);
            if (mp != null) {
                mSoundPlayerMap.put(resId, mp);
                if (listener != null) {
                    mSoundListenerMap.put(mp, listener);
                }
                setMediaPlayerParameters(mp);
                start(resId);
            } else {
                if (listener != null)
                    listener.onError(OnSoundListener.ERR_CREATE_PLAYER_FAIL);
            }

        } catch (IOException e) {
            e.printStackTrace();
            release(resId);
            if (listener != null)
                listener.onError(OnSoundListener.ERR_INIT_PLAYER_FAIL);
        }
    }

    private void setMediaPlayerParameters(MediaPlayer mp) throws IOException, IllegalStateException {
        if (null != mp) {
            mp.setVolume(mVolStep, mVolStep);
            mp.setOnCompletionListener(this);
            mp.setOnErrorListener(this);
        }
    }

    private boolean isPlaying(int resId) {
        MediaPlayer mp;
        mp = mSoundPlayerMap.get(resId);
        if (null != mp) {
            return mp.isPlaying();
        }

        return false;
    }

    private void start(int resId) throws IllegalStateException {
        AudioManager audioManager = (AudioManager) mAppContext
                .getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();

        MediaPlayer mp = mSoundPlayerMap.get(resId);
        if (AudioManager.RINGER_MODE_NORMAL == ringerMode) {
            if (mp != null)
                mp.start();
        } else {
            handleError(resId, OnSoundListener.ERR_NOT_RINGER_MODE_NORMAL);
        }
    }

    private void stop(int resId) {
        MediaPlayer mp = mSoundPlayerMap.get(resId);
        stop(mp);
    }

    private void stop(MediaPlayer mp) {
        try {
            if (mp != null && mp.isPlaying()) {
                mp.stop();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void release(int resId) {
        MediaPlayer mp = mSoundPlayerMap.get(resId);
        release(mp);
        remove(mp);
    }

    private void release(MediaPlayer mp) {
        if (mp != null) {
            mp.release();
        }
    }

    private void remove(MediaPlayer mp) {
        synchronized (mSoundPlayerMap) {
            boolean ret = mSoundPlayerMap.values().remove(mp);
            LogUtil.d(TAG, "remove ret=" + ret);
        }
    }

    public void stopAndReleaseAll() {
        synchronized (mSoundPlayerMap) {
            Iterator<MediaPlayer> it = mSoundPlayerMap.values().iterator();
            while (it.hasNext()) {
                MediaPlayer mp = it.next();
                stop(mp);
                release(mp);
            }
        }

        mSoundPlayerMap.clear();
        mSoundListenerMap.clear();
    }

    public void close() {
        stopAndReleaseAll();
        mAppContext.unregisterReceiver(mVolChangeReceiver);
        mAppContext = null;
        mSingleton = null;
    }

    private void handleError(int resId, int errCode) {
        MediaPlayer mp = mSoundPlayerMap.get(resId);
        if (mp != null) {
            handleError(mp, errCode);
        }
    }

    private void handleError(MediaPlayer mp, int errCode) {
        release(mp);
        remove(mp);
        OnSoundListener l = mSoundListenerMap.get(mp);
        if (l != null) {
            l.onError(errCode);
            mSoundListenerMap.remove(mp);
        }
    }

    private void handleCompletion(MediaPlayer mp) {
        release(mp);
        remove(mp);

        OnSoundListener l = mSoundListenerMap.get(mp);
        if (l != null) {
            l.onCompletion();
            mSoundListenerMap.remove(mp);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtil.d(TAG, "onError");

        handleError(mp, OnSoundListener.ERR_FROM_MEDIA_PLAYER);

        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtil.d(TAG, "onCompletion");

        handleCompletion(mp);
    }

    private class VolChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mVolStep = intent.getIntExtra("value", 0);
        }

    }

    public static interface OnSoundListener {

        public static final int ERR_FROM_MEDIA_PLAYER = 1;

        public static final int ERR_BAD_RES_ID = 2;

        public static final int ERR_MUTE = 3;

        public static final int ERR_WONT_STOP_CURR_PLAYING = 4;

        public static final int ERR_CREATE_PLAYER_FAIL = 5;

        public static final int ERR_INIT_PLAYER_FAIL = 6;

        public static final int ERR_NOT_RINGER_MODE_NORMAL = 7;

        public void onCompletion();

        public void onError(int errCode);
    }

}
