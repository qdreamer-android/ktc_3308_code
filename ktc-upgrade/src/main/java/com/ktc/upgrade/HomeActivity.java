package com.ktc.upgrade;

import android.Manifest;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ktc.upgrade.databinding.ActivityHomeBinding;
import com.ktc.upgrade.serial.SerialPortPresenter;
import com.pwong.library.utils.JsonHelper;
import com.pwong.uiframe.base.BaseActivity;
import com.pwong.uiframe.utils.ToastUtil;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class HomeActivity extends BaseActivity<ActivityHomeBinding, HomeViewModel> {

    public static final String SOCKET_TAG = "SOCKET";
    public static final ByteOrder SOCKET_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    private Fragment mLastFragment;
    private KtcUpgradeFragment upgradeFragment;

    private SerialPortPresenter serialPortPresenter;

    private boolean isReady = false;

    @Override
    protected boolean preInit() {
        return !super.preInit();
    }

    @Override
    protected int layoutView() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        setNavigationIcon(null);
        new Handler().postDelayed(() -> {
            HomeActivityPermissionsDispatcher.initSerialPortPresenterWithPermissionCheck(this);
        }, 200);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void initSerialPortPresenter() {
        serialPortPresenter = new SerialPortPresenter(this);

        showLoading("加载串口组件中....", true, true);
        new Thread(() -> {
            while (!isReady) {
                synchronized (this) {
                    if (!isReady) {
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
        ToastUtil.INSTANCE.showLongToast(this, "没有权限哦，亲(づ￣3￣)づ╭❤～");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void parseRecMsg(byte[] body) {
        if (body != null) {
            String recMsg = new String(body, StandardCharsets.UTF_8);
            // type 6 不是 json 格式，后面追加了音频流数据
            if (!recMsg.contains("{\"type\":6}")) {
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
                    case "2": { // 升级版本相同 / 开启关闭算法失败
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
        serialPortPresenter.disconnect();
        super.onDestroy();
    }
}
