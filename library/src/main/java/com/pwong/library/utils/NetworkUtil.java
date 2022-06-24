package com.pwong.library.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static WifiStatus getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return WifiStatus.NETWORK_NONE;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return WifiStatus.NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return WifiStatus.NETWORK_MOBILE;
            }
        } else {
            return WifiStatus.NETWORK_NONE;
        }
        return WifiStatus.NETWORK_NONE;
    }

    public enum WifiStatus {
        NETWORK_WIFI, NETWORK_MOBILE, NETWORK_NONE
    }
}
