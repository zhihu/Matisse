package com.zhihu.matisse.engine.impl;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.Priority;
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
        FrescoLoader.with(context)
                .resize(resize, resize)
                .placeholder(placeholder)
                .uri(uri)
                .scaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        FrescoLoader.with(context)
                .resize(resize, resize)
                .placeholder(placeholder)
                .uri(uri)
                .scaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        FrescoLoader.with(context)
                .resize(resizeX, resizeY)
                .uri(uri)
                .scaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        FrescoLoader.with(context)
                .resize(resizeX, resizeY)
                .uri(uri)
                .scaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static class FrescoLoader implements View.OnAttachStateChangeListener {

        private Context mContext;
        private ScalingUtils.ScaleType mScaleType = ScalingUtils.ScaleType.CENTER_CROP;
        private ResizeOptions mResizeOptions;
        private Drawable mPlaceHolder;
        private Uri mUri;
        private DraweeHolder<GenericDraweeHierarchy> mDraweeHolder;

        public FrescoLoader(Context context) {
            this.mContext = context.getApplicationContext();
        }

        public static FrescoLoader with(Context context) {
            return new FrescoLoader(context);
        }

        public FrescoLoader uri(Uri uri) {
            this.mUri = uri;
            return this;
        }

        public FrescoLoader scaleType(@Nullable ScalingUtils.ScaleType type) {
            this.mScaleType = type;
            return this;
        }

        public FrescoLoader placeholder(Drawable drawable) {
            this.mPlaceHolder = drawable;
            return this;
        }

        public FrescoLoader resize(ResizeOptions resizeOptions) {
            this.mResizeOptions = resizeOptions;
            return this;
        }

        public FrescoLoader resize(int targetWidth, int targetHeight) {
            this.mResizeOptions = new ResizeOptions(targetWidth, targetHeight);
            return this;
        }

        public void into(ImageView targetView) {
            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(mContext.getResources())
                    .setFadeDuration(300)
                    .setPlaceholderImage(mPlaceHolder)
                    .setFailureImage(mPlaceHolder)
                    .setRetryImage(mPlaceHolder)
                    .setActualImageScaleType(mScaleType)
                    .build();
            mDraweeHolder = DraweeHolder.create(hierarchy, mContext);

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(mUri)
                    .setResizeOptions(mResizeOptions)
                    .setProgressiveRenderingEnabled(true)
//                    .setLocalThumbnailPreviewsEnabled(true)
                    .setRequestPriority(Priority.HIGH)
                    .build();

            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setOldController(mDraweeHolder.getController())
                    .setImageRequest(request)
                    .setTapToRetryEnabled(true)
                    .build();
            mDraweeHolder.setController(draweeController);

            targetView.addOnAttachStateChangeListener(this);
            targetView.setImageDrawable(mDraweeHolder.getTopLevelDrawable());
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
