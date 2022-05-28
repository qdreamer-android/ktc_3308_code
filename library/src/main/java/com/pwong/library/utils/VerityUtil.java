package com.pwong.library.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class VerityUtil {

    public static String str2MD5(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(content.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFileMD5(final File file) {
        if (file == null || !file.exists()) {
            return "";
        }

        String value = null;
        FileInputStream in;
        final byte[] buffer = new byte[1024];
        int len;
        try {
            in = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                md5.update(buffer, 0, len);
            }
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
            if (value.length() < 32) {
                int count = 32 - value.length();
                value = String.format("%0" + count + "d", 0) + value;
            }
            in.close();
        } catch (Exception e) {
            LogUtil.INSTANCE.logE("000000000000000000000000  " + e.toString());
            e.printStackTrace();
        }
        return value;
    }

}
