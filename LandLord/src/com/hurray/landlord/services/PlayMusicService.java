
package com.hurray.landlord.services;

import com.hurray.landlord.utils.SoundSwitch;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;

public class PlayMusicService extends IntentService {

    public static final String RES_ID = "resid";

    public static final String IS_PLAY = "play_or_stop";

    private static final String TAG = "PlayMusicService";

    private static MediaPlayer sMediaPlayer;

    private static int sCurrMusicResId = -1;

    private Handler mHanlder = new Handler();

    public PlayMusicService() {
        super(PlayMusicService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            boolean isPlay = intent.getBooleanExtra(IS_PLAY, false);
            int resId = intent.getIntExtra(RES_ID, 0);
            if (isPlay) {
                if (resId > 0) {
                    if (sCurrMusicResId != resId) { // 换音乐
                        sCurrMusicResId = resId;
                        releaseMusic();
                        playMusic();
                    } else { // 不换音乐
                        playMusic();
                    }
                } else { // 使用旧资源播放
                    playMusic();
                }
            } else {
                releaseMusic();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playMusic() {
        if (SoundSwitch.isMusicOn()) {
            startMusic();
        } else {
            releaseMusic();
        }
    }

    private void startMusic() {
        mHanlder.post(new Runnable() {

            @Override
            public void run() {
                if (sMediaPlayer == null && sCurrMusicResId >= 0) {
                    sMediaPlayer = MediaPlayer.create(getApplicationContext(), sCurrMusicResId);
                    sMediaPlayer.setLooping(true);
                    sMediaPlayer.start();
                }
            }

        });
    }

    private void releaseMusic() {
        if (sMediaPlayer == null) {
            return;
        }
        
        mHanlder.post(new Runnable() {

            @Override
            public void run() {
                if (sMediaPlayer != null) {
                    sMediaPlayer.release();
                    sMediaPlayer = null;
                }
            }

        });
    }

}
