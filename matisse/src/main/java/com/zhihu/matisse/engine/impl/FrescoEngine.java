package com.zhihu.matisse.engine.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.zhihu.matisse.engine.ImageEngine;

/**
 * {@link ImageEngine} implements with Fresco
 */

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
public class FrescoEngine implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        FrescoLoader.with(context)
                .resize(resize, resize)
                .placeholder(placeholder)
                .uri(uri)
                .actualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        FrescoLoader.with(context)
                .resize(resize, resize)
                .placeholder(placeholder)
                .uri(uri)
                .actualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        FrescoLoader.with(context)
                .resize(resizeX, resizeY)
                .uri(uri)
                .actualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        FrescoLoader.with(context)
                .resize(resizeX, resizeY)
                .uri(uri)
                .autoPlayAnimation(true)
                .actualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }

}
