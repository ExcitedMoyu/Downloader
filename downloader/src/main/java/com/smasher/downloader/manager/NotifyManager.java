package com.smasher.downloader.manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;

import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.smasher.downloader.R;
import com.smasher.downloader.entity.DownloadInfo;


/**
 * 下载通知栏管理
 *
 * @author matao
 * @date 2017/8/15
 */
public class NotifyManager {
    public static final String DOWNLOAD_ACTION_TOAST = "DOWNLOAD_ACTION_TOAST";

    private static final String TAG = "[DL]Notification";
    private SparseArray<RemoteViews> remoteViews = new SparseArray<>();

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private NotificationChannel channel;
    private static final String NOTIFICATION_CHANNEL_ID = "download_channelId";
    private static final String DOWNLOAD_ACTION_NOTIFICATION_CLICK = "DOWNLOAD_ACTION_NOTIFICATION_CLICK";

    private String notificationChannelName;
    private String packageName;

    private volatile static NotifyManager singleton;

    public static NotifyManager getSingleton() {
        if (singleton == null) {
            synchronized (NotifyManager.class) {
                if (singleton == null) {
                    singleton = new NotifyManager();
                }
            }
        }
        return singleton;
    }


    private NotifyManager() {
    }


    public void init(Context context) {
        notificationChannelName = context.getString(R.string.download_notification_name);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(getNotificationChannel(notificationChannelName));
        }
        mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        packageName = context.getPackageName();
    }


    /**
     * 更新通知
     *
     * @param context      context
     * @param downloadInfo downloadInfo
     * @param icon         icon
     */
    public void updateNotification(Context context, DownloadInfo downloadInfo, Bitmap icon) {

        if (mNotificationManager == null) {
            Log.d(TAG, "updateNotification: is not init yet");
            return;
        }

        String contentText = getString(context, downloadInfo);
        int progress = getProgress(downloadInfo);

        PendingIntent contentIntent = getPendingIntent(context, downloadInfo);

        int definedId = DownloadManager.getInstance().getNotificationIcon();
        int iconId = definedId > 0 ? definedId : R.drawable.icon_notification;

        mBuilder.setSmallIcon(iconId);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setContentTitle(downloadInfo.getName());
        //mBuilder.setSubText("SubText");
        mBuilder.setContentText(contentText);
        mBuilder.setProgress(100, progress, false);

        setBuilderSetting(downloadInfo);

        RemoteViews remoteView = getRemoteViews(downloadInfo, icon, contentText);
        mBuilder.setContent(remoteView);

        if (mNotificationManager != null) {
            mNotificationManager.notify(downloadInfo.getId(), mBuilder.build());
        }
    }

    /**
     * 下载进度
     *
     * @param downloadInfo downloadInfo
     * @return Progress
     */
    private int getProgress(DownloadInfo downloadInfo) {

        int progress = 0;
        if (downloadInfo.getTotal() != 0) {
            progress = (int) (downloadInfo.getProgress() * 100 / downloadInfo.getTotal());
        }
        return progress;
    }

    /**
     * 自定义view
     *
     * @param downloadInfo downloadInfo
     * @param icon         icon
     * @param contentText  contentText
     * @return RemoteViews
     */
    private RemoteViews getRemoteViews(DownloadInfo downloadInfo, Bitmap icon, String contentText) {
        RemoteViews remoteView = null;

        int progress = getProgress(downloadInfo);

        try {
            if (icon != null) {
                remoteView = remoteViews.get(downloadInfo.getId());
                if (remoteView == null) {
                    remoteView = new RemoteViews(packageName, R.layout.notification_userdefine_layout);
                    remoteViews.put(downloadInfo.getId(), remoteView);
                }

                remoteView.setTextViewText(R.id.tag_tv, downloadInfo.getName());
                remoteView.setImageViewBitmap(R.id.icon_iv, icon);
                remoteView.setProgressBar(R.id.progress, 100, progress, false);
                remoteView.setTextViewText(R.id.content_tv, contentText);

            }
        } catch (Exception ex) {
            Log.e(TAG, "updateNotification: error", ex);
        }
        return remoteView;
    }


    /**
     * 获取 显示文字
     *
     * @param context      context
     * @param downloadInfo downloadInfo
     * @return String
     */
    private String getString(Context context, DownloadInfo downloadInfo) {
        int progress = getProgress(downloadInfo);
        String contentText = "";
        switch (downloadInfo.getStatus()) {
            case DownloadInfo.JS_STATE_FAILED:
                contentText = context.getString(R.string.download_status_failed);
                break;
            case DownloadInfo.JS_STATE_GET_TOTAL:
            case DownloadInfo.JS_STATE_DOWNLOAD_PRE:
            case DownloadInfo.JS_STATE_DOWNLOADING:
                contentText = String.format(context.getString(R.string.download_status_downloading), progress);
                if (progress == 0) {
                    contentText = context.getString(R.string.download_status_init);
                }
                break;
            case DownloadInfo.JS_STATE_FINISH:
                contentText = context.getString(R.string.download_status_success);
                break;
            case DownloadInfo.JS_STATE_PAUSE:
                contentText = String.format(context.getString(R.string.download_status_pause), progress);
                break;
            case DownloadInfo.JS_STATE_WAIT:
                contentText = context.getString(R.string.download_status_wait);
                break;
            default:
                break;
        }
        return contentText;
    }


    private void setBuilderSetting(DownloadInfo downloadInfo) {
        if (mBuilder == null) {
            return;
        }
        switch (downloadInfo.getStatus()) {
            case DownloadInfo.JS_STATE_FAILED:
            case DownloadInfo.JS_STATE_FINISH:
                mBuilder.setAutoCancel(true);
                mBuilder.setOngoing(false);
                break;
            case DownloadInfo.JS_STATE_GET_TOTAL:
            case DownloadInfo.JS_STATE_DOWNLOAD_PRE:
            case DownloadInfo.JS_STATE_DOWNLOADING:
                mBuilder.setAutoCancel(false);
                mBuilder.setOngoing(true);
                break;
            case DownloadInfo.JS_STATE_PAUSE:
            case DownloadInfo.JS_STATE_WAIT:
                mBuilder.setAutoCancel(false);
                mBuilder.setOngoing(false);
                break;
            default:
                break;
        }

    }


    /**
     * pendingIntent
     *
     * @param context      context
     * @param downloadInfo downloadInfo
     * @return PendingIntent
     */
    private PendingIntent getPendingIntent(Context context, DownloadInfo downloadInfo) {
        //当点击消息时就会向系统发送openintent意图
        Intent intent = new Intent();
        intent.setAction(DOWNLOAD_ACTION_NOTIFICATION_CLICK);
        intent.putExtra("name", downloadInfo.getName());
        intent.putExtra("url", downloadInfo.getActionUrl());
        return PendingIntent.getBroadcast(context, downloadInfo.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void cancel(int id) {
        try {
            if (mNotificationManager != null) {
                mNotificationManager.cancel(id);
            }
        } catch (Exception e) {
            Log.e(TAG, "cancel: error", e);
        }
    }


    public void cancelAll() {
        try {
            if (mNotificationManager != null) {
                mNotificationManager.cancelAll();
            }
        } catch (Exception e) {
            Log.e(TAG, "cancel: error", e);
        }
    }

    private NotificationChannel getNotificationChannel(String notificationChannelName) {
        if (channel == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return createNotificationChannel(notificationChannelName);
            }
        }
        return channel;
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private NotificationChannel createNotificationChannel(String notificationChannelName) {
        channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null, null);
        channel.enableVibration(false);
        return channel;
    }


}
