package com.qdreamer.ktc_upgrade;

import androidx.databinding.ViewDataBinding;

import com.pwong.library.utils.LogUtil;
import com.pwong.uiframe.base.BaseActivity;
import com.pwong.uiframe.base.BaseViewModel;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerManager;
import com.xuhao.didi.socket.server.action.ServerActionAdapter;
import com.xuhao.didi.socket.server.impl.OkServerOptions;

import java.nio.ByteOrder;

/**
 * @Author: Pen
 * @Create: 2022-05-23 17:45:41
 * @Email: bug@163.com
 */
public abstract class SocketServiceActivity<DB extends ViewDataBinding, VM extends BaseViewModel> extends BaseActivity<DB, VM> {

    public static final String SOCKET_TAG = "SOCKET_MSG";
    public static final ByteOrder SOCKET_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    private static final String SOCKET_HOST = "172.16.110.6";
    private static final int SOCKET_PORT = 8080;

    private OkServerOptions options;

    private IServerManager<OkServerOptions> mSocketManager = null;

    protected void initSocketManager(ServerActionAdapter mSocketAdapter) {
        OkServerOptions.Builder builder = new OkServerOptions.Builder();
        builder.setReaderProtocol(new KtcPkgReadProtocol());
        options = builder.build();
        mSocketManager = OkSocket.server(SOCKET_PORT).registerReceiver(mSocketAdapter);
        if (!mSocketManager.isLive()) {
            LogUtil.INSTANCE.logE(SOCKET_TAG, "-------------->>>>>>>>>>>>>> 开始启动服务");
            mSocketManager.listen(options);
        }
    }

    protected boolean isSocketServerLive() {
        return mSocketManager != null && mSocketManager.isLive();
    }

    protected void sendSocketMessage(ISendable sendable) {
        if (mSocketManager != null && mSocketManager.getClientPool() != null) {
            mSocketManager.getClientPool().sendToAll(sendable);
        }
    }

    protected void disconnectSocket() {
        if (isSocketServerLive()) {
            mSocketManager.shutdown();
        }
    }

    protected void reconnectSocket() {
        LogUtil.INSTANCE.logE(SOCKET_TAG, "-------------->>>>>>>>>>>>>> 开始启动服务 ${mSocketManager?.isLive}");
        if (mSocketManager != null && !mSocketManager.isLive()) {
            mSocketManager.listen(options);
        }
    }
}
