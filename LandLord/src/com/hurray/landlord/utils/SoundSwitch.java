
package com.hurray.landlord.utils;

import com.hurray.landlord.services.PlayMusicService;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SoundSwitch {

    private static Context mAppCtx;

    private static final String SOUND_SWITCH = "sound_sw";

    private static final String MUSIC_SWITCH = "music_sw";

    public synchronized static void init(Context ctx) {
        mAppCtx = ctx.getApplicationContext();
    }

    public static void setSound(boolean on) {
        if (isSoundOn() != on) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
            synchronized (sp) {
                Editor ed = sp.edit();
                ed.putBoolean(SOUND_SWITCH, on);
                ed.commit();
            }

        }
    }

    public static boolean isSoundOn() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            return sp.getBoolean(SOUND_SWITCH, true);
        }
    }

    public static void setMusic(boolean on) {
        if (isMusicOn() != on) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
            synchronized (sp) {
                Editor ed = sp.edit();
                ed.putBoolean(MUSIC_SWITCH, on);
                ed.commit();
            }

            Intent i = new Intent(mAppCtx, PlayMusicService.class);
            i.putExtra(PlayMusicService.IS_PLAY, true);
            mAppCtx.startService(i);
        }
    }

    public static boolean isMusicOn() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            return sp.getBoolean(MUSIC_SWITCH, true);
        }
    }

}
