
package com.hurray.landlord.game.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class ScoreRec {

    private static Context mAppCtx;

    private static final String MATCH_RECORD = "match_record";

    private static final String ROUND_SCORE = "round_score";

    public synchronized static void init(Context ctx) {
        mAppCtx = ctx.getApplicationContext();
    }

    public static void setMatchRecord(int matchRecord) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            if (matchRecord > sp.getInt(MATCH_RECORD, 0)) {
                Editor ed = sp.edit();
                ed.putInt(MATCH_RECORD, matchRecord);
                ed.commit();
            }
        }
    }

    public static int getMatchRecord() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            return sp.getInt(MATCH_RECORD, 0);
        }
    }

    public static void setRoundScore(int roundScore) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            Editor ed = sp.edit();
            ed.putInt(ROUND_SCORE, roundScore);
            ed.commit();
        }
    }

    public static void addToRoundScore(int add) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            Editor ed = sp.edit();
            ed.putInt(ROUND_SCORE, sp.getInt(ROUND_SCORE, 0) + add);
            ed.commit();
        }
    }

    public static int getRoundScore() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            return sp.getInt(ROUND_SCORE, 0);
        }
    }

}
