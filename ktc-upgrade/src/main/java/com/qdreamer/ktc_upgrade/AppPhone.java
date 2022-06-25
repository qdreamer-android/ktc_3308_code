package com.qdreamer.ktc_upgrade;

import androidx.multidex.MultiDexApplication;

import com.pwong.library.utils.StorageUtil;

public class AppPhone extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        StorageUtil.INSTANCE.initCachePath(this);
    }
}
