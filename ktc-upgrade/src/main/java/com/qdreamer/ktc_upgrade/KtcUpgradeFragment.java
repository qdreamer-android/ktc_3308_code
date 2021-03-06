package com.qdreamer.ktc_upgrade;

import android.view.View;

import com.pwong.library.utils.FastClickUtil;
import com.pwong.library.utils.StorageUtil;
import com.pwong.uiframe.base.BaseFragment;
import com.pwong.uiframe.listener.OnViewClickListener;
import com.pwong.uiframe.utils.ToastUtil;
import com.qdreamer.ktc_upgrade.databinding.FragmentUpgradeBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Author: Pen
 * @Create: 2022-05-24 16:23:01
 * @Email: bug@163.com
 */
public class KtcUpgradeFragment extends BaseFragment<FragmentUpgradeBinding, UpgradeFragmentViewModel>
        implements ISocketConnectListener, OnViewClickListener {

    private static final int VERSION = 1;
    private static final int UPGRADE = 2;
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
        if (getActivity() instanceof HomeActivity) {
            getViewModel().socketConnectSuccess.set(((HomeActivity) getActivity()).getSocketConnected());
        }
        getBinding().setListener(this);

        // Fragment 要初始化，Activity 中必定是申请了文件读写权限的
        String pkgPath = new File(StorageUtil.INSTANCE.getDirPathAfterMkdirs("upgrade"), PKG_NAME).getAbsolutePath();
        getViewModel().upgradePkgPath.set(pkgPath);
    }

    @Override
    public void onConnectChange(boolean isConnect) {
        getViewModel().socketConnectSuccess.set(isConnect);
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
            case R.id.btnVersion: {
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).sendSocketMessage(new KtcPkgWriteInfo(VERSION));
                }
            }
            break;
            case R.id.btnUpgrade: {
                if (getActivity() instanceof HomeActivity && !getViewModel().inUpgrade.get()) {
                    File ktcPkgFile = new File(getViewModel().upgradePkgPath.get());
                    if (ktcPkgFile.exists() && ktcPkgFile.isFile()) {
                        getViewModel().inUpgrade.set(true);
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(ktcPkgFile);
                            byte[] buffer = new byte[(int) ktcPkgFile.length()];
                            inputStream.read(buffer);
                            ((HomeActivity) getActivity()).sendSocketMessage(new KtcPkgWriteInfo(UPGRADE, buffer));
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
                    } else {
                        ToastUtil.INSTANCE.showLongToast(getContext(), "升级包不存在");
                    }
                }
            }
            break;
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
