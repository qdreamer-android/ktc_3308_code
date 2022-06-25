package com.qdreamer.ktc_upgrade.serial;

import com.qdreamer.ktc_upgrade.HomeActivity;
import com.qdreamer.ktc_upgrade.KtcPkgWriteInfo;

import java.lang.ref.WeakReference;

public class SerialPortPresenter implements SerialPortUtil.OnSerialReadContentListener {

    private WeakReference<HomeActivity> weakReference;

    private SerialPortUtil serialPortUtil;

    public SerialPortPresenter(HomeActivity activity) {
        weakReference = new WeakReference<>(activity);
        serialPortUtil = new SerialPortUtil();
        serialPortUtil.openSerialPort(this);
    }

    public void sendSerialPortMessage(KtcPkgWriteInfo bean) {
        serialPortUtil.sendSerialPort(bean.parse());
    }

    public void disconnect() {
        if (serialPortUtil != null) {
            serialPortUtil.closeSerialPort();
            serialPortUtil = null;
        }
    }

    @Override
    public void onReadContent(byte[] body) {
        if (weakReference.get() != null && !weakReference.get().isDestroyed()) {
            weakReference.get().parseRecMsg(body);
        }
    }
}
