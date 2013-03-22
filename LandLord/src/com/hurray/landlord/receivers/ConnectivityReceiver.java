
package com.hurray.landlord.receivers;

import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.NetworkUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver extends BroadcastReceiver {

    private static final String TAG = "ConnectivityReceiver";

    private static OnGprsConnectedListener mListener = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG, "intent.getAction()" + intent.getAction());
        NetworkUtil.checkNetworkState();

        LogUtil.d(TAG, "NetworkUtil.isCmWap() " + NetworkUtil.isCmWap());

        synchronized (this) { // 防止onCmwapConnected被多次调用
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = cm.getActiveNetworkInfo();
            if (network != null) {
                String extraInfo = network.getExtraInfo();
                if (mListener != null) {
                    if (extraInfo != null && extraInfo.compareToIgnoreCase("cmwap") == 0) {
                        mListener.onCmwapConnected();
                    } else if (extraInfo != null && extraInfo.compareToIgnoreCase("cmnet") == 0) {
                        mListener.onCmnetConnected();
                    } else {
                        mListener.onOtherConnectivityChanged();
                    }
                    mListener = null;
                } else {
                    if (extraInfo != null && extraInfo.compareToIgnoreCase("cmwap") == 0) {
                        // TODO:on cmwap
                    }
                }
            } else {
                if (mListener != null) {
                    mListener.onOtherConnectivityChanged();
                    mListener = null;
                }
            }
        }

    }

    public static void setNextOnGprsConnectedListener(OnGprsConnectedListener listener) {
        mListener = listener;
    }

    public interface OnGprsConnectedListener {
        
        public void onCmwapConnected();

        public void onCmnetConnected();

        public void onOtherConnectivityChanged();
    }
}
