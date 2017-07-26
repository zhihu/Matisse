package com.zhihu.matisse.engine.impl;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.components.DeferredReleaser;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * fresco image loader, especially for local images
 * http://frescolib.org/docs/
 *
 * @author chenfeng
 * @since 2017-07-26 15:46
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class FrescoLoader implements View.OnAttachStateChangeListener {

    private Context mContext;
    private DraweeHolder<GenericDraweeHierarchy> mDraweeHolder;
    private Uri mUri;
    //placeholder
    private Drawable mPlaceHolderDrawable;
    private Drawable mFailureDrawable;
    private Drawable mProgressBarDrawable;
    //size,scale,rotation
    private ResizeOptions mResizeOptions;
    private RotationOptions mRotationOptions;
    private float mDesiredAspectRatio;
    private PointF mActualImageFocusPoint;
    private ScalingUtils.ScaleType mPlaceholderScaleType;
    private ScalingUtils.ScaleType mFailureScaleType;
    private ScalingUtils.ScaleType mProgressBarScaleType;
    private ScalingUtils.ScaleType mActualImageScaleType;

    //other settings
    private Drawable mBackgroundDrawable;
    private RoundingParams mRoundingParams;
    private int mFadeDuration;
    private boolean mAutoPlayAnimations;
    private boolean mLocalThumbnailPreviewsEnabled;

    FrescoLoader(Context context) {
        this.mContext = context.getApplicationContext();

        mPlaceHolderDrawable = null;
        mPlaceholderScaleType = GenericDraweeHierarchyBuilder.DEFAULT_SCALE_TYPE;
        mFailureDrawable = null;
        mFailureScaleType = GenericDraweeHierarchyBuilder.DEFAULT_SCALE_TYPE;
        mProgressBarDrawable = null;
        mProgressBarScaleType = GenericDraweeHierarchyBuilder.DEFAULT_SCALE_TYPE;
        mActualImageScaleType = GenericDraweeHierarchyBuilder.DEFAULT_ACTUAL_IMAGE_SCALE_TYPE;
        mActualImageFocusPoint = null;

        mResizeOptions = null;
        mRotationOptions = RotationOptions.autoRotate();
        mDesiredAspectRatio = 0;

        mBackgroundDrawable = null;
        mRoundingParams = null;
        mFadeDuration = GenericDraweeHierarchyBuilder.DEFAULT_FADE_DURATION;
        mAutoPlayAnimations = false;
        mLocalThumbnailPreviewsEnabled = true;

        mDraweeHolder = DraweeHolder.create(null, mContext);
    }

    public static FrescoLoader with(Context context) {
        return new FrescoLoader(context);
    }

    public FrescoLoader uri(Uri uri) {
        this.mUri = uri;
        return this;
    }

    public FrescoLoader actualImageScaleType(ScalingUtils.ScaleType type) {
        this.mActualImageScaleType = type;
        return this;
    }

    public FrescoLoader placeholder(Drawable drawable) {
        this.mPlaceHolderDrawable = drawable;
        return this;
    }

    public FrescoLoader placeHolderScaleType(ScalingUtils.ScaleType type) {
        this.mPlaceholderScaleType = type;
        return this;
    }

    public FrescoLoader failure(Drawable drawable) {
        this.mFailureDrawable = drawable;
        return this;
    }

    public FrescoLoader failureScaleType(ScalingUtils.ScaleType type) {
        this.mFailureScaleType = type;
        return this;
    }

    public FrescoLoader actualImageFocusPoint(PointF foucePoint) {
        this.mActualImageFocusPoint = foucePoint;
        return this;
    }

    public FrescoLoader progressbar(Drawable drawable) {
        this.mProgressBarDrawable = drawable;
        return this;
    }

    public FrescoLoader progressbarScaleType(ScalingUtils.ScaleType type) {
        this.mProgressBarScaleType = type;
        return this;
    }

    public FrescoLoader rotationType(RotationOptions options) {
        this.mRotationOptions = options;
        return this;
    }

    public FrescoLoader desireAspectRatio(float ratio) {
        this.mDesiredAspectRatio = ratio;
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

    public FrescoLoader background(Drawable drawable) {
        this.mBackgroundDrawable = drawable;
        return this;
    }

    public FrescoLoader cornersRadius(int radius) {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setCornersRadius(radius);
        return this;
    }

    public FrescoLoader roundAsCircle(@ColorInt int borderColor, int borderWidth) {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setBorder(borderColor, borderWidth);
        this.mRoundingParams.setRoundAsCircle(true);
        return this;
    }

    public FrescoLoader cornersRadii(
            float topLeft,
            float topRight,
            float bottomRight,
            float bottomLeft) {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setCornersRadii(topLeft, topRight, bottomRight, bottomLeft);
        return this;
    }

    public FrescoLoader fadeDuration(int duration) {
        this.mFadeDuration = duration;
        return this;
    }

    public FrescoLoader autoPlayAnimation(boolean enable) {
        this.mAutoPlayAnimations = enable;
        return this;
    }

    public FrescoLoader localThumbnailPreviewsEnabled(boolean enable) {
        this.mLocalThumbnailPreviewsEnabled = enable;
        return this;
    }

    public void into(ImageView targetView) {
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(mContext.getResources())
                .setFadeDuration(mFadeDuration)
                .setPlaceholderImage(mPlaceHolderDrawable, mPlaceholderScaleType)
                .setFailureImage(mFailureDrawable, mFailureScaleType)
                .setProgressBarImage(mProgressBarDrawable, mProgressBarScaleType)
                .setActualImageScaleType(mActualImageScaleType)
                .setActualImageFocusPoint(mActualImageFocusPoint)
                .setBackground(mBackgroundDrawable)
                .setDesiredAspectRatio(mDesiredAspectRatio)
                .setRoundingParams(mRoundingParams)
                .build();
        mDraweeHolder.setHierarchy(hierarchy);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(mUri)
                .setResizeOptions(mResizeOptions)
                .setLocalThumbnailPreviewsEnabled(mLocalThumbnailPreviewsEnabled)
                .setRequestPriority(Priority.HIGH)
                .setRotationOptions(mRotationOptions)
                .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setOldController(mDraweeHolder.getController())
                .setImageRequest(request)
                .setAutoPlayAnimations(mAutoPlayAnimations)
                .setTapToRetryEnabled(false)
                .build();
        mDraweeHolder.setController(draweeController);

        targetView.removeOnAttachStateChangeListener(this);
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
