package com.zhihu.matisse.engine.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhihu.matisse.engine.ImageEngine;

import coil.ComponentRegistry;
import coil.ImageLoader;
import coil.ImageLoaderBuilder;
import coil.api.ImageLoaders;
import coil.decode.GifDecoder;
import coil.decode.ImageDecoderDecoder;
import coil.request.LoadRequest;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.P;

public class CoilEngine implements ImageEngine {

    private ImageLoader mImageLoader;
    private boolean mSupportsGifs;

    public CoilEngine(@NonNull Context context) {
        checkIfSupportsGifs();

        ImageLoaderBuilder builder = new ImageLoaderBuilder(context);
        if (mSupportsGifs) {
            ComponentRegistry.Builder registryBuilder = new ComponentRegistry.Builder();
            if (SDK_INT >= P) {
                registryBuilder.add(new ImageDecoderDecoder());
            } else {
                registryBuilder.add(new GifDecoder());
            }
            builder.componentRegistry(registryBuilder.build());
        }
        mImageLoader = builder.build();
    }

    public CoilEngine(@NonNull ImageLoader imageLoader) {
        checkIfSupportsGifs();
        mImageLoader = imageLoader;
    }

    private void checkIfSupportsGifs() {
        try {
            Class.forName(GifDecoder.class.getName());
            mSupportsGifs = true;
        } catch (Exception ignored) {
            mSupportsGifs = false;
        }
    }

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        load(context, imageView, uri, placeholder, resize, resize);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        load(context, imageView, uri, placeholder, resize, resize);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        load(context, imageView, uri, null, resizeX, resizeY);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        load(context, imageView, uri, null, resizeX, resizeY);
    }

    private void load(Context context, ImageView imageView, Uri uri, @Nullable Drawable placeholder,
                      int width, int height) {
        LoadRequest request = ImageLoaders.newLoadBuilder(mImageLoader, context)
                .data(uri)
                .size(width, height)
                .placeholder(placeholder)
                .target(imageView)
                .build();
        mImageLoader.load(request);
    }

    @Override
    public boolean supportAnimatedGif() {
        return mSupportsGifs;
    }
}
