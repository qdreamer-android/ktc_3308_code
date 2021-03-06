package com.qdreamer.ktc_upgrade;

import android.Manifest;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pwong.library.utils.FastClickUtil;
import com.pwong.library.utils.JsonHelper;
import com.pwong.library.utils.LogUtil;
import com.pwong.library.utils.NetworkUtil;
import com.pwong.uiframe.listener.OnViewClickListener;
import com.pwong.uiframe.utils.ToastUtil;
import com.qdreamer.ktc_upgrade.databinding.ActivityHomeBinding;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClient;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientIOCallback;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientPool;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerShutdown;
import com.xuhao.didi.socket.server.action.ServerActionAdapter;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * @Author: Pen
 * @Create: 2022-05-24 16:04:30
 * @Email: bug@163.com
 */
@RuntimePermissions
public class HomeActivity extends SocketServiceActivity<ActivityHomeBinding, HomeViewModel> implements OnViewClickListener, IClientIOCallback {

    private Fragment mLastFragment;
    private KtcUpgradeFragment upgradeFragment;
    private CheckFragment checkFragment;

    @Override
    protected int layoutView() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        getBinding().setListener(this);
        setNavigationIcon(null);
        initSocketManager(serverActionAdapter);
        HomeActivityPermissionsDispatcher.switchPageWithPermissionCheck(this, 1);
    }

    public boolean getSocketConnected() {
        return getViewModel().socketConnectSuccess.get();
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
            getViewModel().getTitle().set("OTA ??????");
        } else if (index == 1) {
            if (checkFragment == null) {
                checkFragment = CheckFragment.newInstance(getSocketConnected());
                transaction.add(R.id.frameContainer, checkFragment);
            } else {
                transaction.show(checkFragment);
            }
            fragment = checkFragment;
            getViewModel().getTitle().set("??????");
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

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onPermissionDenied() {
        ToastUtil.INSTANCE.showLongToast(this, "?????????????????????(??????3???)????????????");
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

    private final ServerActionAdapter serverActionAdapter = new ServerActionAdapter() {
        @Override
        public void onServerListening(int serverPort) {
            LogUtil.INSTANCE.logE(SOCKET_TAG, "onServerListening  >>>>> " + serverPort + " >>>>> " + isSocketServerLive());
            runOnUiThread(() -> {
                getViewModel().socketConnectSuccess.set(isSocketServerLive());
                ToastUtil.INSTANCE.showLongToast(HomeActivity.this, "Socket ???????????? " + (isSocketServerLive() ? "??????" : "??????"));
                if (upgradeFragment != null) {
                    upgradeFragment.onConnectChange(isSocketServerLive());
                    if (!isSocketServerLive()) {
                        upgradeFragment.showUpgradeResult("????????????????????????????????????");
                    }
                }
                if (checkFragment != null) {
                    checkFragment.onConnectChange(isSocketServerLive());
                    if (!isSocketServerLive()) {
                        checkFragment.showRecordResult("????????????????????????????????????");
                    }
                }
            });
        }

        @Override
        public void onClientConnected(IClient client, int serverPort, IClientPool clientPool) {
            String host = TextUtils.isEmpty(client.getHostIp()) ? client.getHostName() : client.getHostIp();
            LogUtil.INSTANCE.logE(SOCKET_TAG, host + " >>> ????????????");
            runOnUiThread(() -> {
                ToastUtil.INSTANCE.showLongToast(HomeActivity.this, host + " >> ????????????");
            });
            if (client != null) {
                client.addIOCallback(HomeActivity.this);
            }
        }

        @Override
        public void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool) {
            String host = TextUtils.isEmpty(client.getHostIp()) ? client.getHostName() : client.getHostIp();
            LogUtil.INSTANCE.logE(SOCKET_TAG, host + " >>> ????????????");
            runOnUiThread(() -> {
                ToastUtil.INSTANCE.showLongToast(HomeActivity.this, host + " >> ????????????");
            });
            if (client != null) {
                client.removeIOCallback(HomeActivity.this);
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
            runOnUiThread(() -> {
                getViewModel().socketConnectSuccess.set(false);
                ToastUtil.INSTANCE.showLongToast(HomeActivity.this, "Socket ????????????");
                if (upgradeFragment != null) {
                    upgradeFragment.onConnectChange(false);
                    upgradeFragment.showUpgradeResult("????????????????????????????????????");
                }
                if (checkFragment != null) {
                    checkFragment.onConnectChange(false);
                    checkFragment.showRecordResult("????????????????????????????????????");
                }
            });
        }
    };

    private void parseRecMsg(String host, byte[] body) {
        LogUtil.INSTANCE.logE(SOCKET_TAG, "????????? " + host + " ????????? >>>>> " + (body != null ? body.length : 0));
        if (body != null) {
            String recMsg = new String(body, StandardCharsets.UTF_8);
            // type 6 ?????? json ???????????????????????????????????????
            if (recMsg.contains("{\"type\":6}")) {
                synchronized (HomeActivity.this) {
                    int len = "{\"type\":6}".getBytes().length;
                    byte[] audioData = Arrays.copyOfRange(body, len, body.length);
                    LogUtil.INSTANCE.logE("???????????? >>>>>> ???????????? " + audioData.length);
                    // ????????????????????????????????????
                    if (checkFragment != null && audioData.length > 0) {
                        checkFragment.recordAudioData(audioData);
                    }
                }
            } else {
                LogUtil.INSTANCE.logE("???????????? >>>>>> " + recMsg);
                String type = JsonHelper.INSTANCE.getJsonString(recMsg, "type");
                switch (type) {
                    case "-1": {
                        if (upgradeFragment != null) {
                            upgradeFragment.showUpgradeResult("????????????");
                        }
                    }
                    break;
                    case "1": {
                        if (upgradeFragment != null) {
                            upgradeFragment.showUpgradeResult("????????????");
                        }
                    }
                    break;
                    case "2": {
                        if (upgradeFragment != null) {
                            upgradeFragment.showUpgradeResult("????????????");
                        }
                    }
                    break;
                    case "3": {
                        runOnUiThread(() -> {
                            ToastUtil.INSTANCE.showLongToast(HomeActivity.this, JsonHelper.INSTANCE.getJsonString(recMsg, "ver"));
                        });
                    }
                    break;
                    case "4": {
                        if (upgradeFragment != null) {
                            upgradeFragment.showUpgradeResult("????????????");
                        }
                    }
                    break;
                    case "5": {     // ????????????
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
    public void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool) {
        String host = TextUtils.isEmpty(client.getHostIp()) ? client.getHostName() : client.getHostIp();
        parseRecMsg(host, originalData == null ? null : originalData.getBodyBytes());
    }

    @Override
    public void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool) {
    }

    @Override
    protected void onNetStateChange(@NotNull NetworkUtil.WifiStatus state) {
        super.onNetStateChange(state);
        LogUtil.INSTANCE.logI(SOCKET_TAG, "????????????????????????????????? " + state.name());
        if (state != NetworkUtil.WifiStatus.NETWORK_NONE) {
            reconnectSocket();
        } else {
            ToastUtil.INSTANCE.showLongToast(this, "????????????????????????????????????");
            disconnectSocket();
            getViewModel().socketConnectSuccess.set(false);
            if (upgradeFragment != null) {
                upgradeFragment.onConnectChange(false);
            }
            if (checkFragment != null) {
                checkFragment.onConnectChange(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        disconnectSocket();
        super.onDestroy();
    }
}
