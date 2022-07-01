package com.qdreamer.ktc_upgrade;

import android.Manifest;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pwong.library.utils.FastClickUtil;
import com.pwong.library.utils.JsonHelper;
import com.pwong.library.utils.LogUtil;
import com.pwong.uiframe.base.BaseActivity;
import com.pwong.uiframe.listener.OnViewClickListener;
import com.pwong.uiframe.utils.ToastUtil;
import com.qdreamer.ktc_upgrade.databinding.ActivityHomeBinding;
import com.qdreamer.ktc_upgrade.serial.SerialPortPresenter;
import com.qdreamer.ktc_upgrade.usbhid.UsbHid;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class HomeActivity extends BaseActivity<ActivityHomeBinding, HomeViewModel> implements OnViewClickListener {

    public static final String SOCKET_TAG = "SOCKET";
    public static final ByteOrder SOCKET_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    private Fragment mLastFragment;
    private KtcUpgradeFragment upgradeFragment;
    private CheckFragment checkFragment;

    private SerialPortPresenter serialPortPresenter;

    private boolean isReady = false;

    private UsbHid usbHid;

    @Override
    protected int layoutView() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        getBinding().setListener(this);
        setNavigationIcon(null);
//        initSocketManager(serverActionAdapter);

        new Handler().postDelayed(() -> {
            HomeActivityPermissionsDispatcher.initSerialPortPresenterWithPermissionCheck(this);
        }, 200);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void initSerialPortPresenter() {
        usbHid = new UsbHid(this, 0x2207, 0x0019);
        usbHid.setListener(new UsbHid.Listener() {
            @Override
            public void onNewData(@NotNull byte[] data) {
                LogUtil.INSTANCE.logE("initSerialPortPresenter >> onNewData ------------- " + data.length);
            }

            @Override
            public void onRunError(@NotNull Exception e) {
                LogUtil.INSTANCE.logE("initSerialPortPresenter >> onRunError ------------- " + e);
            }

            @Override
            public void onStateChanged(@NotNull UsbHid.State state) {
                getBinding().tgBtnHID.setEnabled(state == UsbHid.State.Working);
                if (state == UsbHid.State.Working) {
                    ToastUtil.INSTANCE.showLongToast(HomeActivity.this, "USB HID 可用");
                } else {
                    ToastUtil.INSTANCE.showLongToast(HomeActivity.this, "USB HID 不可用，当前状态为 " + state.name());
                }
            }
        });
        usbHid.openDevice();

        getBinding().tgBtnHID.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendUSBHidMsg(isChecked);
        });
    }

    private synchronized void sendUSBHidMsg(boolean switchHID) {
        if (switchHID) {
            byte[] buffer = new byte[]{0x59, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            LogUtil.INSTANCE.logE("开启 HID >>>>>>>>>>>>>>> " + buffer[0] + " >> " + buffer[1]);
            usbHid.write(buffer, null);
            loopSerialPortMsg();
        } else {
            byte[] buffer = new byte[]{0x59, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            LogUtil.INSTANCE.logE("关闭 HID >>>>>>>>>>>>>>> " + buffer[0] + " >> " + buffer[1]);
            usbHid.write(buffer, null);
            if (serialPortPresenter != null) {
                serialPortPresenter.disconnect();
                serialPortPresenter = null;
                isReady = false;
            }
        }
    }

    private void loopSerialPortMsg() {
        LogUtil.INSTANCE.logE("------------> loading.......");
        if (serialPortPresenter == null) {
            serialPortPresenter = new SerialPortPresenter(this);
        }
        showLoading("加载串口组件中....", false, false);
        isReady = false;
        new Thread(() -> {
            while (!isReady && serialPortPresenter != null) {
                synchronized (this) {
                    LogUtil.INSTANCE.logE("------------> loading....... " + !isReady + " ---- " + (serialPortPresenter != null));
                    if (!isReady && serialPortPresenter != null) {
                        serialPortPresenter.sendSerialPortMessage(new KtcPkgWriteInfo(KtcUpgradeFragment.VERSION));
                    } else {
                        return;
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void switchPage(int index) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        if (index == 0) {
            if (upgradeFragment == null) {
                upgradeFragment = KtcUpgradeFragment.newInstance();
                transaction.add(R.id.frameContainer, upgradeFragment);
            } else {
                transaction.show(upgradeFragment);
            }
            fragment = upgradeFragment;
            getViewModel().getTitle().set("OTA 升级");
        } else if (index == 1) {
            if (checkFragment == null) {
                checkFragment = CheckFragment.newInstance();
                transaction.add(R.id.frameContainer, checkFragment);
            } else {
                transaction.show(checkFragment);
            }
            fragment = checkFragment;
            getViewModel().getTitle().set("厂测");
        }
        if (fragment != null && mLastFragment != null) {
            if (mLastFragment == fragment) {
                return;
            }
            transaction.hide(mLastFragment);
        }
        getViewModel().position.set(index);
        mLastFragment = fragment;
        transaction.commit();
    }

    public SerialPortPresenter getSerialPortPresenter() {
        return serialPortPresenter;
    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onPermissionDenied() {
        LogUtil.INSTANCE.logE("------------> 白屏？？？？");
        ToastUtil.INSTANCE.showLongToast(this, "没有权限哦，亲(づ￣3￣)づ╭❤～");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onClick(@NotNull View v) {
        if (FastClickUtil.INSTANCE.isNotFastClick()) {
            onFastClick(v);
        }
    }

    @Override
    public void onFastClick(@NotNull View v) {
        if (!isReady) {
            return;
        }
        switch (v.getId()) {
            case R.id.linearUpgrade: {
                HomeActivityPermissionsDispatcher.switchPageWithPermissionCheck(this, 0);
            }
            break;
            case R.id.linearCheck: {
                 HomeActivityPermissionsDispatcher.switchPageWithPermissionCheck(this, 1);
            }
            break;
            default:
                break;
        }
    }

    public void parseRecMsg(byte[] body) {
        LogUtil.INSTANCE.logE(SOCKET_TAG, "接收消息 >>>>> " + (body != null ? body.length : 0));
        if (body != null) {
            String recMsg = new String(body, StandardCharsets.UTF_8);
            // type 6 不是 json 格式，后面追加了音频流数据
            if (recMsg.contains("{\"type\":6}")) {
                synchronized (HomeActivity.this) {
                    int len = "{\"type\":6}".getBytes().length;
                    byte[] audioData = Arrays.copyOfRange(body, len, body.length);
                    LogUtil.INSTANCE.logE("接收消息 >>>>>> 音频消息 " + audioData.length);
                    // 音频是录音结束之后返回的
                    if (checkFragment != null && audioData.length > 0) {
                        checkFragment.recordAudioData(audioData);
                    }
                }
            } else {
                LogUtil.INSTANCE.logE("接收消息 >>>>>> " + recMsg);
                String type = JsonHelper.INSTANCE.getJsonString(recMsg, "type");
                switch (type) {
                    case "-1": {
                        if (upgradeFragment != null) {
                            upgradeFragment.showUpgradeResult("升级失败");
                        }
                    }
                    break;
                    case "0": {
                        if (upgradeFragment != null) {
                            upgradeFragment.dealAlgorithm(true);
                        }
                    }
                    break;
                    case "1": {
                        if (upgradeFragment != null) {
                            upgradeFragment.showUpgradeResult("更新成功");
                        }
                    }
                    break;
                    case "2": {
                        if (upgradeFragment != null) {
                            upgradeFragment.showUpgradeResult("版本相同");
                        }
                    }
                    break;
                    case "-2": {
                        if (upgradeFragment != null) {
                            upgradeFragment.dealAlgorithm(false);
                        }
                    }
                    break;
                    case "3": {
                        runOnUiThread(() -> {
                            if (isReady) {
                                ToastUtil.INSTANCE.showLongToast(HomeActivity.this, JsonHelper.INSTANCE.getJsonString(recMsg, "ver"));
                            } else {
                                synchronized (this) {
                                    HomeActivityPermissionsDispatcher.switchPageWithPermissionCheck(this, 0);
                                    hideLoading();
                                    isReady = true;
                                }
                            }
                        });
                    }
                    break;
                    case "4": {
                        if (upgradeFragment != null) {
                            upgradeFragment.showUpgradeResult("解压失败");
                        }
                    }
                    break;
                    case "5": {     // 开始录音
                        if (checkFragment != null) {
                            runOnUiThread(() -> checkFragment.recordStart());
                        }
                    }
                    break;
                    default:
                        runOnUiThread(() -> {
                            ToastUtil.INSTANCE.showLongToast(HomeActivity.this, type);
                        });
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
//        disconnectSocket();
        serialPortPresenter.disconnect();
        serialPortPresenter = null;
        usbHid.closeDevice();
        super.onDestroy();
    }
}
