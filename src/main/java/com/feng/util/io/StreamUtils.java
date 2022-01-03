package com.feng.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {

    public static String inputStream2String(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inputStream.read(b)) != -1) {
                byteArrayOutputStream.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            byteArrayOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(byteArrayOutputStream);
        }

        return byteArrayOutputStream.toString();
    }

    public static void close(OutputStream outputStream) {
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
