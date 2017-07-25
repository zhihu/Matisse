package com.zhihu.matisse.engine.impl;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ForwardingDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.zhihu.matisse.engine.ImageEngine;

/**
 * {@link ImageEngine} implements with Fresco
 */

public class FrescoEngine implements ImageEngine {

    private void initFresco(Context context) {
        if (!Fresco.hasBeenInitialized()) {
            Fresco.initialize(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        initFresco(context);

        DraweeLoader loader = new DraweeLoader(context);
        loader.setSize(resize);
        loader.setImageUri(uri);
        imageView.addOnAttachStateChangeListener(loader);
        imageView.setImageDrawable(loader.mDraweeHolder.getTopLevelDrawable());

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

        private ForwardingDrawable mActualDrawable;

        private int size = 0;

        public DraweeHolder<GenericDraweeHierarchy> mDraweeHolder;

        public DraweeLoader(Context context) {
            init(context);
        }

        public void setSize(int size){
            this.size = size;
        }

        private void init(Context context) {
            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
//                    .setPlaceholderImage()
                    .build();
            mDraweeHolder = DraweeHolder.create(hierarchy, context);

        }

        public void setImageUri(Uri uri) {

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(size,size))
                    .build();
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setOldController(mDraweeHolder.getController())
                    .setImageRequest(request)
                    .setControllerListener(new ControllerListener<ImageInfo>() {
                        @Override
                        public void onSubmit(String id, Object callerContext) {

                        }

                        @Override
                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {

                        }

                        @Override
                        public void onIntermediateImageSet(String id, ImageInfo imageInfo) {

                        }

                        @Override
                        public void onIntermediateImageFailed(String id, Throwable throwable) {

                        }

                        @Override
                        public void onFailure(String id, Throwable throwable) {

                        }

                        @Override
                        public void onRelease(String id) {

                        }
                    })
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
    }
}
