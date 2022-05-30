package com.qdreamer.ktc_upgrade;

import android.Manifest;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.pwong.library.utils.FastClickUtil;
import com.pwong.library.utils.JsonHelper;
import com.pwong.library.utils.LogUtil;
import com.pwong.library.utils.NetworkUtil;
import com.pwong.library.utils.StorageUtil;
import com.pwong.uiframe.listener.OnViewClickListener;
import com.pwong.uiframe.utils.ToastUtil;
import com.qdreamer.ktc_upgrade.databinding.ActivityKtcUpgradeBinding;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClient;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientIOCallback;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientPool;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerShutdown;
import com.xuhao.didi.socket.server.action.ServerActionAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * @Author: Pen
 * @Create: 2022-05-23 17:57:57
 * @Email: bug@163.com
 */
@RuntimePermissions
public class KtcUpgradeActivity extends SocketServiceActivity<ActivityKtcUpgradeBinding, KtcUpgradeViewModel> implements OnViewClickListener, IClientIOCallback {

    private static final int RECORD = 0;
    private static final int VERSION = 1;
    private static final int UPGRADE = 2;
    private static final String PKG_NAME = "3308_ota_packge.zip";
    private static final String VOICE = "biu.wav";

    /**
     * 之前出现过音量太高，播放 biu.wav 崩溃，暂时将音量设置成 50%
     */
    private static final int VOICE_PERCENT = 50;

    private static final String AUDIO_DIR = StorageUtil.INSTANCE.getDirPathAfterMkdirs("audio");
    private FileOutputStream mAudioFile = null;

    private int audioDuration = 0;
    private long audioStartTime = 0;

    private MediaPlayer mediaPlayer;

    @Override
    protected int layoutView() {
        return R.layout.activity_ktc_upgrade;
    }

    @Override
    protected void initView() {
        if (getBinding() != null) {
            getBinding().setListener(this);
        }
        setNavigationIcon(null);
        KtcUpgradeActivityPermissionsDispatcher.applyPermissionWithPermissionCheck(this);
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
            case R.id.btnUpgrade: {
                if (!getViewModel().inUpgrade.get()) {
                    File ktcPkgFile = new File(getViewModel().upgradePkgPath.get());
                    if (ktcPkgFile.exists() && ktcPkgFile.isFile()) {
                        getViewModel().inUpgrade.set(true);
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(ktcPkgFile);
                            byte[] buffer = new byte[(int) ktcPkgFile.length()];
                            inputStream.read(buffer);
                            sendSocketMessage(new KtcPkgWriteInfo(UPGRADE, buffer));
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.INSTANCE.showLongToast(this, "读取升级包文件失败");
                            getViewModel().inUpgrade.set(false);
                        } finally {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        ToastUtil.INSTANCE.showLongToast(this, "升级包不存在");
                    }
                }
            }
            break;
            case R.id.btnVersion: {
//                playBiuVoice();
                sendSocketMessage(new KtcPkgWriteInfo(VERSION));
            }
            break;
            case R.id.btnRecord: {
                audioDuration = 0;
                if (!getViewModel().isRecording.get()) {
                    if (TextUtils.isEmpty(getViewModel().duration.get())) {
                        ToastUtil.INSTANCE.showLongToast(this, "请输入录音时长");
                    } else if (TextUtils.isEmpty(getViewModel().channel.get())) {
                        ToastUtil.INSTANCE.showLongToast(this, "请输入 mic 数量");
                    } else {
                        try {
                            int duration = Integer.parseInt(getViewModel().duration.get());
                            int channel = Integer.parseInt(getViewModel().channel.get());
                            if (duration > 0 && channel > 0) {
                                synchronized (KtcUpgradeActivity.this) {
                                    mAudioFile = null;
                                    audioDuration = duration * 1000;
                                    Map<String, Integer> map = new LinkedHashMap<>();
                                    map.put("type", RECORD);
                                    map.put("record_tm", duration);
                                    map.put("channel", channel);
                                    sendSocketMessage(new KtcPkgWriteInfo(JsonHelper.INSTANCE.toJson(map)));
                                }
                            } else {
                                ToastUtil.INSTANCE.showLongToast(this, "请输入有效的时长和 mic 数量");
                            }
                        } catch (Exception e) {
                            ToastUtil.INSTANCE.showLongToast(this, "开始录音失败");
                            e.printStackTrace();
                        }
                    }
                }
            }
            default:
                break;
        }
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void applyPermission() {
        String pkgPath = new File(StorageUtil.INSTANCE.getDirPathAfterMkdirs("upgrade"), PKG_NAME).getAbsolutePath();
        getViewModel().upgradePkgPath.set(pkgPath);
        initSocketManager(serverActionAdapter);
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPermissionDenied() {
        ToastUtil.INSTANCE.showLongToast(this, "没有权限哦，亲(づ￣3￣)づ╭❤～");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        KtcUpgradeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onNetStateChange(@NotNull NetworkUtil.WifiStatus state) {
        super.onNetStateChange(state);
        LogUtil.INSTANCE.logI(SOCKET_TAG, "网络连接状态发生改变： " + state.name());
        if (state != NetworkUtil.WifiStatus.NETWORK_NONE) {
            reconnectSocket();
        } else {
            disconnectSocket();
            getViewModel().socketConnectSuccess.set(false);
            ToastUtil.INSTANCE.showLongToast(this, "网络连接断开，请检查网络");
        }
    }

    @Override
    protected void onDestroy() {
        disconnectSocket();
        if (null != mediaPlayer) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    private final ServerActionAdapter serverActionAdapter = new ServerActionAdapter() {
        @Override
        public void onServerListening(int serverPort) {
            LogUtil.INSTANCE.logE(SOCKET_TAG, "onServerListening  >>>>> " + serverPort + " >>>>> " + isSocketServerLive());
            getViewModel().socketConnectSuccess.set(isSocketServerLive());
            if (!isSocketServerLive()) {
                runOnUiThread(() -> {
                    if (getViewModel().inUpgrade.get()) {
                        ToastUtil.INSTANCE.showLongToast(KtcUpgradeActivity.this, "服务连接断开，请重新升级");
                        getViewModel().inUpgrade.set(false);
                    } else if (getViewModel().isRecording.get()) {
                        ToastUtil.INSTANCE.showLongToast(KtcUpgradeActivity.this, "服务连接断开，请重新录音");
                        getViewModel().isRecording.set(false);
                    }
                });
            }
        }

        @Override
        public void onClientConnected(IClient client, int serverPort, IClientPool clientPool) {
            String host = TextUtils.isEmpty(client.getHostIp()) ? client.getHostName() : client.getHostIp();
            LogUtil.INSTANCE.logE(SOCKET_TAG, host + " >>> 连接成功");
            runOnUiThread(() -> {
                ToastUtil.INSTANCE.showLongToast(KtcUpgradeActivity.this, host + " >> 连接成功");
            });
            if (client != null) {
                client.addIOCallback(KtcUpgradeActivity.this);
            }
        }

        @Override
        public void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool) {
            String host = TextUtils.isEmpty(client.getHostIp()) ? client.getHostName() : client.getHostIp();
            LogUtil.INSTANCE.logE(SOCKET_TAG, host + " >>> 连接断开");
            runOnUiThread(() -> {
                ToastUtil.INSTANCE.showLongToast(KtcUpgradeActivity.this, host + " >> 连接断开");
            });
            if (client != null) {
                client.removeIOCallback(KtcUpgradeActivity.this);
            }
        }

        @Override
        public void onServerWillBeShutdown(int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable) {
            LogUtil.INSTANCE.logE(SOCKET_TAG, "onServerWillBeShutdown  >>>>> " + serverPort + " >>>>> " + isSocketServerLive());
            if (shutdown != null) {
                shutdown.shutdown();
            }
        }

        @Override
        public void onServerAlreadyShutdown(int serverPort) {
            LogUtil.INSTANCE.logE(SOCKET_TAG, "onServerAlreadyShutdown  >>>>> " + serverPort + " >>>>> " + isSocketServerLive());
            getViewModel().socketConnectSuccess.set(false);
            runOnUiThread(() -> {
                if (getViewModel().inUpgrade.get()) {
                    ToastUtil.INSTANCE.showLongToast(KtcUpgradeActivity.this, "服务连接断开，请重新升级");
                    getViewModel().inUpgrade.set(false);
                } else if (getViewModel().isRecording.get()) {
                    ToastUtil.INSTANCE.showLongToast(KtcUpgradeActivity.this, "服务连接断开，请重新录音");
                    getViewModel().isRecording.set(false);
                }
            });
        }
    };

    private void parseRecMsg(byte[] body) {
        if (body != null) {
            String recMsg = new String(body, StandardCharsets.UTF_8);
            // type 6 不是 json 格式，后面追加了音频流数据
            if (recMsg.contains("{\"type\":6}")) {
                synchronized (KtcUpgradeActivity.this) {
                    int len = "{\"type\":6}".getBytes().length;
                    byte[] audioData = Arrays.copyOfRange(body, len, body.length);
                    LogUtil.INSTANCE.logE("接收消息 >>>>>> 音频消息 " + audioData.length + " >>>> " + getViewModel().isRecording.get() + " >>>> " + (mAudioFile != null));
                    // 音频是录音结束之后返回的
                    if (/*getViewModel().isRecording.get() && */mAudioFile != null) {
                        try {
                            mAudioFile.write(audioData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                LogUtil.INSTANCE.logE("接收消息 >>>>>> " + recMsg);
                String type = JsonHelper.INSTANCE.getJsonString(recMsg, "type");
                switch (type) {
                    case "-1": {
                        runOnUiThread(() -> {
                            ToastUtil.INSTANCE.showLongToast(KtcUpgradeActivity.this, "升级失败");
                            getViewModel().inUpgrade.set(false);
                        });
                    }
                    break;
                    case "1": {
                        runOnUiThread(() -> {
                            ToastUtil.INSTANCE.showLongToast(KtcUpgradeActivity.this, "更新成功");
                            getViewModel().inUpgrade.set(false);
                        });
                    }
                    break;
                    case "2": {
                        runOnUiThread(() -> {
                            ToastUtil.INSTANCE.showLongToast(KtcUpgradeActivity.this, "版本相同");
                            getViewModel().inUpgrade.set(false);
                        });
                    }
                    break;
                    case "3": {
                        runOnUiThread(() -> {
                            ToastUtil.INSTANCE.showLongToast(KtcUpgradeActivity.this, JsonHelper.INSTANCE.getJsonString(recMsg, "ver"));
                        });
                    }
                    break;
                    case "4": {
                        runOnUiThread(() -> {
                            ToastUtil.INSTANCE.showLongToast(KtcUpgradeActivity.this, "解压失败");
                            getViewModel().inUpgrade.set(false);
                        });
                    }
                    break;
                    case "5": {     // 开始录音
                        getViewModel().isRecording.set(true);
                        // Java 不能用协程，GlobalScope.launch(Dispatcher.IO) {}; 那么线程我也不会写了

    //                    String audioName = TimeUtil.INSTANCE.stampToDate("yyyyMMddHHmmss") + "-" + getViewModel().channel.get() + ".pcm";
    //                    File audioFile = new File(AUDIO_DIR, audioName);

                        /*
                         此处被坑，厂测程序需要用到 Socket 录音的音频文件，所以将两个 app 的音频文件保持同一个路径，
                         Socket 录音完成之后，打开厂测的 App 进行检测
                         */
                        File audioFile = new File("/sdcard/mul.pcm");
                        try {
                            if (audioFile.exists()) {
                                audioFile.delete();
                            }
                            audioFile.createNewFile();
                            mAudioFile = new FileOutputStream(audioFile);
                        } catch (Exception e) {
                            LogUtil.INSTANCE.logE("mul.pcm 写入异常 >>>> " + e.getMessage());
                            e.printStackTrace();
                            audioFile.delete();
                            mAudioFile = null;
                        }
                        audioStartTime = System.currentTimeMillis();

                        playBiuVoice();

                        new Thread(() -> {
                            while (getViewModel().isRecording.get()) {
                                try {
                                    Thread.sleep(10);
                                } catch (Exception ignore) {
                                }
                                if (System.currentTimeMillis() - audioStartTime > audioDuration) {
                                    synchronized (KtcUpgradeActivity.this) {
                                        runOnUiThread(() -> {
                                            getViewModel().isRecording.set(false);
                                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                                mediaPlayer.stop();
                                            }
                                        });
                                    }
                                }
                            }
                        }).start();
                    }
                    break;
                    default:
                        runOnUiThread(() -> {
                            ToastUtil.INSTANCE.showLongToast(KtcUpgradeActivity.this, type);
                        });
                        break;
                }
            }
        }
    }

    private void playBiuVoice() {
        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
        }
        // TODO 播放 biu 需要控制音量，音量太大可能会崩溃
        AudioMngHelper.getInstance(this).updateVolume(VOICE_PERCENT);
        try {
            mediaPlayer.reset();
            AssetFileDescriptor afd = getAssets().openFd(VOICE);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.INSTANCE.logE(SOCKET_TAG, "音频播放失败");
        }
    }

    @Override
    public void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool) {
        String host = TextUtils.isEmpty(client.getHostIp()) ? client.getHostName() : client.getHostIp();
        LogUtil.INSTANCE.logE(SOCKET_TAG, "接收到 " + host + " 的消息 >>>>> ");
        parseRecMsg(originalData == null ? null : originalData.getBodyBytes());
    }

    @Override
    public void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool) {
    }
}
