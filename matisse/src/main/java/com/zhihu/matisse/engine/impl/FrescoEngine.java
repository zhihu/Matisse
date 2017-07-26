package com.zhihu.matisse.engine.impl;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.zhihu.matisse.engine.ImageEngine;

/**
 * {@link ImageEngine} implements with Fresco
 */

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
public class FrescoEngine implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        DraweeLoader loader = new DraweeLoaderBuilder(context)
                .setrResize(resize, resize)
                .setPlaceholder(placeholder)
                .setUri(uri)
                .build();
        loader.load(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {

    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {

    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {

    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public class DraweeLoader implements View.OnAttachStateChangeListener {

        private DraweeHolder<GenericDraweeHierarchy> mDraweeHolder;
        private DraweeLoaderBuilder builder;

        public DraweeLoader(DraweeLoaderBuilder builder) {
            this.builder = builder;
            initFresco();
            initHolder();
            initController();
        }

        private void initFresco() {
            if (!Fresco.hasBeenInitialized()) {
                Fresco.initialize(builder.getContext());
            }
        }

        private void initHolder() {
            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(builder.getContext().getResources())
                    .setPlaceholderImage(builder.getPlaceholder())
                    .build();
            mDraweeHolder = DraweeHolder.create(hierarchy, builder.getContext());
        }

        private void initController() {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(builder.getUri())
                    .setResizeOptions(builder.getResize())
                    .build();

            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setOldController(mDraweeHolder.getController())
                    .setImageRequest(request)
                    .build();
            mDraweeHolder.setController(draweeController);
        }

        @Override
        public void onViewAttachedToWindow(View v) {
            mDraweeHolder.onAttach();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            mDraweeHolder.onDetach();
        }

        public void load(ImageView targetView) {
            targetView.addOnAttachStateChangeListener(this);
            targetView.setImageDrawable(mDraweeHolder.getTopLevelDrawable());
        }

    }

    public class DraweeLoaderBuilder {
        public Context mContext;
        private ResizeOptions mResizeOptions;
        private Drawable mPlaceHolder;
        private Uri mUri;

        public DraweeLoaderBuilder(Context context) {
            this.mContext = context;
        }

        public Context getContext() {
            return mContext;
        }

        public DraweeLoaderBuilder setUri(Uri uri) {
            this.mUri = uri;
            return this;
        }

        public Uri getUri() {
            return mUri;
        }

        public DraweeLoaderBuilder setrResize(int width, int height) {
            mResizeOptions = new ResizeOptions(width, height);
            return this;
        }

        public ResizeOptions getResize() {
            return mResizeOptions;
        }

        public DraweeLoaderBuilder setPlaceholder(Drawable drawable) {
            this.mPlaceHolder = drawable;
            return this;
        }

        public Drawable getPlaceholder() {
            return mPlaceHolder;
        }


        public DraweeLoader build() {
            return new DraweeLoader(this);
        }
    }
}
