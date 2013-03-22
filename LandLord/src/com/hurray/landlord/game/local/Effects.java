
package com.hurray.landlord.game.local;

import com.hurray.landlord.bitmaps.CardFgsBitmap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Effects {

    public static final int CARD_FG_0 = 0;
    public static final int CARD_FG_1 = 1;
    public static final int CARD_FG_2 = 2;

    public static final int GAME_BG_0 = 0;
    public static final int GAME_BG_1 = 1;
    public static final int GAME_BG_2 = 2;

    private static final String CARD_FG = "cd_fg";
    private static final String GAME_BG = "gm_bg";

    private static final String BEAUTY_ANNA = "by_anna";
    private static final String BEAUTY_JENNY = "by_jenny";

    private static Context mAppCtx;

    public static void init(Context ctx) {
        mAppCtx = ctx.getApplicationContext();
    }

    public static int getCardFg() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            return sp.getInt(CARD_FG, CARD_FG_0);
        }
    }

    public static void setCardFg(int cardFg) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            Editor ed = sp.edit();
            ed.putInt(CARD_FG, cardFg);
            ed.commit();
        }
        CardFgsBitmap.updateCardFg();
    }

    public static int getGameBg() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            return sp.getInt(GAME_BG, GAME_BG_0);
        }
    }

    public static void setGameBg(int gameBg) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            Editor ed = sp.edit();
            ed.putInt(GAME_BG, gameBg);
            ed.commit();
        }
    }

    public static boolean isAnnaOn() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            return sp.getBoolean(BEAUTY_ANNA, false);
        }
    }

    public static void setAnnaOn(boolean on) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            Editor ed = sp.edit();
            ed.putBoolean(BEAUTY_ANNA, on);
            ed.commit();
        }
    }

    public static boolean isJennyOn() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            return sp.getBoolean(BEAUTY_JENNY, false);
        }
    }

    public static void setJennyOn(boolean on) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            Editor ed = sp.edit();
            ed.putBoolean(BEAUTY_JENNY, on);
            ed.commit();
        }
    }

}
