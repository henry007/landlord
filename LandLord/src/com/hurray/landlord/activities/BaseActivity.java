
package com.hurray.landlord.activities;

import com.hurray.landlord.services.PlayMusicService;
import com.hurray.landlord.utils.SoundSwitch;
import com.hurray.landlord.utils.StrictDebug;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

abstract public class BaseActivity extends Activity {

    protected AudioManager mAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictDebug.check();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAudio = (AudioManager) getSystemService(AUDIO_SERVICE);
//        IntentFilter filter = new IntentFilter();
//
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//
//        filter.addAction("lalala");
//        registerReceiver(receiver, filter);

    }

//    BroadcastReceiver receiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            System.out.println("broadCast--Receive!!");
//            String action = intent.getAction();
//            if (action == Intent.ACTION_SCREEN_OFF)
//            {
//                System.out.println("screen is closed!");
//
//            } else if (action == Intent.ACTION_SCREEN_ON)
//            {
//                System.out.println("screen is on!");
//            }
//        }
//    };

    @Override
    protected void onResume() {
        super.onResume();
        // MobclickAgent.onResume(this);
//        sendBroadcast(new Intent("lalala"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(receiver);
    }

    protected void playBgMusic(int resId) {
        Intent i = new Intent(this, PlayMusicService.class);
        i.putExtra(PlayMusicService.IS_PLAY, true);
        i.putExtra(PlayMusicService.RES_ID, resId);
        startService(i);
    }

    protected void playBgMusic() {
        Intent i = new Intent(this, PlayMusicService.class);
        i.putExtra(PlayMusicService.IS_PLAY, true);
        startService(i);
    }

    protected void stopBgMusic() {
        Intent i = new Intent(this, PlayMusicService.class);
        i.putExtra(PlayMusicService.IS_PLAY, false);
        startService(i);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (!onVolumenUP()) {
                    mAudio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                            AudioManager.FLAG_SHOW_UI);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (!onVolumnDown()) {
                    mAudio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                            AudioManager.FLAG_SHOW_UI);
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (event.getRepeatCount() == 0) {
                    if (onBack()) {
                        return true;
                    }
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    protected boolean onVolumenUP() {
        return false;
    }

    protected boolean onVolumnDown() {
        return false;
    }

    protected boolean onBack() {
        return true;// 默认：屏蔽系统返回键
    }

    protected void safeShowDialog(int id) {
        if (!isFinishing()) {
            showDialog(id);
        }
    }
}
