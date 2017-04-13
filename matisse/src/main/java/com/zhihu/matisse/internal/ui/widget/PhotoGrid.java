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
package com.zhihu.matisse.internal.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;

public class PhotoGrid extends SquareFrameLayout implements View.OnClickListener {

    private ImageView mThumbnail;
    private CheckView mCheckView;
    private ImageView mGifTag;

    private Item mPhoto;
    private PreBindInfo mPreBindInfo;
    private OnPhotoGridClickListener mListener;

    public PhotoGrid(Context context) {
        super(context);
        init(context);
    }

    public PhotoGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.photo_grid_content, this, true);

        mThumbnail = (ImageView) findViewById(R.id.photo_thumbnail);
        mCheckView = (CheckView) findViewById(R.id.check_view);
        mGifTag = (ImageView) findViewById(R.id.gif);

        mThumbnail.setOnClickListener(this);
        mCheckView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            if (v == mThumbnail) {
                mListener.onThumbnailClicked(mThumbnail, mPhoto, mPreBindInfo.mViewHolder);
            } else if (v == mCheckView) {
                mListener.onCheckViewClicked(mCheckView, mPhoto, mPreBindInfo.mViewHolder);
            }
        }
    }

    public void preBindPhoto(PreBindInfo info) {
        mPreBindInfo = info;
    }

    public void bindPhoto(Item item) {
        mPhoto = item;
        setGifTag();
        initCheckView();
        setImage();
    }

    public Item getPhoto() {
        return mPhoto;
    }

    private void setGifTag() {
        mGifTag.setVisibility(mPhoto.isGif() ? View.VISIBLE : View.GONE);
    }

    private void initCheckView() {
        mCheckView.setCountable(mPreBindInfo.mCheckViewCountable);
    }

    public void setCheckEnabled(boolean enabled) {
        mCheckView.setEnabled(enabled);
    }

    public void setCheckedNum(int checkedNum) {
        mCheckView.setCheckedNum(checkedNum);
    }

    public void setChecked(boolean checked) {
        mCheckView.setChecked(checked);
    }

    private void setImage() {
        if (mPhoto.isGif()) {
            SelectionSpec.getInstance().imageEngine.loadAnimatedGifThumbnail(getContext(), mPreBindInfo.mResize,
                    mPreBindInfo.mPlaceholder, mThumbnail, mPhoto.getContentUri());
        } else {
            SelectionSpec.getInstance().imageEngine.loadThumbnail(getContext(), mPreBindInfo.mResize,
                    mPreBindInfo.mPlaceholder, mThumbnail, mPhoto.getContentUri());
        }
    }

    public void setOnPhotoGridClickListener(OnPhotoGridClickListener listener) {
        mListener = listener;
    }

    public void removeOnPhotoGridClickListener() {
        mListener = null;
    }

    public interface OnPhotoGridClickListener {

        void onThumbnailClicked(ImageView thumbnail, Item photo, RecyclerView.ViewHolder holder);

        void onCheckViewClicked(CheckView checkView, Item photo, RecyclerView.ViewHolder holder);
    }

    public static class PreBindInfo {
        int mResize;
        Drawable mPlaceholder;
        boolean mCheckViewCountable;
        RecyclerView.ViewHolder mViewHolder;

        public PreBindInfo(int resize, Drawable placeholder, boolean checkViewCountable,
                           RecyclerView.ViewHolder viewHolder) {
            mResize = resize;
            mPlaceholder = placeholder;
            mCheckViewCountable = checkViewCountable;
            mViewHolder = viewHolder;
        }
    }

}
