package com.zhihu.matisse.sample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.zhihu.matisse.engine.EngineView;
import com.zhihu.matisse.engine.ImageEngine;

import me.relex.photodraweeview.PhotoDraweeView;

/**
 * author: EwenQin
 * since : 2017/7/28 下午3:20.
 */

public class FrescoImageEngine implements ImageEngine {

    @Override public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        if (imageView instanceof PhotoDraweeView) {
            PhotoDraweeView draweeView = (PhotoDraweeView) imageView;
            frescoLoadPhoto(context, new ResizeOptions(resizeX, resizeY), null, draweeView, uri, false);
        } else {
            frescoLoadImage(context, new ResizeOptions(resizeX, resizeY), null, imageView, uri, false);
        }
    }

    @Override public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        if (imageView instanceof PhotoDraweeView) {
            PhotoDraweeView draweeView = (PhotoDraweeView) imageView;
            frescoLoadPhoto(context, new ResizeOptions(resize, resize), placeholder, draweeView, uri, false);
        } else {
            frescoLoadImage(context, new ResizeOptions(resize, resize), placeholder, imageView, uri, false);
        }
    }

    @Override public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        if (imageView instanceof PhotoDraweeView) {
            PhotoDraweeView draweeView = (PhotoDraweeView) imageView;
            frescoLoadPhoto(context, new ResizeOptions(resize, resize), placeholder, draweeView, uri, true);
        } else {
            frescoLoadImage(context, new ResizeOptions(resize, resize), placeholder, imageView, uri, true);
        }
    }

    @Override public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        if (imageView instanceof PhotoDraweeView) {
            PhotoDraweeView draweeView = (PhotoDraweeView) imageView;
            frescoLoadPhoto(context, new ResizeOptions(resizeX, resizeY), null, draweeView, uri, true);
        } else {
            frescoLoadImage(context, new ResizeOptions(resizeX, resizeY), null, imageView, uri, true);
        }
    }

    private void frescoLoadImage(Context context, ResizeOptions resizeOptions, Drawable placeholder, ImageView imageView, Uri uri, boolean isGif) {
        if (!(imageView instanceof EngineView)) {
            return;
        }
        EngineView engineView = (EngineView) imageView;
        FrescoViewHolder holder;
        if (engineView.getOnEngineViewAttachListener() != null) {
            holder = (FrescoViewHolder) engineView.getOnEngineViewAttachListener();
        } else {
            holder = new FrescoViewHolder(context, imageView);
            engineView.setOnEngineViewAttachListener(holder);
        }
        holder.placeholder(placeholder);
        ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (resizeOptions != null) {
            requestBuilder.setResizeOptions(resizeOptions);
        }
        holder.setController(Fresco.newDraweeControllerBuilder()
                .setOldController(holder.getController())
                .setAutoPlayAnimations(isGif)
                .setImageRequest(requestBuilder.build())
                .build());
    }

    private void frescoLoadPhoto(Context context, ResizeOptions resizeOptions, Drawable placeholder, PhotoDraweeView draweeView, Uri uri, boolean isGif) {
        draweeView.getHierarchy().setPlaceholderImage(placeholder);
        ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (resizeOptions != null) {
            requestBuilder.setResizeOptions(resizeOptions);
        }
        draweeView.setEnableDraweeMatrix(false);
        draweeView.setController(Fresco.newDraweeControllerBuilder()
                .setOldController(draweeView.getController())
                .setAutoPlayAnimations(isGif)
                .setImageRequest(requestBuilder.build())
                .setControllerListener(new PhotoControllerListener(draweeView))
                .build());
    }

    @Override public boolean supportAnimatedGif() {
        return true;
    }

    private class PhotoControllerListener extends BaseControllerListener<ImageInfo> {

        private PhotoDraweeView mDraweeView;

        private PhotoControllerListener(PhotoDraweeView draweeView) {
            this.mDraweeView = draweeView;
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
            super.onFailure(id, throwable);
            mDraweeView.setEnableDraweeMatrix(false);
        }

        @Override
        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            super.onFinalImageSet(id, imageInfo, animatable);
            mDraweeView.setEnableDraweeMatrix(true);
            if (imageInfo != null) {
                mDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        }

        @Override
        public void onIntermediateImageFailed(String id, Throwable throwable) {
            super.onIntermediateImageFailed(id, throwable);
            mDraweeView.setEnableDraweeMatrix(false);
        }

        @Override
        public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
            super.onIntermediateImageSet(id, imageInfo);
            mDraweeView.setEnableDraweeMatrix(true);
            if (imageInfo != null) {
                mDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        }
    }

    private class FrescoViewHolder implements EngineView.OnEngineViewAttachListener {

        private DraweeHolder<GenericDraweeHierarchy> mDraweeHolder;

        private FrescoViewHolder(Context context, ImageView into) {
            Resources resources = context.getResources();
            GenericDraweeHierarchyBuilder hierarchyBuilder = new GenericDraweeHierarchyBuilder(resources);
            mDraweeHolder = DraweeHolder.create(hierarchyBuilder.build(), context);
            into.setImageDrawable(mDraweeHolder.getTopLevelDrawable());
        }

        private FrescoViewHolder placeholder(Drawable placeholder) {
            mDraweeHolder.getHierarchy().setPlaceholderImage(placeholder);
            return this;
        }

        private DraweeController getController() {
            return mDraweeHolder.getController();
        }

        private void setController(DraweeController draweeController) {
            mDraweeHolder.setController(draweeController);
        }

        @Override public void onViewAttachedToWindow(View v) {
            mDraweeHolder.onAttach();
        }

        @Override public void onViewDetachedFromWindow(View v) {
            mDraweeHolder.onDetach();
        }

        @Override public void onViewFinishTemporaryDetach(View v) {
            mDraweeHolder.onAttach();
        }

        @Override public void onViewStartTemporaryDetach(View v) {
            mDraweeHolder.onDetach();
        }
    }
}
