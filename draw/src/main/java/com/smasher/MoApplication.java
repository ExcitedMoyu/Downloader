package com.smasher;

import android.app.Application;
import android.content.Context;

import com.smasher.util.ApplicationContext;

/**
 * @author matao
 * @date 2019/5/9
 */
public class MoApplication extends Application {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ApplicationContext.setApplicationContext(this);
    }
}
