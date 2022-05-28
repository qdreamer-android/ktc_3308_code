package com.qdreamer.ktc_upgrade;

import com.xuhao.didi.core.protocol.IReaderProtocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @Author: Pen
 * @Create: 2022-05-23 17:37:06
 * @Email: bug@163.com
 */
public class KtcPkgReadProtocol implements IReaderProtocol {

    private static final int HEAD_LEN = 4;

    @Override
    public int getHeaderLength() {
        return HEAD_LEN;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        if (header == null || header.length < getHeaderLength()) {
            return 0;
        }
        ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOfRange(header, 0, HEAD_LEN));
        bb.order(SocketServiceActivity.SOCKET_BYTE_ORDER);
        return bb.getInt();
    }
}
