package com.smasher.downloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.smasher.downloader.entity.DownloadInfo;
import com.smasher.downloader.manager.DownloadManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author moyu
 */
public class MainActivity extends AppCompatActivity {


    @BindView(R.id.start1)
    Button start1;
    @BindView(R.id.stop1)
    Button stop1;
    @BindView(R.id.start2)
    Button start2;
    @BindView(R.id.stop2)
    Button stop2;

    /**
     * 豌豆荚 下载地址
     */
    private static final String FIRST_URL = "http://ucan.25pp.com/Wandoujia_web_seo_baidu_homepage.apk";
    /**
     * 12306 APP 下载地址
     */
    private static final String THIRD_URL = "http://dynamic.12306.cn/otn/appDownload/androiddownload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.start1, R.id.stop1, R.id.start2, R.id.stop2})
    public void onViewClicked(View view) {


        String firstName = "豌豆荚.apk";
        DownloadInfo info = new DownloadInfo();
        info.setDownLoadType(DownloadInfo.DOWN_LOAD_TYPE_COMMON);
        info.setFullName(firstName);
        info.setUrl(FIRST_URL);
        info.setUniqueKey("");
        info.setName("豌豆荚");

        String thirdName = "12306.apk";
        DownloadInfo info2 = new DownloadInfo();
        info2.setDownLoadType(DownloadInfo.DOWN_LOAD_TYPE_COMMON);
        info2.setFullName(thirdName);
        info2.setUrl(THIRD_URL);
        info2.setUniqueKey("");
        info2.setName("12306");


        switch (view.getId()) {
            case R.id.start1:
                DownloadManager.getInstance().addTask(info).submit(this);
                break;
            case R.id.stop1:
                DownloadManager.getInstance().pauseTask(info).submit(this);
                break;
            case R.id.start2:
                DownloadManager.getInstance().addTask(info2).submit(this);
                break;
            case R.id.stop2:
                DownloadManager.getInstance().pauseTask(info).submit(this);
                break;
            default:
                break;
        }
    }
}
