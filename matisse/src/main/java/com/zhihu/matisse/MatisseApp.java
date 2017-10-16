package com.zhihu.matisse;

import android.app.Application;
import android.graphics.Bitmap;

import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.zhihu.matisse.engine.impl.FrescoSupplier;

/**
 * Created by bhijuelos on 10/11/17.
 */

public class MatisseApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        initializeFresco();
    }

    private void initializeFresco() {
        Supplier<MemoryCacheParams> supplier = new FrescoSupplier();

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(getApplicationContext())
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .setBitmapMemoryCacheParamsSupplier(supplier)
                .build();
        // Initializing Fresco, the image library
        Fresco.initialize(this, config);
    }
}
