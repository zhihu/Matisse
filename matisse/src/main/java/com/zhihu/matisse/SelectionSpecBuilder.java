/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;

import com.zhihu.matisse.engine.ImageEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.ui.MatisseActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_BEHIND;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_USER;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LOCKED;

/**
 * Fluent API for building media select specification.
 */
@SuppressWarnings("unused")
public final class SelectionSpecBuilder {
    private final Matisse mMatisse;
    private final SelectionSpec mSelectionSpec;
    private final Set<MimeType> mMimeType;
    private int mThemeId;
    private int mOrientation;
    private boolean mCountable;
    private int mMaxSelectable;
    private List<Filter> mFilters;
    private boolean mCapture;
    private CaptureStrategy mCaptureStrategy;
    private int mSpanCount;
    private int mGridExpectedSize;
    private float mThumbnailScale;
    private ImageEngine mImageEngine;

    @IntDef({
            SCREEN_ORIENTATION_UNSPECIFIED,
            SCREEN_ORIENTATION_LANDSCAPE,
            SCREEN_ORIENTATION_PORTRAIT,
            SCREEN_ORIENTATION_USER,
            SCREEN_ORIENTATION_BEHIND,
            SCREEN_ORIENTATION_SENSOR,
            SCREEN_ORIENTATION_NOSENSOR,
            SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            SCREEN_ORIENTATION_SENSOR_PORTRAIT,
            SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
            SCREEN_ORIENTATION_REVERSE_PORTRAIT,
            SCREEN_ORIENTATION_FULL_SENSOR,
            SCREEN_ORIENTATION_USER_LANDSCAPE,
            SCREEN_ORIENTATION_USER_PORTRAIT,
            SCREEN_ORIENTATION_FULL_USER,
            SCREEN_ORIENTATION_LOCKED
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ScreenOrientation {
    }

    /**
     * Constructs a new specification builder on the context.
     *
     * @param matisse  a requester context wrapper.
     * @param mimeType MIME type set to select.
     */
    SelectionSpecBuilder(Matisse matisse, @NonNull Set<MimeType> mimeType) {
        mMatisse = matisse;
        mMimeType = mimeType;
        mSelectionSpec = SelectionSpec.getCleanInstance();
        mOrientation = SCREEN_ORIENTATION_UNSPECIFIED;
    }

    /**
     * Theme for media selecting Activity.
     * <p>
     * There are two built-in themes:
     * 1. com.zhihu.matisse.R.style.Matisse_Zhihu;
     * 2. com.zhihu.matisse.R.style.Matisse_Dracula
     * you can define a custom theme derived from the above ones or other themes.
     *
     * @param themeId theme resource id. Default value is com.zhihu.matisse.R.style.Matisse_Zhihu.
     * @return {@link SelectionSpecBuilder} for fluent API.
     */
    public SelectionSpecBuilder theme(@StyleRes int themeId) {
        mThemeId = themeId;
        return this;
    }

    /**
     * Show a auto-increased number or a check mark when user select media.
     *
     * @param countable true for a auto-increased number from 1, false for a check mark. Default
     *                  value is false.
     * @return {@link SelectionSpecBuilder} for fluent API.
     */
    public SelectionSpecBuilder countable(boolean countable) {
        mCountable = countable;
        return this;
    }

    /**
     * Maximum selectable count.
     *
     * @param maxSelectable Maximum selectable count. Default value is 1.
     * @return {@link SelectionSpecBuilder} for fluent API.
     */
    public SelectionSpecBuilder maxSelectable(int maxSelectable) {
        mMaxSelectable = maxSelectable;
        return this;
    }

    /**
     * Add filter to filter each selecting item.
     *
     * @param filter {@link Filter}
     * @return {@link SelectionSpecBuilder} for fluent API.
     */
    public SelectionSpecBuilder addFilter(Filter filter) {
        if (mFilters == null) {
            mFilters = new ArrayList<>();
        }
        mFilters.add(filter);
        return this;
    }

    /**
     * Determines whether the photo capturing is enabled or not on the media grid view.
     * <p>
     * If this value is set true, photo capturing entry will appear only on All Media's page.
     *
     * @param enable Whether to enable capturing or not. Default value is false;
     * @return {@link SelectionSpecBuilder} for fluent API.
     */
    public SelectionSpecBuilder capture(boolean enable) {
        mCapture = enable;
        return this;
    }

    /**
     * Capture strategy provided for the location to save photos including internal and external
     * storage and also a authority for {@link android.support.v4.content.FileProvider}.
     *
     * @param captureStrategy {@link CaptureStrategy}, needed only when capturing is enabled.
     * @return {@link SelectionSpecBuilder} for fluent API.
     */
    public SelectionSpecBuilder captureStrategy(CaptureStrategy captureStrategy) {
        mCaptureStrategy = captureStrategy;
        return this;
    }

    /**
     * Set the desired orientation of this activity.
     *
     * @param orientation An orientation constant as used in {@link ScreenOrientation}.
     *                    Default value is {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_PORTRAIT}.
     * @return {@link SelectionSpecBuilder} for fluent API.
     * @see Activity#setRequestedOrientation(int)
     */
    public SelectionSpecBuilder restrictOrientation(@ScreenOrientation int orientation) {
        mOrientation = orientation;
        return this;
    }

    /**
     * Set a fixed span count for the media grid. Same for different screen orientations.
     * <p>
     * This will be ignored when {@link #gridExpectedSize(int)} is set.
     *
     * @param spanCount Requested span count.
     * @return {@link SelectionSpecBuilder} for fluent API.
     */
    public SelectionSpecBuilder spanCount(int spanCount) {
        mSpanCount = spanCount;
        return this;
    }

    /**
     * Set expected size for media grid to adapt to different screen sizes. This won't necessarily
     * be applied cause the media grid should fill the view container. The measured media grid's
     * size will be as close to this value as possible.
     *
     * @param size Expected media grid size in pixel.
     * @return {@link SelectionSpecBuilder} for fluent API.
     */
    public SelectionSpecBuilder gridExpectedSize(int size) {
        mGridExpectedSize = size;
        return this;
    }

    /**
     * Photo thumbnail's scale compared to the View's size. It should be a float value in (0.0,
     * 1.0].
     *
     * @param scale Thumbnail's scale in (0.0, 1.0]. Default value is 0.5.
     * @return {@link SelectionSpecBuilder} for fluent API.
     */
    public SelectionSpecBuilder thumbnailScale(float scale) {
        mThumbnailScale = scale;
        return this;
    }

    /**
     * Provide an image engine.
     * <p>
     * There are two built-in image engines:
     * 1. {@link com.zhihu.matisse.engine.impl.GlideEngine}
     * 2. {@link com.zhihu.matisse.engine.impl.PicassoEngine}
     * And you can implement your own image engine.
     *
     * @param imageEngine {@link ImageEngine}
     * @return {@link SelectionSpecBuilder} for fluent API.
     */
    public SelectionSpecBuilder imageEngine(ImageEngine imageEngine) {
        mImageEngine = imageEngine;
        return this;
    }

    /**
     * Start to select media and wait for result.
     *
     * @param requestCode Identity of the request Activity or Fragment.
     */
    public void forResult(int requestCode) {
        Activity activity = mMatisse.getActivity();
        if (activity == null) {
            return;
        }

        mSelectionSpec.mimeTypeSet = mMimeType;
        if (mThemeId == 0) {
            mThemeId = R.style.Matisse_Zhihu;
        }
        mSelectionSpec.themeId = mThemeId;
        mSelectionSpec.orientation = mOrientation;

        if (mMaxSelectable <= 1) {
            mSelectionSpec.countable = false;
            mSelectionSpec.maxSelectable = 1;
        } else {
            mSelectionSpec.countable = mCountable;
            mSelectionSpec.maxSelectable = mMaxSelectable;
        }

        if (mFilters != null && mFilters.size() > 0) {
            mSelectionSpec.filters = mFilters;
        }
        mSelectionSpec.capture = mCapture;
        if (mCapture) {
            if (mCaptureStrategy == null) {
                throw new IllegalArgumentException("Don't forget to set CaptureStrategy.");
            }
            mSelectionSpec.captureStrategy = mCaptureStrategy;
        }

        if (mGridExpectedSize > 0) {
            mSelectionSpec.gridExpectedSize = mGridExpectedSize;
        } else {
            mSelectionSpec.spanCount = mSpanCount <= 0 ? 3 : mSpanCount;
        }

        if (mThumbnailScale < 0 || mThumbnailScale > 1.0f) {
            throw new IllegalArgumentException("Thumbnail scale must be between (0.0, 1.0]");
        }
        if (mThumbnailScale == 0) {
            mThumbnailScale = 0.5f;
        }
        mSelectionSpec.thumbnailScale = mThumbnailScale;

        mSelectionSpec.imageEngine = mImageEngine;

        Intent intent = new Intent(activity, MatisseActivity.class);

        Fragment fragment = mMatisse.getFragment();
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

}
