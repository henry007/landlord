package com.hurray.landlord.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: Yizhou He
 * 4/26/12 16:46
 */
public class StringUtil {
    public static String digestMD5(String content) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(content.getBytes());
            return toHexString(algorithm.digest(), "");
        } catch (NoSuchAlgorithmException e) {
            Log.v("Lord.Util", "md5 algorithm not found", e);
            throw new RuntimeException(e);
            // 05-20 09:42:13.697: ERROR/hjhjh(256):
            // 5d5c87e61211ab7a4847f7408f48ac
        }
    }

    private static String toHexString(byte[] bytes, String separator) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex).append(separator);
        }
        return hexString.toString();
    }
}
