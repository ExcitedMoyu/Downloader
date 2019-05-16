package com.smasher.downloader.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @author moyu
 */
public class WeakReferenceHandler extends Handler {
    private WeakReference<Callback> mWeakReferCallBack;

    public WeakReferenceHandler(Callback cb) {
        super();
        mWeakReferCallBack = new WeakReference<>(cb);
    }

    public WeakReferenceHandler(Looper looper, Callback cb) {
        super(looper);
        mWeakReferCallBack = new WeakReference<>(cb);
    }

    @Override
    public void handleMessage(Message msg) {
        Callback cb = mWeakReferCallBack.get();
        if (null != cb) {
            cb.handleMessage(msg);
        }
    }
}
