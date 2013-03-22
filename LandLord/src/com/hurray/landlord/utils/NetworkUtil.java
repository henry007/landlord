
package com.hurray.landlord.utils;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetworkUtil {
    private static final String TAG = "NetworkUtil";

    private static Context sContext;

    private static boolean sIsConnected;

    private static boolean sIsWifi;

    private static boolean sIsLocation;

    private static boolean sIsCmWap;

    public static void initContext(Context context) {
        sContext = context;
    }

    public static boolean isConnected() {
        /*
         * ConnectivityManager cm = (ConnectivityManager) context
         * .getSystemService(Context.CONNECTIVITY_SERVICE); NetworkInfo network
         * = cm.getActiveNetworkInfo(); if (network != null) { return
         * network.isAvailable(); } return false;
         */
        return sIsConnected;
    }

    public static boolean isWifiEnabled() {
        /*
         * WifiManager wifiManager = (WifiManager)
         * context.getSystemService(Context.WIFI_SERVICE); return
         * wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
         */
        return sIsWifi;
    }

    public static boolean isLocationEnabled() {
        /*
         * LocationManager locationManager = (LocationManager) context
         * .getSystemService(Context.LOCATION_SERVICE); List<String>
         * accessibleProviders = locationManager.getProviders(true); return
         * accessibleProviders != null && accessibleProviders.size() > 0;
         */
        return sIsLocation;
    }

    public static boolean isCmWap() {
        return sIsCmWap;
    }

    public static boolean isGprsConnected() {
        return sIsConnected && !sIsWifi;
    }

    public static void checkNetworkState() {
        // Check connectivity state
        ConnectivityManager cm = (ConnectivityManager) sContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network != null && network.isConnected()) {
            LogUtil.d(TAG, "getActiveNetworkInfo getExtraInfo = " + network.getExtraInfo());
            LogUtil.d(TAG, "connected true");
            sIsConnected = true;
            WifiManager wifiManager = (WifiManager) sContext.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                LogUtil.d(TAG, "wifi true");
                sIsWifi = true;
                sIsCmWap = false;
            } else if ("mobile".equalsIgnoreCase(network.getTypeName())
                    && "cmwap".equalsIgnoreCase(network.getExtraInfo())) {
                LogUtil.d(TAG, "cmwap true");
                sIsCmWap = true;
                sIsWifi = false;
            } else {
                LogUtil.d(TAG, "cmnet true");
                sIsWifi = false;
                sIsCmWap = false;
            }
        } else {
            LogUtil.e(TAG, "connected false");
            sIsConnected = false;
            sIsWifi = false;
            sIsCmWap = false;
        }

        // Check location setting state
        LocationManager locationManager = (LocationManager) sContext
                .getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            sIsLocation = true;
        } else {
            sIsLocation = false;
        }

        LogUtil.d(TAG, "location " + sIsLocation);
    }

    public static void checkLocationState() {
        // Check location setting state
        LocationManager locationManager = (LocationManager) sContext
                .getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            sIsLocation = true;
        } else {
            sIsLocation = false;
        }

        LogUtil.d(TAG, "location " + sIsLocation);
    }

    public static void cmWapCheck(HttpClient client) {
        if (isCmWap()) {
            HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
            client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        } else {
            client.getParams().removeParameter(ConnRoutePNames.DEFAULT_PROXY);
        }
    }
}
