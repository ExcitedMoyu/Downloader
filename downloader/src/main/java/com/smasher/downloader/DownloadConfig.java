package com.smasher.downloader;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.smasher.downloader.path.AppPath;


/**
 * @author matao
 */
public class DownloadConfig {

    private static final String TAG = "[DL]DownloadConfig";


    /**
     * 关于线程池的一些配置
     */
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public static final int CORE_POOL_SIZE = Math.max(3, CPU_COUNT / 2);
    public static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;
    public static final long KEEP_ALIVE_TIME = 0L;

    public static final int SOCKET_TIMEOUT = 20 * 1000;


    public static final String DOWNLOAD_ACTION_TOAST = "DOWNLOAD_ACTION_TOAST";

    public static String SAVE_PATH = "";





    /**
     * 初始化
     *
     * @param savePath 默认的保存地址
     */
    public static void init(Application application, String savePath) {
        SAVE_PATH = savePath;
        if (TextUtils.isEmpty(SAVE_PATH)) {
            AppPath.init("Download[DL]");
            SAVE_PATH = AppPath.getDownloadPath(application);
        }
    }





}
