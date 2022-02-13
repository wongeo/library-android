package com.feng.util.net.http;

import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by feng on 2017/6/30.
 */

public class WebClient extends AbsWebCLient {

    public static final String TAG = "WebClient";

    private OnWebClientListener mOnWebClientListener;

    public void setWebClientListener(OnWebClientListener listener) {
        mOnWebClientListener = listener;
    }

    public void downloadData(String url) {

        // 打开一个HttpURLConnection连接
        HttpURLConnection conn = null;

        try {
            mUrl = new URL(url);
            conn = (HttpURLConnection) mUrl.openConnection();
            // 设置连接超时时间
            conn.setConnectTimeout(5 * 1000);
            // 开始连接
            conn.connect();
            // 判断请求是否成功
            if (conn.getResponseCode() == 200) {
                // 获取返回的数据
                byte[] data = readStream(conn.getInputStream());
                Log.i(TAG, "Get方式请求成功，返回数据如下：");
            } else {
                Log.i(TAG, "Get方式请求失败");
            }
        } catch (Exception ex) {

        } finally {
            // 关闭连接
            conn.disconnect();
        }
    }

    public void downloadDataAsync(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadData(url);
            }
        }).start();
    }

    private byte[] readStream(InputStream inputStream) {
        return null;
    }

    public interface OnWebClientListener {

        void onError(String url, Exception ex);

        void onFinish(String url, byte[] resut);
    }
}
