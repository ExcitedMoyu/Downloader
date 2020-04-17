package com.smasher.example;

import android.app.DownloadManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.DownloadManager.Query;

/**
 * @author Smasher
 * on 2020/4/17 0017
 */
public class DownloadChangeObserver extends ContentObserver {

    private static final String TAG = "ContentObserver";
    public static final int HANDLE_DOWNLOAD = 0x001;
    private ScheduledExecutorService scheduledExecutorService;
    private DownloadManager mDownloadManager;

    private long mId;
    private Handler mHandler;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public DownloadChangeObserver(Handler handler, int id, DownloadManager downloadManager) {
        super(handler);
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mDownloadManager = downloadManager;
        mHandler = handler;
        mId = id;
        Log.d(TAG, "Constructor DownloadChangeObserver: ");
    }


    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        //在子线程中查询
        scheduledExecutorService.scheduleAtFixedRate(progressRunnable, 0, 1, TimeUnit.SECONDS);
        Log.d(TAG, "DownloadChangeObserver: onChange");

    }


    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Log.d(TAG, "DownloadChangeObserver: onChange");
    }


    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            query();
        }
    };


    private void query() {
        Log.d(TAG, "query: ");

        int state = 0;
        long size = -1;
        long total = -1;
        if (mDownloadManager != null) {
            Query query = new Query();
            query.setFilterById(mId);

            Cursor cursor = null;
            try {
                cursor = mDownloadManager.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    //已经下载文件大小
                    size = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    Log.d(TAG, "query: size-" + size);
                    //下载文件的总大小
                    total = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    Log.d(TAG, "query: total-" + total);
                    //下载状态
                    state = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    Log.d(TAG, "query: state-" + state);
                    switch (state) {
                        case DownloadManager.STATUS_FAILED:
                            Log.d(TAG, "query: state- 失敗");
                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            Log.d(TAG, "query: state- 成功");
                            break;
                        case DownloadManager.STATUS_PENDING:
                            Log.d(TAG, "query: state- 等待");
                            break;
                        case DownloadManager.STATUS_PAUSED:
                            Log.d(TAG, "query: state- 暂停");
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            Log.d(TAG, "query: state- 下载中");
                            break;
                        default:
                            break;
                    }
                } else {
                    Log.d(TAG, "query: cursor is null");
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setState(state);
            resultInfo.setSize(size);
            resultInfo.setTotal(total);

            mHandler.sendMessage(mHandler.obtainMessage(HANDLE_DOWNLOAD, 0, 0, resultInfo));
        }
    }


    /**
     * 关闭定时器，线程等操作
     */
    public void close() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
    }
}
