/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.engine.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zhihu.matisse.engine.ImageEngine;

/**
 * {@link ImageEngine} implementation using Picasso.
 */

public class PicassoEngine implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, Drawable placeholder, ImageView imageView, Uri uri) {
        Picasso.with(context).load(uri).placeholder(placeholder)
                .fit()
                .centerCrop()
                .into(imageView);
    }

    @Override
    public void loadAnimatedGifThumbnail(Context context, Drawable placeholder, ImageView imageView,
                                         Uri uri) {
        loadThumbnail(context, placeholder, imageView, uri);
    }

    @Override
    public void loadImage(Context context, ImageView imageView, Uri uri) {
        Picasso.with(context).load(uri).fit().priority(Picasso.Priority.HIGH)
                .centerInside().into(imageView);
    }

    @Override
    public void loadAnimatedGifImage(Context context, ImageView imageView, Uri uri) {
        loadImage(context, imageView, uri);
    }

    @Override
    public boolean supportAnimatedGif() {
        return false;
    }
}
