package com.qdreamer.ktc_upgrade;

import android.view.View;

import com.pwong.library.utils.FastClickUtil;
import com.pwong.library.utils.StorageUtil;
import com.pwong.uiframe.base.BaseFragment;
import com.pwong.uiframe.listener.OnViewClickListener;
import com.pwong.uiframe.utils.ToastUtil;
import com.qdreamer.ktc_upgrade.databinding.FragmentUpgradeBinding;
import com.qdreamer.ktc_upgrade.serial.SerialPortPresenter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Author: Pen
 * @Create: 2022-05-24 16:23:01
 * @Email: bug@163.com
 */
public class KtcUpgradeFragment extends BaseFragment<FragmentUpgradeBinding, UpgradeFragmentViewModel> implements OnViewClickListener {

    public static final int VERSION = 1;
    private static final int UPGRADE = 2;
    private static final int ALGORITHM_OPEN = 3;
    private static final int ALGORITHM_CLOSE = 4;
    public static final String PKG_NAME = "3308_ota_packge.zip";

    private KtcUpgradeFragment() {
    }

    public static KtcUpgradeFragment newInstance() {
        return new KtcUpgradeFragment();
    }

    @Override
    protected int layoutView() {
        return R.layout.fragment_upgrade;
    }

    @Override
    protected void initView() {
        getBinding().setListener(this);

        // Fragment 要初始化，Activity 中必定是申请了文件读写权限的
        String pkgPath = new File(StorageUtil.INSTANCE.getDirPathAfterMkdirs("upgrade"), PKG_NAME).getAbsolutePath();
        getViewModel().upgradePkgPath.set(pkgPath);
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
            case R.id.btnSwitch: {
                getViewModel().switching.set(true);
                if (getActivity() instanceof HomeActivity) {
                    SerialPortPresenter presenter = ((HomeActivity) getActivity()).getSerialPortPresenter();
                    if (presenter != null) {
                        if (getViewModel().algorithmSwitch.get()) {  // 关闭
                            presenter.sendSerialPortMessage(new KtcPkgWriteInfo(ALGORITHM_CLOSE));
                        } else {    // 开启
                            presenter.sendSerialPortMessage(new KtcPkgWriteInfo(ALGORITHM_OPEN));
                        }
                    } else {
                        getViewModel().switching.set(false);
                    }
                }
            }
            break;
            case R.id.btnVersion: {
                if (getActivity() instanceof HomeActivity) {
                    SerialPortPresenter presenter = ((HomeActivity) getActivity()).getSerialPortPresenter();
                    if (presenter != null) {
                        presenter.sendSerialPortMessage(new KtcPkgWriteInfo(VERSION));
                    }
                }
            }
            break;
            case R.id.btnUpgrade: {
                if (getActivity() instanceof HomeActivity && !getViewModel().inUpgrade.get()) {
                    File ktcPkgFile = new File(getViewModel().upgradePkgPath.get());
                    if (ktcPkgFile.exists() && ktcPkgFile.isFile()) {
                        getViewModel().inUpgrade.set(true);
                        new Thread(() -> {
                            FileInputStream inputStream = null;
                            try {
                                inputStream = new FileInputStream(ktcPkgFile);
                                byte[] buffer = new byte[(int) ktcPkgFile.length()];
                                inputStream.read(buffer);
                                SerialPortPresenter presenter = ((HomeActivity) getActivity()).getSerialPortPresenter();
                                if (presenter != null) {
                                    presenter.sendSerialPortMessage(new KtcPkgWriteInfo(UPGRADE, buffer));
                                } else {
                                    getViewModel().inUpgrade.set(false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                ToastUtil.INSTANCE.showLongToast(getContext(), "升级失败");
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
                        }).start();
                    } else {
                        ToastUtil.INSTANCE.showLongToast(getContext(), "升级包不存在");
                    }
                }
            }
            break;
        }
    }

    public void dealAlgorithm(boolean success) {
        if (success) {
            getActivity().runOnUiThread(() -> {
                getViewModel().switching.set(false);
                getViewModel().algorithmSwitch.set(!getViewModel().algorithmSwitch.get());
                ToastUtil.INSTANCE.showLongToast(getContext(), (getViewModel().algorithmSwitch.get() ? "开启" : "关闭") + "成功");
            });
        } else {
            if (getViewModel().switching.get()) {
                getActivity().runOnUiThread(() -> {
                    getViewModel().switching.set(false);
                    ToastUtil.INSTANCE.showLongToast(getContext(), (getViewModel().algorithmSwitch.get() ? "关闭" : "开启") + "失败");
                });
            } else {
                showUpgradeResult("版本相同");
            }
        }
    }

    public void showUpgradeResult(String result)  {
        if (getViewModel().inUpgrade.get()) {
            getActivity().runOnUiThread(() -> {
                ToastUtil.INSTANCE.showLongToast(getContext(), result);
                getViewModel().inUpgrade.set(false);
            });
        }
    }

}
