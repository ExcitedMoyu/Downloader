package com.smasher.util;

import android.app.Application;

/**
 * @author moyu
 */
public class ApplicationContext {

    private static Application mApplicationContext;

    public static Application getInstance() {
        return mApplicationContext;
    }

    public static void setApplicationContext(Application applicationContext) {
        mApplicationContext = applicationContext;
    }
}