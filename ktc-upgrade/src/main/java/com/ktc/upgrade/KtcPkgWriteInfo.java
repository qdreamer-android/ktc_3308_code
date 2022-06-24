package com.ktc.upgrade;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class KtcPkgWriteInfo implements Serializable {

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

    public byte[] parse() {
        byte[] body = data == null ? new byte[0] : data;
        int headerLength = 4;
        byte[] bodyBody = attachment.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(PREFIX.length + headerLength + bodyBody.length + body.length);
        buffer.order(HomeActivity.SOCKET_BYTE_ORDER);
        buffer.put(PREFIX);
        buffer.putInt(body.length + bodyBody.length);
        buffer.put(bodyBody);
        buffer.put(body);
        return buffer.array();
    }

}
