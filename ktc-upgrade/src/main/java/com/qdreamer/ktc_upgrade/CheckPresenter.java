package com.qdreamer.ktc_upgrade;

import android.text.TextUtils;

import com.pwong.library.utils.JsonHelper;
import com.pwong.library.utils.LogUtil;
import com.qdreamer.qvoice.QSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * @Author: Pen
 * @Create: 2022-05-27 17:30:41
 * @Email: bug@163.com
 */
public class CheckPresenter implements FactoryHelper.QdreamerArrayFactoryTestListener {

    private WeakReference<CheckFragment> checkFragmentReference;

    private QSession qSession;
    private long sessionValue;
    private FactoryHelper factoryHelper;

    /**
     * 厂测程序的路径在 Factory 中和 MainActivity 中写的，不想统一改
     * 就用 getFilesDir().getAbsolutePath() + "/" 吧
     */
    private String mPath;

    private String mConsistParams = "nil_er=100.0;eq_offse=35000.0;mic_corr_er=200;mic_corr_aver=180.0;mic_energy_er=800.0;mic_energy_aver=700.0;spk_corr_er=200.0;spk_corr_aver=180.0;spk_energy_er=600.0;spk_energy_aver=500.0";

    public CheckPresenter(CheckFragment checkFragment) {
        checkFragmentReference = new WeakReference<>(checkFragment);
        factoryHelper = FactoryHelper.getInstance(checkFragment.getContext());
        factoryHelper.copyRes(R.raw.qvoice);
        qSession = new QSession(checkFragment.getContext());
        sessionValue = qSession.initSession(checkFragment.getString(R.string.QDREAMER_APP_ID), checkFragment.getString(R.string.QDREAMER_APP_KEY));
        qSession.setQSessionCallback(new QSession.QSessionCallBack() {
            @Override
            public void errorCode(String s) {
                LogUtil.INSTANCE.logE(HomeActivity.SOCKET_TAG, "SDK error callback >>> " + s);
            }
        });
    }

    public boolean initEngine(String micBin) {
        synchronized (checkFragmentReference.get()) {
            if (!TextUtils.isEmpty(mPath)) {
                factoryHelper.delete();
                factoryHelper.setListener(null);
                factoryHelper = null;
                factoryHelper = FactoryHelper.getInstance(checkFragmentReference.get().getContext());
            }
            factoryHelper.setListener(this);
            mPath = factoryHelper.getStoragePath();
            String param = "role=consist;cfg=" + mPath + micBin + ";playfn=" + mPath + "biu.wav;syn=1;use_rearrange=0;cache_path=./qvoice;use_logwav=0;use_timer=1;use_thread=1;" + mConsistParams + ";";
            return factoryHelper.init(sessionValue, param);
        }
    }

    public void startEngineCheck(File audioFile) {
        synchronized (checkFragmentReference.get()) {
            factoryHelper.start();
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(audioFile);
                int len;
                byte[] buffer = new byte[4096];
                while ((len = inputStream.read(buffer)) != -1) {
                    if (len < buffer.length) {
                        factoryHelper.feedData(Arrays.copyOfRange(buffer, 0, len));
                    } else {
                        factoryHelper.feedData(buffer);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            factoryHelper.getResutAndStop();
        }
    }

    public void release() {
        if (factoryHelper != null) {
            factoryHelper.setListener(null);
            factoryHelper.delete();
            factoryHelper = null;
        }
        checkFragmentReference.clear();
    }

    private void engineInfo(String json, String info) {
        String channel = JsonHelper.INSTANCE.getJsonString(json, "channel");
        if (checkFragmentReference.get() != null) {
            String result = String.format(info, channel);
            LogUtil.INSTANCE.logE(HomeActivity.SOCKET_TAG, Thread.currentThread().getName() + " ------- " + result);
            checkFragmentReference.get().appendCheckResult(result);
        }
    }

    @Override
    public void onMicNull(String msg) {
        engineInfo(msg, "mic - %s 为空");
    }

    @Override
    public void onMicEqual(String msg) {
        engineInfo(msg, "mic - %s 录音相同");
    }

    @Override
    public void onMicMax(String msg) {
        engineInfo(msg, "mic - %s 破音");
    }

    @Override
    public void onMicCorr(String msg) {
        engineInfo(msg, "mic - %s 相关性差");
    }

    @Override
    public void onMicEnergy(String msg) {
        engineInfo(msg, "mic - %s 能量差");
    }

    @Override
    public void onAecNull(String msg) {
        engineInfo(msg, "spk - %s 为空");
    }

    @Override
    public void onAecEqual(String msg) {
        engineInfo(msg, "spk - %s 相同");
    }

    @Override
    public void onAecMax(String msg) {
        engineInfo(msg, "spk - %s 破音");
    }

    @Override
    public void onAecCorr(String msg) {
        engineInfo(msg, "spk - %s 相关性差");
    }

    @Override
    public void onAecEnergy(String msg) {
        engineInfo(msg, "spk - %s 能量差");
    }
}
