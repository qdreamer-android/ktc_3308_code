package com.qdreamer.ktc_upgrade;

import com.pwong.library.utils.LogUtil;
import com.xuhao.didi.core.iocore.interfaces.ISendable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @Author: Pen
 * @Create: 2022-05-23 17:40:08
 * @Email: bug@163.com
 */
public class KtcPkgWriteInfo implements ISendable {

    private static final byte[] PREFIX = "start".getBytes(StandardCharsets.UTF_8);

    private final String attachment;
    private final byte[] data;

    public KtcPkgWriteInfo(int type) {
        this.attachment = "{\"type\":" + type + "}";
        this.data = null;
    }

    public KtcPkgWriteInfo(String attachment) {
        this.attachment = attachment;
        this.data = null;
    }

    public KtcPkgWriteInfo(int type, byte[] data) {
        this.attachment = "{\"type\":" + type + "}";
        this.data = data;
    }

    @Override
    public byte[] parse() {
        byte[] body = data == null ? new byte[0] : data;
        int headerLength = 4;
        byte[] bodyBody = attachment.getBytes(StandardCharsets.UTF_8);
        LogUtil.INSTANCE.logE(HomeActivity.SOCKET_TAG, "发送消息 >>>> " + attachment + " --- " + body.length);
        ByteBuffer buffer = ByteBuffer.allocate(PREFIX.length + headerLength + bodyBody.length + body.length);
        buffer.order(HomeActivity.SOCKET_BYTE_ORDER);
        buffer.put(PREFIX);
        buffer.putInt(body.length + bodyBody.length);
        buffer.put(bodyBody);
        buffer.put(body);
        return buffer.array();
    }

}
