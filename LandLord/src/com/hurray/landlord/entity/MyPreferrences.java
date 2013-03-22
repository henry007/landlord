
package com.hurray.landlord.entity;

import com.hurray.landlord.Constants;
import com.hurray.landlord.utils.LogUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class MyPreferrences {

    protected static Context mContext;

    public static void initContext(Context c) {

        mContext = c.getApplicationContext();

    }

    public SharedPreferences getPreferrence(String name) {

        return mContext.getSharedPreferences(name, 0);
    }

    public SharedPreferences.Editor getEditor(String name) {

        return getPreferrence(name).edit();
    }

    public static class AccountPreferrence extends MyPreferrences {

        private String shareName;

        public boolean savePwd;

        public String user;

        public String nick;

        public String pass;

        public int userId;

        private static AccountPreferrence apf;

        private AccountPreferrence(String name) {
            // super(c);

            shareName = name;
        }

        public static AccountPreferrence getSingleton() {

            if (apf == null) {

                apf = new AccountPreferrence(Constants.PREFS_ACCOUNT);
            }
            return apf;
        }

        public synchronized void registerOnSharedPreferenceChangeListener(
                OnSharedPreferenceChangeListener listener) {
            getPreferrence(shareName).registerOnSharedPreferenceChangeListener(listener);
        }

        public synchronized void unregisterOnSharedPreferenceChangeListener(
                OnSharedPreferenceChangeListener listener) {
            getPreferrence(shareName).unregisterOnSharedPreferenceChangeListener(listener);
        }

        public synchronized void setOnlinePrizeDesc(String Value) {
            getEditor(shareName).putString(Constants.ONLINE_PRIZE, Value).commit();
        }

        public synchronized String getOnlinePrizeDesc(String defValue) {
            return getPreferrence(shareName).getString(Constants.ONLINE_PRIZE, defValue);
        }

        public synchronized void setAwardPopFlag(boolean Value) {
            getEditor(shareName).putBoolean(Constants.AWARD_FLAG, Value).commit();
        }

        public synchronized boolean getAwardPopFlag(boolean defValue) {

            return getPreferrence(shareName).getBoolean(Constants.AWARD_FLAG, defValue);
        }

        public synchronized void setAwardDesc(String Value) {

            getEditor(shareName).putString(Constants.AWARD_DESC, Value).commit();
        }

        public synchronized String getAwardDesc(String defValue) {

            return getPreferrence(shareName).getString(Constants.AWARD_DESC, defValue);
        }

        public synchronized void setMoneyGoldVal(int Value) {

            getEditor(shareName).putInt(Constants.MONEY_GOLD_VALUE, Value).commit();
        }

        public synchronized int getMoneyGoldVal(int defValue) {

            return getPreferrence(shareName).getInt(Constants.MONEY_GOLD_VALUE, defValue);
        }

        public synchronized void setMoneyHeartVal(int Value) {

            getEditor(shareName).putInt(Constants.MONEY_HEART_VALUE, Value).commit();
        }

        public synchronized int getMoneyHeartVal(int defValue) {

            return getPreferrence(shareName).getInt(Constants.MONEY_HEART_VALUE, defValue);
        }

        public synchronized boolean isSavePwd(boolean defValue) {
            return getPreferrence(shareName).getBoolean(Constants.SAVE_PWD_KEY, defValue);
        }

        public synchronized void setSavePwd(boolean savePwd) {
            getEditor(shareName).putBoolean(Constants.SAVE_PWD_KEY, savePwd).commit();
        }

        public synchronized String getUser(String defValue) {
            return getPreferrence(shareName).getString(Constants.USER_KEY, defValue);
        }

        public synchronized void setUser(String user) {
            getEditor(shareName).putString(Constants.USER_KEY, user).commit();
        }

        public synchronized String getNick(String defValue) {
            return getPreferrence(shareName).getString(Constants.NICK_KEY, defValue);
        }

        public synchronized void setNick(String nick) {
            getEditor(shareName).putString(Constants.NICK_KEY, nick).commit();
        }

        public synchronized String getPass(String defValue) {
            return getPreferrence(shareName).getString(Constants.PASS_KEY, defValue);
        }

        public synchronized void setPass(String pass) {
            getEditor(shareName).putString(Constants.PASS_KEY, pass).commit();
        }

        public synchronized int getUserId(int defValue) {
            return getPreferrence(shareName).getInt(Constants.USER_ID_KEY, defValue);
        }

        public synchronized void setUserId(int userId) {
            getEditor(shareName).putInt(Constants.USER_ID_KEY, userId).commit();
        }

        public synchronized long getUserId(long defValue) {
            return getPreferrence(shareName).getLong(Constants.USER_ID, defValue);
        }

        public synchronized void setUserId(long userId) {
            getEditor(shareName).putLong(Constants.USER_ID, userId).commit();
        }

        public synchronized String getSessionId(String defValue) {
            return getPreferrence(shareName).getString(Constants.SESSION_ID, defValue);
        }

        public synchronized void setSessionId(String sessionId) {
            getEditor(shareName).putString(Constants.SESSION_ID, sessionId).commit();
        }

        public synchronized String getEmail(String defValue) {
            return getPreferrence(shareName).getString(Constants.EMAIL, defValue);
        }

        public synchronized void setEmail(String email) {
            getEditor(shareName).putString(Constants.EMAIL, email).commit();
        }

        public synchronized String getPassword(String defValue) {
            return getPreferrence(shareName).getString(Constants.PASSWORD, defValue);
        }

        public synchronized void setPassword(String password) {
            getEditor(shareName).putString(Constants.PASSWORD, password).commit();
        }

        public synchronized boolean isGuest(boolean defValue) {
            return getPreferrence(shareName).getBoolean(Constants.IS_GUEST, defValue);
        }

        public synchronized void setGuest(boolean isGuest) {
            getEditor(shareName).putBoolean(Constants.IS_GUEST, isGuest).commit();
        }

        public synchronized int getHairId(int defValue) {
            return getPreferrence(shareName).getInt(Constants.HAIR_ID, defValue);
        }

        public synchronized void setHairId(int hairId) {
            getEditor(shareName).putInt(Constants.HAIR_ID, hairId).commit();
        }

        public synchronized int getClothId(int defValue) {
            return getPreferrence(shareName).getInt(Constants.CLOTHES_ID, defValue);
        }

        public synchronized void setClothId(int clothId) {
            getEditor(shareName).putInt(Constants.CLOTHES_ID, clothId).commit();
        }

        public synchronized boolean isMale(boolean defValue) {
            return getPreferrence(shareName).getBoolean(Constants.IS_MALE, defValue);
        }

        public synchronized void setMale(boolean isMale) {
            getEditor(shareName).putBoolean(Constants.IS_MALE, isMale).commit();
        }

        public synchronized String getNickName(String defValue) {
            return getPreferrence(shareName).getString(Constants.NICKNAME, defValue);
        }

        public synchronized void setNickName(String nickName) {
            getEditor(shareName).putString(Constants.NICKNAME, nickName).commit();
        }

        public synchronized int getLevel(int defValue) {
            return getPreferrence(shareName).getInt(Constants.LEVEL, defValue);
        }

        public synchronized void setLevel(int level) {
            getEditor(shareName).putInt(Constants.LEVEL, level).commit();
        }

        public synchronized int getCurrentExp(int defValue) {
            return getPreferrence(shareName).getInt(Constants.CURRENT_EXP, defValue);
        }

        public synchronized void setCurrentExp(int currentExp) {
            getEditor(shareName).putInt(Constants.CURRENT_EXP, currentExp).commit();
        }

        public synchronized int getNextExp(int defValue) {
            return getPreferrence(shareName).getInt(Constants.NEXT_EXP, defValue);
        }

        public synchronized void setNextExp(int nextExp) {
            getEditor(shareName).putInt(Constants.NEXT_EXP, nextExp).commit();
        }

        public synchronized boolean getSilence(boolean defValue) {
            return getPreferrence(shareName).getBoolean(Constants.SILENCE, defValue);
        }

        public synchronized void setSilence(boolean silence) {
            getEditor(shareName).putBoolean(Constants.SILENCE, silence).commit();
        }

    }

    // public static class PreferManager {
    //
    // public static MyPreferrences getPreferrenceInstance(String name) {
    //
    // if (name.equals(Constants.PREFS_ACCOUNT)) {
    //
    // return AccountPreferrence.getSingleInstance();
    //
    // }
    //
    // return null;
    // }
    // }
}
