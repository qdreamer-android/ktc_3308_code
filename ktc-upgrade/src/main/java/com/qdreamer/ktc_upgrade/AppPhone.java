package com.qdreamer.ktc_upgrade;

import androidx.multidex.MultiDexApplication;

import com.pwong.library.utils.StorageUtil;

/**
 * @Author: Pen
 * @Create: 2022-05-23 17:32:52
 * @Email: bug@163.com
 */
public class AppPhone extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        StorageUtil.INSTANCE.initCachePath(this);
    }
}
