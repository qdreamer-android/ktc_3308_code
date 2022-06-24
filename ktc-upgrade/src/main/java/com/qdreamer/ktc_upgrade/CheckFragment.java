package com.qdreamer.ktc_upgrade;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.pwong.library.utils.FastClickUtil;
import com.pwong.library.utils.JsonHelper;
import com.pwong.library.utils.LogUtil;
import com.pwong.library.utils.StorageUtil;
import com.pwong.uiframe.base.BaseFragment;
import com.pwong.uiframe.listener.OnViewClickListener;
import com.pwong.uiframe.utils.ToastUtil;
import com.qdreamer.ktc_upgrade.databinding.FragmentCheckBinding;
import com.qdreamer.ktc_upgrade.serial.SerialPortPresenter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: Pen
 * @Create: 2022-05-24 16:30:35
 * @Email: bug@163.com
 */
public class CheckFragment extends BaseFragment<FragmentCheckBinding, CheckFragmentViewModel> implements OnViewClickListener {

    /**
     * 对应 4、6、8 麦的资源
     */
    private static final String BIN_MIC_4 = "consist-4_1mic.bin";
    private static final String BIN_MIC_6 = "consist-6_2mic.bin";
    private static final String BIN_MIC_8 = "consist-8_2mic.bin";

    private String micConfig;

    private static final int RECORD = 0;
    private static final String VOICE = "biu.wav";

    /**
     * 之前出现过音量太高，播放 biu.wav 崩溃，可以考虑将音量设置成 X%
     */
    private static final int VOICE_PERCENT = 100;

    private static final File AUDIO_FILE = new File(StorageUtil.INSTANCE.getDirPathAfterMkdirs("audio"), "ktc_check.pcm");
    private FileOutputStream mAudioFile = null;

    /**
     * {@code VOICE} 音频时长为 8 秒，因此设置录音时长为 9 秒，稍微多点，确保声音录入进去
     */
    private static final int RECORD_DURATION = 9;

    private MediaPlayer mediaPlayer;

    private CheckPresenter checkPresenter;

    private CheckFragment() {
    }

    public static CheckFragment newInstance() {
        return new CheckFragment();
    }

    @Override
    protected int layoutView() {
        return R.layout.fragment_check;
    }

    @Override
    protected void initView() {
        getBinding().setListener(this);

        checkPresenter = new CheckPresenter(this);

        getBinding().rbtMic4.setChecked(false);
        getBinding().rbtMic6.setChecked(false);
        getBinding().rbtMic8.setChecked(true);
        initEngineAfterChangeMicConfig(BIN_MIC_8);

        getBinding().rgpMic.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbtMic4: {
                    initEngineAfterChangeMicConfig(BIN_MIC_4);
                }
                break;
                case R.id.rbtMic6: {
                    initEngineAfterChangeMicConfig(BIN_MIC_6);
                }
                break;
                case R.id.rbtMic8: {
                    initEngineAfterChangeMicConfig(BIN_MIC_8);
                }
                break;
            }
        });
    }

    private void initEngineAfterChangeMicConfig(String mic) {
        if (!mic.equals(micConfig)) {
            micConfig = mic;
            checkPresenter.initEngine(micConfig);
        }
    }

    @Override
    public void onClick(@NotNull View v) {
        if (FastClickUtil.INSTANCE.isNotFastClick()) {
            onFastClick(v);
        }
    }

    @Override
    public void onFastClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.btnCheck: {
                getViewModel().checkResult.set("");
                if (getActivity() instanceof HomeActivity && !getViewModel().isRecording.get()) {
                    int channel = getMicChannel();
                    try {
                        synchronized (CheckFragment.this) {
                            mAudioFile = null;
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.put("type", RECORD);
                            map.put("record_tm", RECORD_DURATION);
                            map.put("channel", channel);
                            SerialPortPresenter presenter = ((HomeActivity) getActivity()).getSerialPortPresenter();
                            if (presenter != null) {
                                presenter.sendSerialPortMessage(new KtcPkgWriteInfo(JsonHelper.INSTANCE.toJson(map)));
                            }
                        }
                    } catch (Exception e) {
                        ToastUtil.INSTANCE.showLongToast(getContext(), "开始录音失败");
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    private int getMicChannel() {
        switch (micConfig) {
            case BIN_MIC_4:
                return 4;
            case BIN_MIC_6:
                return 6;
            case BIN_MIC_8:
                return 8;
            default:
                throw new InvalidParameterException("mic channel error");
        }
    }

    public void recordStart() {
        getViewModel().isRecording.set(true);
        // Java 不能用协程，GlobalScope.launch(Dispatcher.IO) {}; 那么线程我也不会写了

        synchronized (CheckFragment.this) {
            try {
                if (AUDIO_FILE.exists()) {
                    AUDIO_FILE.delete();
                }
                AUDIO_FILE.createNewFile();
                mAudioFile = new FileOutputStream(AUDIO_FILE);
            } catch (Exception e) {
                LogUtil.INSTANCE.logE(AUDIO_FILE + " 文件异常 >>>> " + e.getMessage());
                e.printStackTrace();
                AUDIO_FILE.delete();
                mAudioFile = null;
            }
        }

        final long recordStartTime = System.currentTimeMillis();

        playBiuVoice();

        new Thread(() -> {
            while (getViewModel().isRecording.get()) {
                try {
                    Thread.sleep(10);
                } catch (Exception ignore) {
                }
                // 多 2 秒 用于等待音频数据结果
                if (System.currentTimeMillis() - recordStartTime > RECORD_DURATION * 1000L + 2 * 1000) {
                    getActivity().runOnUiThread(() -> {
                        getViewModel().isRecording.set(false);
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                    });

                    // 如果音频是一次性上来的，可以将这句代码放在音频结果回调中
                    checkPresenter.startEngineCheck(AUDIO_FILE);
                }
            }
        }).start();
    }

    public void recordAudioData(byte[] audioData) {
        synchronized (CheckFragment.this) {
            if (mAudioFile != null) {
                try {
                    mAudioFile.write(audioData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void playBiuVoice() {
        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
        }
        // 播放 biu 需要控制音量，音量太大可能会崩溃
        AudioMngHelper.getInstance(getContext()).updateVolume(VOICE_PERCENT);
        try {
            mediaPlayer.reset();
            AssetFileDescriptor afd = getContext().getAssets().openFd(VOICE);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.INSTANCE.logE(HomeActivity.SOCKET_TAG, "音频播放失败");
        }
    }

    public void showRecordResult(String result) {
        if (getViewModel().isRecording.get()) {
            getActivity().runOnUiThread(() -> {
                ToastUtil.INSTANCE.showLongToast(getContext(), result);
                getViewModel().isRecording.set(false);
            });
        }
    }

    public void appendCheckResult(String result) {
        if (!TextUtils.isEmpty(result)) {
            if (TextUtils.isEmpty(getViewModel().checkResult.get())) {
                getViewModel().checkResult.set(result);
            } else {
                getViewModel().checkResult.set(getViewModel().checkResult.get() + "\n" + result);
            }
        }
    }

    @Override
    public void onDestroyView() {
        checkPresenter.release();
        if (null != mediaPlayer) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        try {
            // 程序正常销毁时，将本次录音的文件进行删除，因此，需要拉取音频请在应用退出前保存
            if (AUDIO_FILE.exists() && AUDIO_FILE.isFile()) {
                AUDIO_FILE.delete();
            }
        } catch (Exception ignore) {
        }
        super.onDestroyView();
    }
}
