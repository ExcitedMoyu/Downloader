package com.smasher.downloader.manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.smasher.downloader.entity.DownloadInfo;
import com.smasher.downloader.entity.RequestInfo;
import com.smasher.downloader.listener.DownloadObserver;
import com.smasher.downloader.service.DownloadService;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载管理
 */
public class DownloadManager {

    private static final String TAG = "[DL]DownLoadMG";

    private ArrayList<RequestInfo> requests = new ArrayList<>();
    private List<DownloadObserver> mObservers = new ArrayList<>();

    private volatile static DownloadManager mInstance;

    public static DownloadManager getInstance() {
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new DownloadManager();
                }
            }
        }
        return mInstance;
    }

    private DownloadManager() {
    }


    /**
     * 注册观察者
     */
    public void registerObserver(DownloadObserver observer) {
        synchronized (mObservers) {
            if (!mObservers.contains(observer)) {
                mObservers.add(observer);
            }
        }
    }

    /**
     * 反注册观察者
     */
    public void unRegisterObserver(DownloadObserver observer) {
        synchronized (mObservers) {
            if (mObservers.contains(observer)) {
                mObservers.remove(observer);
            }
        }
    }


    /**
     * 提交  下载/暂停  等任务.(提交就意味着开始执行生效)
     *
     * @param context context
     */
    public synchronized void submit(Context context) {
        if (requests.isEmpty()) {
            Log.w(TAG, "没有下载任务可供执行");
            return;
        }
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.SERVICE_INTENT_EXTRA, requests);
        context.startService(intent);
        requests.clear();
    }


    /**
     * 添加 新的下载任务
     *
     * @param info 下载的url
     * @return DownloadHelper自身 (方便链式调用)
     */
    public DownloadManager addTask(DownloadInfo info) {
        int requestType = RequestInfo.COMMAND_DOWNLOAD;
        RequestInfo requestInfo = createRequest(info, requestType);
        Log.i(TAG, "addTask() requestInfo=" + requestInfo);
        requests.add(requestInfo);
        return this;
    }

    /**
     * 暂停某个下载任务
     *
     * @param info 下载的url
     * @return DownloadHelper自身 (方便链式调用)
     */
    public DownloadManager pauseTask(DownloadInfo info) {
        int requestType = RequestInfo.COMMAND_PAUSE;
        RequestInfo requestInfo = createRequest(info, requestType);
        Log.i(TAG, "pauseTask() -> requestInfo=" + requestInfo);
        requests.add(requestInfo);
        return this;
    }


    private RequestInfo createRequest(DownloadInfo info, int requestType) {
        RequestInfo request = new RequestInfo();
        request.setCommand(requestType);
        request.setDownloadInfo(info);
        return request;
    }


    // region #Observers
    private void notifyDownloadProgressed(DownloadInfo info) {
        synchronized (mObservers) {
            for (DownloadObserver observer : mObservers) {
                observer.onDownloadProgressed(info);
            }
        }
    }


    private void notifyDownloadPre(DownloadInfo info) {
        synchronized (mObservers) {
            for (DownloadObserver observer : mObservers) {
                observer.onDownloadPre(info);
            }
        }
    }


    private void notifyDownloadFinished(DownloadInfo info) {
        synchronized (mObservers) {
            for (DownloadObserver observer : mObservers) {
                observer.onDownLoadFinished(info);
            }
        }
    }

    private void notifyDownloadInstalled(DownloadInfo info) {
        synchronized (mObservers) {
            for (DownloadObserver observer : mObservers) {
                observer.onDownLoadInstalled(info);
            }
        }
    }


    private void notifyDownloadError(DownloadInfo info) {
        synchronized (mObservers) {
            for (DownloadObserver observer : mObservers) {
                observer.onDownloadError(info);
            }
        }
    }
    //end region
}
