
package com.hurray.landlord.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

    private static final String SILENCE = "sp_silence";

    private static Context mAppContext;

    public static void init(Context ctx) {
        mAppContext = ctx.getApplicationContext();
    }

    public static boolean isSilence() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppContext);
        return sp.getBoolean(SILENCE, false);
    }

    public static void setSilence(boolean isSilence) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppContext);
        sp.edit().putBoolean(SILENCE, isSilence).commit();
    }
}
