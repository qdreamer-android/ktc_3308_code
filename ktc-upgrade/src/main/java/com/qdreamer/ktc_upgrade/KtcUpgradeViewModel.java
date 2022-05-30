package com.qdreamer.ktc_upgrade;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.pwong.uiframe.base.BaseViewModel;

/**
 * @Author: Pen
 * @Create: 2022-05-23 17:56:16
 * @Email: bug@163.com
 */
public class KtcUpgradeViewModel extends BaseViewModel {

    public ObservableBoolean socketConnectSuccess = new ObservableBoolean(false);
    public ObservableField<String> upgradePkgPath = new ObservableField<>();
    public ObservableBoolean inUpgrade = new ObservableBoolean(false);
    public ObservableBoolean isRecording = new ObservableBoolean(false);
    public ObservableBoolean isPlaying = new ObservableBoolean(false);
    // assets/biu.wav 音频文件是 8 秒，录音时长大概 9 秒
    public ObservableField<String> duration = new ObservableField<>("9");
    public ObservableField<String> channel = new ObservableField<>();

}
