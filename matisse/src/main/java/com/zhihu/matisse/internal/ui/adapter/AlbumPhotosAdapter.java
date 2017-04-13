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
package com.zhihu.matisse.internal.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.entity.UncapableCause;
import com.zhihu.matisse.internal.model.SelectedItemCollection;
import com.zhihu.matisse.internal.ui.widget.CheckView;
import com.zhihu.matisse.internal.ui.widget.PhotoGrid;

public class AlbumPhotosAdapter extends
        RecyclerViewCursorAdapter<RecyclerView.ViewHolder> implements
        PhotoGrid.OnPhotoGridClickListener {

    private static final int VIEW_TYPE_CAPTURE = 0x01;
    private static final int VIEW_TYPE_PHOTO = 0x02;
    private final SelectedItemCollection mSelectedCollection;
    private final Drawable mPlaceholder;
    private SelectionSpec mSelectionSpec;
    private CheckStateListener mCheckStateListener;
    private OnPhotoClickListener mOnPhotoClickListener;
    private RecyclerView mRecyclerView;
    private int mImageResize;

    public AlbumPhotosAdapter(Context context, SelectedItemCollection selectedCollection, RecyclerView recyclerView) {
        super(null);
        mSelectionSpec = SelectionSpec.getInstance();
        mSelectedCollection = selectedCollection;

        TypedArray ta = context.getTheme().obtainStyledAttributes(new int[]{R.attr.item_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();

        mRecyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CAPTURE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_capture_item, parent, false);
            CaptureViewHolder holder = new CaptureViewHolder(v);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getContext() instanceof OnPhotoCapture) {
                        ((OnPhotoCapture) v.getContext()).capture();
                    }
                }
            });
            return holder;
        } else if (viewType == VIEW_TYPE_PHOTO) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_grid_item, parent, false);
            return new PhotoViewHolder(v);
        }
        return null;
    }

    @Override
    protected void onBindViewHolder(final RecyclerView.ViewHolder holder, Cursor cursor) {
        if (holder instanceof CaptureViewHolder) {
            CaptureViewHolder captureViewHolder = (CaptureViewHolder) holder;
            Drawable[] drawables = captureViewHolder.mHint.getCompoundDrawables();
            TypedArray ta = holder.itemView.getContext().getTheme().obtainStyledAttributes(
                    new int[]{R.attr.capture_color});
            int color = ta.getColor(0, 0);
            ta.recycle();
            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
            }
        } else if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;

            final Item item = Item.valueOf(cursor);
            photoViewHolder.mPhotoGrid.preBindPhoto(new PhotoGrid.PreBindInfo(
                    getImageResize(photoViewHolder.mPhotoGrid.getContext()),
                    mPlaceholder,
                    mSelectionSpec.countable,
                    holder
            ));
            photoViewHolder.mPhotoGrid.bindPhoto(item);
            photoViewHolder.mPhotoGrid.setOnPhotoGridClickListener(this);
            setCheckStatus(item, photoViewHolder.mPhotoGrid);
        }
    }

    private void setCheckStatus(Item item, PhotoGrid photoGrid) {
        if (mSelectionSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum > 0) {
                photoGrid.setCheckEnabled(true);
                photoGrid.setCheckedNum(checkedNum);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    photoGrid.setCheckEnabled(false);
                    photoGrid.setCheckedNum(CheckView.UNCHECKED);
                } else {
                    photoGrid.setCheckEnabled(true);
                    photoGrid.setCheckedNum(checkedNum);
                }
            }
        } else {
            boolean selected = mSelectedCollection.isSelected(item);
            if (selected) {
                photoGrid.setCheckEnabled(true);
                photoGrid.setChecked(true);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    photoGrid.setCheckEnabled(false);
                    photoGrid.setChecked(false);
                } else {
                    photoGrid.setCheckEnabled(true);
                    photoGrid.setChecked(false);
                }
            }
        }
    }

    @Override
    public void onThumbnailClicked(ImageView thumbnail, Item photo, RecyclerView.ViewHolder holder) {
        if (mOnPhotoClickListener != null) {
            mOnPhotoClickListener.onPhotoClick(null, photo, holder.getAdapterPosition());
        }
    }

    @Override
    public void onCheckViewClicked(CheckView checkView, Item photo, RecyclerView.ViewHolder holder) {
        if (mSelectionSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(photo);
            if (checkedNum == CheckView.UNCHECKED) {
                if (assertAddSelection(holder.itemView.getContext(), photo)) {
                    mSelectedCollection.add(photo);
                    notifyCheckStateChanged();
                }
            } else {
                mSelectedCollection.remove(photo);
                notifyCheckStateChanged();
            }
        } else {
            if (mSelectedCollection.isSelected(photo)) {
                mSelectedCollection.remove(photo);
                notifyCheckStateChanged();
            } else {
                if (assertAddSelection(holder.itemView.getContext(), photo)) {
                    mSelectedCollection.add(photo);
                    notifyCheckStateChanged();
                }
            }
        }
    }

    private void notifyCheckStateChanged() {
        notifyDataSetChanged();
        if (mCheckStateListener != null) {
            mCheckStateListener.onUpdate();
        }
    }

    @Override
    public int getItemViewType(int position, Cursor cursor) {
        return Item.valueOf(cursor).isCapture() ? VIEW_TYPE_CAPTURE : VIEW_TYPE_PHOTO;
    }

    private boolean assertAddSelection(Context context, Item item) {
        UncapableCause cause = mSelectedCollection.isAcceptable(item);
        UncapableCause.handleCause(context, cause);
        return cause == null;
    }

    public void registerCheckStateListener(CheckStateListener listener) {
        mCheckStateListener = listener;
    }

    public void unregisterCheckStateListener() {
        mCheckStateListener = null;
    }

    public void registerOnPhotoClickListener(OnPhotoClickListener listener) {
        mOnPhotoClickListener = listener;
    }

    public void unregisterOnPhotoClickListener() {
        mOnPhotoClickListener = null;
    }

    public void refreshSelection() {
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        if (first == -1 || last == -1) {
            return;
        }
        Cursor cursor = getCursor();
        for (int i = first; i <= last; i++) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(first);
            if (holder instanceof PhotoViewHolder) {
                if (cursor.moveToPosition(i)) {
                    setCheckStatus(Item.valueOf(cursor), ((PhotoViewHolder) holder).mPhotoGrid);
                }
            }
        }
    }

    private int getImageResize(Context context) {
        if (mImageResize == 0) {
            RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
            int spanCount = ((GridLayoutManager) lm).getSpanCount();
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int availableWidth = screenWidth - context.getResources().getDimensionPixelSize(
                    R.dimen.photo_grid_spacing) * (spanCount - 1);
            mImageResize = availableWidth / spanCount;
            mImageResize = (int) (mImageResize * mSelectionSpec.thumbnailScale);
        }
        return mImageResize;
    }

    public interface CheckStateListener {
        void onUpdate();
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(Album album, Item item, int adapterPosition);
    }

    public interface OnPhotoCapture {
        void capture();
    }

    private static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private PhotoGrid mPhotoGrid;

        PhotoViewHolder(View itemView) {
            super(itemView);
            mPhotoGrid = (PhotoGrid) itemView;
        }
    }

    private static class CaptureViewHolder extends RecyclerView.ViewHolder {

        private TextView mHint;

        CaptureViewHolder(View itemView) {
            super(itemView);

            mHint = (TextView) itemView.findViewById(R.id.hint);
        }
    }

}
