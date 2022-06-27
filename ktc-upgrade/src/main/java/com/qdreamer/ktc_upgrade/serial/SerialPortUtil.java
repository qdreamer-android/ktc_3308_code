package com.qdreamer.ktc_upgrade.serial;

import android.util.Log;

import com.pwong.library.utils.LogUtil;
import com.qdreamer.ktc_upgrade.HomeActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import android_serialport_api.SerialPort;

/**
 * @author by AllenJ on 2018/4/20.
 * <p>
 * 通过串口用于接收或发送数据
 */

public class SerialPortUtil {

    private static final String PREFIX = "start";
    private static final byte[] PREFIX_DATA = PREFIX.getBytes(StandardCharsets.UTF_8);

    private String TAG = "qtk";

    private SerialPort serialPort = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private ReceiveThread mReceiveThread = null;

    private OnSerialReadContentListener mOnSerialReadContentListener;

    /**
     * 打开串口，接收数据
     * 通过串口，接收单片机发送来的数据
     */
    public void openSerialPort(OnSerialReadContentListener listener) {
        mOnSerialReadContentListener = listener;
        try {
            // 需要确保临时文件存在，
//            File deviceFile = new File(StorageUtil.INSTANCE.getCACHE_PATH_ROOT(), "serialPort.log");

//            Process exec = Runtime.getRuntime().exec("ls /dev/ttyACM0");
            // * 不行
//            Process exec = Runtime.getRuntime().exec("ls /dev/ttyACM*");
//            InputStream inputStream = null;
//            try {
//                exec.waitFor();
//                inputStream = exec.getInputStream();
//                int len;
//                byte[] buffer = new byte[1024];
//                byte[] allBuffer = new byte[0];
//                while ((len = inputStream.read(buffer)) != -1) {
//                    if (len < buffer.length) {
//                        byte[] copyOfRange = Arrays.copyOfRange(buffer, 0, len);
//                        byte[] tempBuffer = new byte[allBuffer.length + len];
//                        System.arraycopy(allBuffer, 0, tempBuffer, 0, allBuffer.length);
//                        System.arraycopy(copyOfRange, 0, tempBuffer, allBuffer.length, copyOfRange.length);
//                        allBuffer = tempBuffer;
//                    } else {
//                        byte[] tempBuffer = new byte[allBuffer.length + buffer.length];
//                        System.arraycopy(allBuffer, 0, tempBuffer, 0, allBuffer.length);
//                        System.arraycopy(buffer, 0, tempBuffer, allBuffer.length, buffer.length);
//                        allBuffer = tempBuffer;
//                    }
//                }
//                String result = new String(allBuffer, StandardCharsets.UTF_8);
//                LogUtil.INSTANCE.logE("--------->>>>>>>>>>>>> " + result + " <<<<<<<<");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//            }

//            File deviceFile = new File("/dev/ttyACM0");
            int count = 0;
            File deviceFile;
            while (!(deviceFile = new File("/dev/ttyACM" + count)).exists()) {
                count ++;
                if (count > 100) {
                    LogUtil.INSTANCE.logE("----------- 不存在 ttyACM0 - " + count);
                    return;
                }
            }

            LogUtil.INSTANCE.logE("===== " + deviceFile.getAbsolutePath() + " ----- " + deviceFile.exists());
            serialPort = new SerialPort(deviceFile, 115200, 0);
//            LogUtil.INSTANCE.logE("------------> " + deviceFile.getAbsolutePath());
//            if (!deviceFile.exists() || !deviceFile.isFile()) {be
//                deviceFile.createNewFile();
//            }
//            serialPort = new SerialPort(deviceFile, 115200, 0);
            //调用对象SerialPort方法，获取串口中"读和写"的数据流
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            LogUtil.INSTANCE.logE("----> " + (inputStream == null) + " ------> " + (outputStream == null));
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.INSTANCE.logE("-----------------------> " + e);
        }
        getSerialPort();
    }

    /**
     * 关闭串口
     * 关闭串口中的输入输出流
     */
    public void closeSerialPort() {
        mOnSerialReadContentListener = null;
        Log.i(TAG, "关闭串口");
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                inputStream = null;
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                outputStream = null;
            }
        }
    }

    /**
     * 发送数据
     * 通过串口，发送数据到单片机
     *
     * @param data 要发送的数据
     */
    public void sendSerialPort(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        try {
            Log.i(TAG, "serial write end 111...:" + data.length);
            outputStream.write(data);
            Log.i(TAG, "serial write end 222...:" + data);
            outputStream.flush();
            Log.i(TAG, "serial flush end 333...:" + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSerialPort() {
        if (mReceiveThread == null) {
            mReceiveThread = new ReceiveThread();
        }
        mReceiveThread.start();
    }

    /**
     * 接收串口数据的线程
     * 1、先获取头前缀 start{@link PREFIX} 数据
     * 2、紧接着获取4个字节，用于计算后面的数据长度 len
     * 3、根据第二步的长度，接着再获取对应 len 的数据
     */
    private class ReceiveThread extends Thread {

        /**
         * 标识;-1 表示等待 start,0 表示已经获取到 start
         */
        private int dataLen = -1;

        ByteBuffer allDataBuffer = null;

        private int allSize = 0;

        private int allDataNum = 0;

        byte[] readData = new byte[2048];

        @Override
        public void run() {
            super.run();
            //条件判断，只要条件为true，则一直执行这个线程
            while (inputStream != null) {
                try {
                    int size = inputStream.read(readData);
                    if (size > 0) {
                        LogUtil.INSTANCE.logE("串口接收消息开始 >>>>>> dataLen: " + dataLen + " -----> readLen: " + size + " ----- " + allDataNum);
                        if (dataLen < 0) {
                            byte[] prefix = Arrays.copyOfRange(readData, 0, PREFIX_DATA.length);
                            if (PREFIX.equals(new String(prefix, StandardCharsets.UTF_8))) {
                                allDataNum = 0;
                                LogUtil.INSTANCE.logE("串口接收消息开始 ------- 数据量是否满足 " + (size >= PREFIX_DATA.length + 4) + " ----- 上次消息总包长： " + allSize);
                                allSize = 0;
                                if (size > PREFIX_DATA.length) {
                                    byte[] head = Arrays.copyOfRange(readData, PREFIX_DATA.length, PREFIX_DATA.length + 4);
                                    ByteBuffer byteBuffer = ByteBuffer.wrap(head);
                                    byteBuffer.order(HomeActivity.SOCKET_BYTE_ORDER);
                                    int bodyLen = byteBuffer.getInt();
                                    LogUtil.INSTANCE.logE("-111111 串口开始 --- 消息长度 >>>>>> " + bodyLen);

                                    if (size >= PREFIX_DATA.length + 4 + bodyLen) {
                                        byte[] bodyData = Arrays.copyOfRange(readData, PREFIX_DATA.length + 4, PREFIX_DATA.length + 4 + bodyLen);
                                        if (mOnSerialReadContentListener != null) {
                                            mOnSerialReadContentListener.onReadContent(bodyData);
                                        }
                                        allSize += bodyData.length;
                                    } else {
                                        dataLen = PREFIX_DATA.length + 4 + bodyLen - size;
                                        byte[] bodyData = Arrays.copyOfRange(readData, PREFIX_DATA.length + 4, size);
                                        allDataBuffer = ByteBuffer.allocate(bodyLen);
                                        allDataBuffer.order(HomeActivity.SOCKET_BYTE_ORDER);
                                        allDataBuffer.put(bodyData);
                                        allSize += size;
                                    }
                                } else {
                                    dataLen = 0;
                                }
                            }
                        } else if (dataLen == 0) {
                            byte[] head = Arrays.copyOfRange(readData, 0, 4);
                            ByteBuffer byteBuffer = ByteBuffer.wrap(head);
                            byteBuffer.order(HomeActivity.SOCKET_BYTE_ORDER);
                            int bodyLen = byteBuffer.getInt();
                            LogUtil.INSTANCE.logE("000000 串口开始 --- 消息长度 >>>>>> " + bodyLen);
                            if (size >= 4 + bodyLen) {
                                byte[] bodyData = Arrays.copyOfRange(readData, 4, 4 + bodyLen);
                                allSize += size;
                                if (mOnSerialReadContentListener != null) {
                                    mOnSerialReadContentListener.onReadContent(bodyData);
                                }
                                dataLen = -1;
                            } else {
                                allSize += size;
                                dataLen = 4 + bodyLen - size;
                                byte[] bodyData = Arrays.copyOfRange(readData, 4, size);
                                allDataBuffer = ByteBuffer.allocate(bodyLen);
                                allDataBuffer.order(HomeActivity.SOCKET_BYTE_ORDER);
                                allDataBuffer.put(bodyData);
                            }
                        } else {
                            LogUtil.INSTANCE.logE("===================== -------- " + allDataNum);
                            if (size < dataLen) {
                                allSize += size;
                                byte[] bodyData = Arrays.copyOfRange(readData, 0, size);
                                allDataBuffer.put(bodyData);
                                dataLen = dataLen - size;
                            } else {
                                allSize += size;
                                byte[] bodyData = Arrays.copyOfRange(readData, 0, dataLen);
                                allDataBuffer.put(bodyData);
                                if (mOnSerialReadContentListener != null) {
                                    mOnSerialReadContentListener.onReadContent(allDataBuffer.array());
                                }
                                dataLen = -1;
                                allDataBuffer = null;
                            }
                        }
                        allDataNum += size;
                    } else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.INSTANCE.logE("串口接收消息异常 -------- " + e);
                }
            }
        }
    }

    public interface OnSerialReadContentListener {
        void onReadContent(byte[] body);
    }

}
