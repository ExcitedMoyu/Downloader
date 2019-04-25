package com.smasher.downloader;

import android.app.Application;

import com.smasher.downloader.path.AppPath;

/**
 * @author matao
 * @date 2019/4/25
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        AppPath.init("DownLoad[Dl]");
        DownloadConfig.init(this, AppPath.getDownloadPath(this));
    }
}
