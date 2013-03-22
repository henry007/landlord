
package com.hurray.landlord.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class ManifestUtil {

    private static Context mAppContext;

    public static void init(Context ctx) {
        mAppContext = ctx.getApplicationContext();
    }

    public static String getChannelId() {
        String channelId = "";
        try {
            ApplicationInfo appInfo = mAppContext.getPackageManager()
                    .getApplicationInfo(
                            mAppContext.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (appInfo != null) {
                channelId = appInfo.metaData.getString("CHANNEL");
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return channelId;
    }

    public static int getAssetsResVersion() {
        int assetsResVer = 0;
        try {
            ApplicationInfo appInfo = mAppContext.getPackageManager()
                    .getApplicationInfo(
                            mAppContext.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (appInfo != null) {
                assetsResVer = appInfo.metaData.getInt("RES_VER");
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return assetsResVer;
    }

    public static int getVersionCode() {
        int versionCode = 0;
        try {
            versionCode = mAppContext.getPackageManager().
                    getPackageInfo(mAppContext.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    public static String getVersionName() {
        String versionName = "";
        try {
            versionName = mAppContext.getPackageManager().
                    getPackageInfo(mAppContext.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }
}
