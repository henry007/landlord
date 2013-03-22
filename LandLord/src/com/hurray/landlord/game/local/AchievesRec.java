
package com.hurray.landlord.game.local;

import com.hurray.landlord.Constants;
import com.hurray.landlord.utils.LogUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import junit.framework.Assert;

public class AchievesRec {

    private static final String TAG = "AchievesRec";

    public static final int ACHIEVE_ID_FG0 = 0; // default 牌面

    public static final int ACHIEVE_ID_FG1_SLWS = 1; // 神龙无双：单局打出12张及以上的顺子，例如345678910JQKA

    public static final int ACHIEVE_ID_FG2_PLW = 2; // 霹雳王：单局打出2048倍

    public static final int ACHIEVE_ID_BG0 = 3; // default 背景

    public static final int ACHIEVE_ID_BG1_FXX = 4; // 飞行侠：单局打出4飞

    public static final int ACHIEVE_ID_BG2_FTZZ = 5; // 飞天至尊：单局打出8连对

    public static final int ACHIEVE_ID_KQS = 6; // 快抢手：两手出完

    public static final int ACHIEVE_ID_DYJ = 7; // 大赢家：连胜10局

    private static Context mAppCtx;

    private static final String ACHIEVES = "achieves";

    private static final String ACHIEVED = "_achd";

    private static final String SELECTED = "_seld";

    public static void init(Context ctx) {
        mAppCtx = ctx.getApplicationContext();
        Effects.init(ctx);
        initData();
    }
    
    private static void initData() {
        setAchieved(ACHIEVE_ID_FG0, true);
        setAchieved(ACHIEVE_ID_BG0, true);
        
        if(Constants.DEBUG_POSSESSION_ACH){
            setAchieved(AchievesRec.ACHIEVE_ID_FG1_SLWS, true);
            setAchieved(AchievesRec.ACHIEVE_ID_BG2_FTZZ, true);
            setAchieved(AchievesRec.ACHIEVE_ID_BG1_FXX, true);
            setAchieved(AchievesRec.ACHIEVE_ID_KQS, true);
            setAchieved(AchievesRec.ACHIEVE_ID_FG2_PLW, true);
            setAchieved(AchievesRec.ACHIEVE_ID_DYJ, true);
        }
        
        if (!isSelected(ACHIEVE_ID_FG1_SLWS) && !isSelected(ACHIEVE_ID_FG2_PLW)) {
            setSelected(ACHIEVE_ID_FG0, true);
        }
        
        if (!isSelected(ACHIEVE_ID_BG1_FXX) && !isSelected(ACHIEVE_ID_BG2_FTZZ)) {
            setSelected(ACHIEVE_ID_BG0, true);
        }
    }

    public static ArrayList<Integer> getAchievedIds() {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            int begIdx = ACHIEVE_ID_FG0;
            int endIdx = ACHIEVE_ID_DYJ;

            for (int i = begIdx; i <= endIdx; i++) {
                if (isAchieved(sp, i)) {
                    arr.add(i);
                }
            }
            return arr;
        }
    }

    public static ArrayList<Integer> getSelectedIds() {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            int begIdx = ACHIEVE_ID_FG0;
            int endIdx = ACHIEVE_ID_DYJ;

            for (int i = begIdx; i <= endIdx; i++) {
                if (isSelected(sp, i)) {
                    arr.add(i);
                }
            }
            return arr;
        }
    }

    public static void setAchieved(int achieveId, boolean isAchieved) {
        LogUtil.d(TAG, "achieveId=" + achieveId + " isAchieved=" + isAchieved);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            Editor ed = sp.edit();
            ed.putBoolean(ACHIEVES + ACHIEVED + getAchieveName(achieveId), isAchieved);
            ed.commit();
        }
    }

    public static void setSelected(int achieveId, boolean isSelected) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            Editor ed = sp.edit();

            switch (achieveId) {
                case ACHIEVE_ID_FG0:
//                    if (isSelected) {
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG0), true);
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG1_SLWS), false);
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG2_PLW), false);
//                    }
                    break;
                case ACHIEVE_ID_FG1_SLWS:
//                    if (isSelected) {
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG0), false);
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG1_SLWS), true);
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG2_PLW), false);
//                    } else {
//                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG0), true);
//                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG1_SLWS), false);
//                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG2_PLW), false);
//                    }
                    break;
                case ACHIEVE_ID_FG2_PLW:
//                    if (isSelected) {
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG0), false);
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG1_SLWS), false);
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG2_PLW), true);
//                    } else {
//                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG0), true);
//                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG1_SLWS), false);
//                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_FG2_PLW), false);
//                    }
                    break;
                case ACHIEVE_ID_BG0:
//                    if (isSelected) {
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG0), true);
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG1_FXX), false);
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG2_FTZZ), false);
//                    }
                    break;
                case ACHIEVE_ID_BG1_FXX: // 飞行侠：单局打出4飞.
                    
//                    if (isSelected) {
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG0), false);
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG1_FXX), true);
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG2_FTZZ), false);
//                    } else {
//                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG0), true);
//                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG1_FXX), false);
//                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG2_FTZZ), false);
//                    }
                    break;
                case ACHIEVE_ID_BG2_FTZZ: // 飞天至尊：单局打出8连对
//                    if (isSelected) {
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG0), false);
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG1_FXX), false);
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG2_FTZZ), true);
//                    } else {
//                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG0), true);
//                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG1_FXX), false);
//                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_BG2_FTZZ), false);
//                    }
                    break;
                case ACHIEVE_ID_KQS: // 快抢手：两手出完
                    if (isSelected) {
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_KQS), true);
                    } else {
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_KQS), false);
                    }
                    break;
                case ACHIEVE_ID_DYJ: // 大赢家：连胜10局
                    if (isSelected) {
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_DYJ), true);
                    } else {
                        ed.putBoolean(ACHIEVES + SELECTED + getAchieveName(ACHIEVE_ID_DYJ), false);
                    }
                    break;

            }

            ed.commit();
        }

        setEffect(achieveId, isSelected);
    }

    public static boolean isAchieved(int achieveId) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            return isAchieved(sp, achieveId);
        }
    }

    public static boolean isSelected(int achieveId) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mAppCtx);
        synchronized (sp) {
            return isSelected(sp, achieveId);
        }
    }

    private static boolean isAchieved(SharedPreferences sp, int achieveId) {
        return sp.getBoolean(ACHIEVES + ACHIEVED + getAchieveName(achieveId), false);
    }

    private static boolean isSelected(SharedPreferences sp, int achieveId) {
        return sp.getBoolean(ACHIEVES + SELECTED + getAchieveName(achieveId), false);
    }

    private static String getAchieveName(int achieveId) {
        switch (achieveId) {
            case ACHIEVE_ID_FG0:
                return "fg0";
            case ACHIEVE_ID_FG1_SLWS:
                return "slws";
            case ACHIEVE_ID_FG2_PLW:
                return "plw";
            case ACHIEVE_ID_BG0:
                return "bg0";
            case ACHIEVE_ID_BG1_FXX:
                return "fxx";
            case ACHIEVE_ID_BG2_FTZZ:
                return "ftzz";
            case ACHIEVE_ID_KQS:
                return "kqs";
            case ACHIEVE_ID_DYJ:
                return "dyj";
        }

        Assert.assertTrue("UNKNOW ACHIEVE ID", false);
        return "";
    }

    private static void setEffect(int achieveId, boolean selected) {
        switch (achieveId) {
            case ACHIEVE_ID_FG0:
                if (selected)
                    Effects.setCardFg(Effects.CARD_FG_0);
                break;
            case ACHIEVE_ID_FG1_SLWS:
                if (selected)
                    Effects.setCardFg(Effects.CARD_FG_2);//lhx 掉换萝莉和皇后卡片背景
                else
                    Effects.setCardFg(Effects.CARD_FG_0);
                break;
            case ACHIEVE_ID_FG2_PLW:
                if (selected)
                    Effects.setCardFg(Effects.CARD_FG_1);//lhx 掉换萝莉和皇后卡片背景
                else
                    Effects.setCardFg(Effects.CARD_FG_0);
                break;
            case ACHIEVE_ID_BG0:
                if (selected)
                    Effects.setGameBg(Effects.GAME_BG_0);
                break;
            case ACHIEVE_ID_BG1_FXX:
                if (selected)
                    Effects.setGameBg(Effects.GAME_BG_2);//lhx 掉换夜上海和绿野仙踪背景
                else
                    Effects.setGameBg(Effects.GAME_BG_0);
                break;
            case ACHIEVE_ID_BG2_FTZZ:
                if (selected)
                    Effects.setGameBg(Effects.GAME_BG_1);//lhx 掉换夜上海和绿野仙踪背景
                else
                    Effects.setGameBg(Effects.GAME_BG_0);
                break;
            case ACHIEVE_ID_KQS:
                Effects.setAnnaOn(selected);
                break;
            case ACHIEVE_ID_DYJ:
                Effects.setJennyOn(selected);
                break;
        }
    }

}
