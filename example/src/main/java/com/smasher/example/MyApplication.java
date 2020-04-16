package com.smasher.example;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.smasher.downloader.path.AppPath;
import com.smasher.downloader.path.ApplicationContext;

/**
 * @author matao
 * @date 2019/4/25
 */
public class MyApplication extends Application {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
        ApplicationContext.setApplicationContext(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
