package com.smasher.downloader.listener;


import com.smasher.downloader.entity.DownloadInfo;

public interface DownloadListener {

    void onDownLoadWait(DownloadInfo info);

    void onDownLoadPre(DownloadInfo info);

    void onDownLoadProgress(DownloadInfo info);

    void onDownLoadFinished(DownloadInfo info);

    void onDownLoadInstalled(DownloadInfo info);

    void onDownLoadError(DownloadInfo info);

    void onDownLoadPause(DownloadInfo info);
}
