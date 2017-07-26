package com.zhihu.matisse.sample;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initFresco();
    }

    private void initFresco() {
        Fresco.initialize(getApplicationContext());
    }

}
