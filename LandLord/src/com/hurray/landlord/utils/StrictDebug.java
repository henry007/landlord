
package com.hurray.landlord.utils;

import com.hurray.landlord.Constants;

import android.os.Build;
import android.os.StrictMode;


public class StrictDebug {

    private final static String TAG = "StrictDebug";

    /**
     * Used between onCreate and super.onCreate Please use SDK 2.3 or above
     */
    public static void check() {
        if (!Constants.DEBUG) {
            return;
        }
        if (Build.VERSION.SDK_INT < 9) {
            return;
        }
        LogUtil.d(TAG, "start strict mode.");
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                .penaltyDeath().build());
    }
}
