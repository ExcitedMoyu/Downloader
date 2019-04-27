package com.smasher.downloader.manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.smasher.downloader.DownloadConfig;
import com.smasher.downloader.R;
import com.smasher.downloader.entity.DownloadInfo;


/**
 * 下载通知栏管理
 *
 * @author matao
 * @date 2017/8/15
 */
public class NotifyManager {

    private static final String TAG = "[DL]Notification";
    private SparseArray<RemoteViews> remoteViews = new SparseArray<>();

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private static NotificationChannel channel;

    private String NOTIFICATION_CHANNEL_ID = "download_channelId";
    private String NOTIFICATION_CHANNEL_NAME = "download_message";
    private String DOWNLOAD_ACTION_NOTIFICATION_CLICK = "DOWNLOAD_ACTION_NOTIFICATION_CLICK";

    private volatile static NotifyManager singleton;

    public static NotifyManager getSingleton(Context context) {
        if (singleton == null) {
            synchronized (NotifyManager.class) {
                if (singleton == null) {
                    singleton = new NotifyManager(context);
                }
            }
        }
        return singleton;
    }


    private NotifyManager(Context context) {
        NOTIFICATION_CHANNEL_NAME = context.getString(R.string.download_notification_name);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(getNotificationChannel());
        }
        mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

    }


    public void updateNotification(Context context, DownloadInfo downloadInfo, Bitmap icon) {
        //下载进度
        int progress = 0;
        if (downloadInfo.getTotal() != 0) {
            progress = (int) (downloadInfo.getProgress() * 100 / downloadInfo.getTotal());
        }

        String contentText = "";
        switch (downloadInfo.getStatus()) {
            case DownloadInfo.JS_STATE_FAILED:
                contentText = context.getString(R.string.download_status_failed);
                mBuilder.setAutoCancel(true);
                mBuilder.setOngoing(false);
                break;
            case DownloadInfo.JS_STATE_GET_TOTAL:
            case DownloadInfo.JS_STATE_DOWNLOAD_PRE:
            case DownloadInfo.JS_STATE_DOWNLOADING:
                contentText = String.format(context.getString(R.string.download_status_downloading), progress);
                if (progress == 0) {
                    contentText = context.getString(R.string.download_status_init);
                }
                mBuilder.setAutoCancel(false);
                mBuilder.setOngoing(true);
                break;
            case DownloadInfo.JS_STATE_FINISH:
                contentText = context.getString(R.string.download_status_success);
                mBuilder.setAutoCancel(true);
                mBuilder.setOngoing(false);
                break;
            case DownloadInfo.JS_STATE_PAUSE:
                contentText = String.format(context.getString(R.string.download_status_pause), progress);
                mBuilder.setAutoCancel(false);
                mBuilder.setOngoing(false);
                break;
            case DownloadInfo.JS_STATE_WAIT:
                contentText = context.getString(R.string.download_status_wait);
                mBuilder.setAutoCancel(false);
                mBuilder.setOngoing(false);
                break;
            default:
                break;
        }


        PendingIntent contentIntent = getPendingIntent(context, downloadInfo);
        mBuilder.setSmallIcon(R.drawable.icon_notification);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setContentTitle(downloadInfo.getName());
        //mBuilder.setSubText("SubText");
        mBuilder.setContentText(contentText);
        mBuilder.setProgress(100, progress, false);

        try {
            if (icon != null) {
                RemoteViews remoteView = remoteViews.get(downloadInfo.getId());
                if (remoteView == null) {
                    remoteView = new RemoteViews(context.getPackageName(), R.layout.notification_userdefine_layout);
                    remoteViews.put(downloadInfo.getId(), remoteView);
                }

                remoteView.setTextViewText(R.id.tag_tv, downloadInfo.getName());
                remoteView.setImageViewBitmap(R.id.icon_iv, icon);
                remoteView.setProgressBar(R.id.progress, 100, progress, false);
                remoteView.setTextViewText(R.id.content_tv, contentText);

                mBuilder.setContent(remoteView);
            }
        } catch (Exception ex) {
            Log.e(TAG, "updateNotification: error", ex);
        }

        if (mNotificationManager != null) {
            mNotificationManager.notify(downloadInfo.getId(), mBuilder.build());
        }
    }

    private PendingIntent getPendingIntent(Context context, DownloadInfo downloadInfo) {
        //当点击消息时就会向系统发送openintent意图
        Intent intent = new Intent();
        intent.setAction(DOWNLOAD_ACTION_NOTIFICATION_CLICK);
        intent.putExtra("name", downloadInfo.getName());
        intent.putExtra("url", downloadInfo.getActionUrl());
        return PendingIntent.getBroadcast(context, downloadInfo.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void cancel(Context context, int id) {
        try {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.cancel(id);
            }
        } catch (Exception e) {
            Log.e(TAG, "cancel: error", e);
        }
    }


    private NotificationChannel getNotificationChannel() {
        if (channel == null) {
            return createNotificationChannel();
        }
        return channel;
    }


    private NotificationChannel createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        } else {
            Log.e(TAG, "Android版本低于26，无需创建通知渠道");
        }
        return null;
    }


}
