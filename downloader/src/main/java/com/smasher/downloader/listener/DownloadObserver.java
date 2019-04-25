package com.smasher.downloader.listener;


import com.smasher.downloader.entity.DownloadInfo;

/**
 * @author matao
 */
public interface DownloadObserver {

    void onDownloadWait(DownloadInfo info);

    void onDownloadPre(DownloadInfo info);

    void onDownloadError(DownloadInfo info);

    void onDownloadProgressed(DownloadInfo info);

    void onDownLoadFinished(DownloadInfo info);

    void onDownLoadInstalled(DownloadInfo info);
}
