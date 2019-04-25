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

    public static final int SOCKET_TIMEOUT = 20 * 1000;

    public static final String DOWNLOAD_ACTION_NOTIFICATION_CLICK = "DOWNLOAD_ACTION_NOTIFICATION_CLICK";
    public static final String DOWNLOAD_ACTION_TOAST = "DOWNLOAD_ACTION_TOAST";

    public static String SAVE_PATH = "";


    public static final String NOTIFICATION_CHANNEL_ID = "download_channelId";
    public static final String NOTIFICATION_CHANNEL_NAME = "download_mesage";

    private static NotificationChannel channel;

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

    public static NotificationChannel getNotificationChannel() {
        if (channel == null) {
            return createNotificationChannel();
        }
        return channel;
    }


    private static NotificationChannel createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            } else {
                Log.e(TAG, "Android版本低于26，无需创建通知渠道");
            }
        }
        return null;
    }

}
