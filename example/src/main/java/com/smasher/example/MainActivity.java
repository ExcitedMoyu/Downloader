package com.smasher.example;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.smasher.downloader.entity.DownloadInfo;
import com.smasher.downloader.listener.DownloadObserver;
import com.smasher.downloader.manager.DownloadManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author moyu
 */
public class MainActivity extends AppCompatActivity implements DownloadObserver {


    private static final String TAG = "MainActivity";
    @BindView(R.id.start1)
    Button start1;
    @BindView(R.id.stop1)
    Button stop1;
    @BindView(R.id.start2)
    Button start2;
    @BindView(R.id.stop2)
    Button stop2;
    @BindView(R.id.finish)
    Button finish;


    /**
     * QQ阅读
     */
    private static final String ZERO_URL = "https://imtt.dd.qq.com/16891/9D66B9A8CEF65ADFA0975B3AE8B4B8A6.apk?fsname=com.qq.reader_7.0.3.678_128.apk&amp;csr=1bbd";

    /**
     * 豌豆荚 下载地址
     */
    private static final String FIRST_URL = "http://ucan.25pp.com/Wandoujia_web_seo_baidu_homepage.apk";
    /**
     * 起点读书
     * http://download.qidian.com/apknew/source/QDReader.apk
     */
    private static final String Second_URL = "http://download.qidian.com/apknew/source/QDReader.apk";

    /**
     * 12306 APP 下载地址
     */
    private static final String THIRD_URL = "http://dynamic.12306.cn/otn/appDownload/androiddownload";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        ButterKnife.bind(this);
        DownloadManager.getInstance().init(this, "");
        DownloadManager.getInstance().registerObserver(this, this);
        DownloadManager.getInstance().setNotificationEnable(true);
        DownloadManager.getInstance().setNotificationIcon(R.drawable.ic_stat_name);
    }


    @OnClick({R.id.start1, R.id.stop1, R.id.start2, R.id.stop2})
    public void onViewClicked(View view) {


        String zeroName = "com.qq.reader_7.0.3.678_128.apk";
        DownloadInfo info0 = new DownloadInfo();
        info0.setDownLoadType(DownloadInfo.DOWN_LOAD_TYPE_APK);
        info0.setFullName(zeroName);
        info0.setUrl(ZERO_URL);
        info0.setUniqueKey("com.qq.reader");
        info0.setName("QQ阅读");


        String firstName = "豌豆荚.apk";
        DownloadInfo info = new DownloadInfo();
        info.setDownLoadType(DownloadInfo.DOWN_LOAD_TYPE_COMMON);
        info.setFullName(firstName);
        info.setUrl(FIRST_URL);
        info.setUniqueKey("");
        info.setName("豌豆荚");

        String secondName = "QDReader.apk";
        DownloadInfo info2 = new DownloadInfo();
        info2.setDownLoadType(DownloadInfo.DOWN_LOAD_TYPE_APK);
        info2.setFullName(secondName);
        info2.setUrl(Second_URL);
        info2.setUniqueKey("com.qidian.QDReader");
        info2.setName("起点读书");


        String thirdName = "12306.apk";
        DownloadInfo info3 = new DownloadInfo();
        info3.setDownLoadType(DownloadInfo.DOWN_LOAD_TYPE_COMMON);
        info3.setFullName(thirdName);
        info3.setUrl(Second_URL);
        info3.setUniqueKey("");
        info3.setName("12306");


        switch (view.getId()) {
            case R.id.start1:
                DownloadManager.getInstance().addTask(info0).submit(this);
                break;
            case R.id.stop1:
                DownloadManager.getInstance().pauseTask(info0).submit(this);
                break;
            case R.id.start2:
                DownloadManager.getInstance().addTask(info2).submit(this);
                break;
            case R.id.stop2:
                DownloadManager.getInstance().pauseTask(info2).submit(this);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        DownloadManager.getInstance().unRegisterObserver(this, this);
    }

    @Override
    public void onDownloadWait(DownloadInfo info) {
        Log.d(TAG, "onDownloadWait: " + info.getName() + " " + info.getProgress());

    }

    @Override
    public void onDownloadPre(DownloadInfo info) {
        Log.d(TAG, "onDownloadPre: " + info.getName() + " " + info.getProgress());
    }

    @Override
    public void onDownloadError(DownloadInfo info) {
        Log.d(TAG, "onDownloadError: " + info.getName() + " " + info.getProgress());
    }

    @Override
    public void onDownloadProgressed(DownloadInfo info) {
        Log.d(TAG, "onDownloadProgressed: " + info.getName() + " " + info.getProgress());
    }

    @Override
    public void onDownLoadFinished(DownloadInfo info) {
        Log.d(TAG, "onDownLoadFinished: " + info.getName() + " " + info.getProgress());
    }

    @Override
    public void onDownLoadInstalled(DownloadInfo info) {
        Log.d(TAG, "onDownLoadInstalled: " + info.getName() + " " + info.getProgress());
    }

    @OnClick(R.id.finish)
    public void onViewClicked() {
        finish();
    }



}
