package com.smasher.downloader.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;


import com.smasher.downloader.DownloadConfig;
import com.smasher.downloader.thread.ThreadPool;
import com.smasher.downloader.util.DownloadUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @author matao
 */
public class IconManager {

    private static final String TAG = "[DL]IconManager";

    private Hashtable<String, Bitmap> icons;

    private volatile static IconManager singleton;

    public static IconManager getSingleton() {
        if (singleton == null) {
            synchronized (IconManager.class) {
                if (singleton == null) {
                    singleton = new IconManager();
                }
            }
        }
        return singleton;
    }


    private IconManager() {
        icons = new Hashtable<>();
    }


    public void loadIcon(final String url) {

        if (TextUtils.isEmpty(url)) {
            return;
        }

        ThreadPool.getInstance(ThreadPool.PRIORITY_MEDIUM).submit(new Runnable() {
            @Override
            public void run() {
                OkHttpClient mClient = new OkHttpClient.Builder()
                        .connectTimeout(DownloadConfig.SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
                        .readTimeout(DownloadConfig.SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
                        .build();


                Bitmap icon = null;
                InputStream iconIs = null;
                try {
                    //获取图标
                    Request iconRequest = new Request.Builder()
                            .url(url)
                            .build();
                    Call iconCall = mClient.newCall(iconRequest);
                    Response iconResponse = iconCall.execute();
                    iconIs = iconResponse.body().byteStream();
                    byte[] iconBytes = DownloadUtils.readInputStream(iconIs);
                    icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
                    if (icon != null) {
                        icons.put(url, icon);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "run: ", e);
                } catch (OutOfMemoryError error) {
                    Log.e(TAG, "run: ", error);
                } finally {
                    if (iconIs != null) {
                        try {
                            iconIs.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }


    public Bitmap getIcon(String url) {

        if (url == null) {
            return null;
        }
        if (icons != null) {
            return icons.get(url);
        }
        return null;
    }

    public void remove(String url) {
        if (icons != null) {
            icons.remove(url);
        }
    }


}
