package com.zhihu.matisse.sample;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * 文件描述
 *
 * @author chenfeng
 * @since 2017-07-26 13:38
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        initFresco();
    }

    private void initFresco() {
        Fresco.initialize(getApplicationContext());
    }

}
