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
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.util.LogTime;
import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.model.SelectedItemCollection;
import com.zhihu.matisse.internal.ui.widget.CheckView;
import com.zhihu.matisse.internal.ui.widget.MediaGrid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AlbumMediaAdapter extends
        RecyclerViewCursorAdapter<RecyclerView.ViewHolder> implements
        MediaGrid.OnMediaGridClickListener {
    private static final String TAG = "AlbumMediaAdapter";
    private static final int VIEW_TYPE_CAPTURE = 0x01;
    private static final int VIEW_TYPE_MEDIA = 0x02;
    private static final int VIEW_TYPE_DATE = 0x03;
    private final SelectedItemCollection mSelectedCollection;
    private final Drawable mPlaceholder;
    private SelectionSpec mSelectionSpec;
    private CheckStateListener mCheckStateListener;
    private OnMediaClickListener mOnMediaClickListener;
    private RecyclerView mRecyclerView;
    private int mImageResize;
    private int mDateCount = 0;
    /**
     * key: the position of RecyclerView position
     * value: the position of cursor position
     */
    private Map<Integer, Integer> viewToCursorMap = new HashMap<>();
    /**
     * key: the position of cursor position
     * value: the position of relative date position
     */
    private SparseIntArray cursorToDateMap = new SparseIntArray();
    private List<String> mDateList = new ArrayList<>();
    private List<Uri> mSelectedUris ;

    public AlbumMediaAdapter(Context context, SelectedItemCollection selectedCollection, RecyclerView recyclerView, List<Uri> selectedUris) {
        super(null);
        mSelectionSpec = SelectionSpec.getInstance();
        mSelectedCollection = selectedCollection;
        mSelectedUris = selectedUris;
        TypedArray ta = context.getTheme().obtainStyledAttributes(new int[]{R.attr.item_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();

        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        if (isDataValid(mCursor)) {
            if (mSelectionSpec.groupByDate) {
                return mCursor.getCount() + getDateCountFromCursor(mCursor);
            }
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    private int getDateCountFromCursor(Cursor mCursor) {
        if (mDateCount != 0) {
            return mDateCount;
        }
        viewToCursorMap.clear();
        cursorToDateMap.clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINA);
        mDateList = new ArrayList<>();
        viewToCursorMap.put(0, null);
        while (mCursor.moveToNext()) {
            String date = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
            if (!TextUtils.isEmpty(date)) {
                String format = dateFormat.format(new Date(Long.valueOf(date)));
                if (!mDateList.contains(format)) {
                    mDateList.add(format);
                }
                int cursorPos = mCursor.getPosition();
                cursorToDateMap.put(mCursor.getPosition(), mDateList.size() - 1);

                if (cursorToDateMap.size() > 1 && cursorToDateMap.get(cursorPos) != cursorToDateMap.get(cursorPos - 1)) {
                    viewToCursorMap.put(viewToCursorMap.size(), null);
                }
                viewToCursorMap.put(viewToCursorMap.size(), cursorPos);
            }
        }
        mDateCount = mDateList.size();
        return mDateList.size();
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
        } else if (viewType == VIEW_TYPE_MEDIA) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_grid_item, parent, false);
            return new MediaViewHolder(v);
        } else if (viewType == VIEW_TYPE_DATE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_date_item, parent, false);
            return new MediaDateViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!mSelectionSpec.groupByDate) {
            super.onBindViewHolder(holder, position);
        } else {
            if (!isDataValid(mCursor)) {
                throw new IllegalStateException("Cannot bind view holder when cursor is in invalid state.");
            }

            Integer cursorPos = viewToCursorMap.get(position);
            if (cursorPos != null) {
                mCursor.moveToPosition(cursorPos);
            }

            onBindViewHolder(holder, mCursor);
        }
    }

    @Override
    protected void onBindViewHolder(final RecyclerView.ViewHolder holder, Cursor cursor) {
        if (holder instanceof CaptureViewHolder) {
            CaptureViewHolder captureViewHolder = (CaptureViewHolder) holder;
            Drawable[] drawables = captureViewHolder.mHint.getCompoundDrawables();
            TypedArray ta = holder.itemView.getContext().getTheme().obtainStyledAttributes(
                    new int[]{R.attr.capture_textColor});
            int color = ta.getColor(0, 0);
            ta.recycle();
            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
            }
        } else if (holder instanceof MediaViewHolder) {
            MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;

            final Item item = Item.valueOf(cursor);

            if (mSelectedUris.contains(item.uri) && !mSelectedCollection.isSelected(item)) {
                mSelectedCollection.add(item);
                if (mCheckStateListener != null) {
                    mCheckStateListener.onUpdate();
                }
                //onCheckViewClicked((CheckView) mediaViewHolder.mMediaGrid.findViewById(R.id.check_view), item, mediaViewHolder);
            }
            
            mediaViewHolder.mMediaGrid.preBindMedia(new MediaGrid.PreBindInfo(
                    getImageResize(mediaViewHolder.mMediaGrid.getContext()),
                    mPlaceholder,
                    mSelectionSpec.countable,
                    holder
            ));
            mediaViewHolder.mMediaGrid.bindMedia(item);
            mediaViewHolder.mMediaGrid.setOnMediaGridClickListener(this);
            setCheckStatus(item, mediaViewHolder.mMediaGrid);
        } else if (holder instanceof MediaDateViewHolder) {
            MediaDateViewHolder mediaDateViewHolder = (MediaDateViewHolder) holder;
            mediaDateViewHolder.mDate.setText("");
            int curPos;
            if (viewToCursorMap.get(holder.getAdapterPosition()) == null) {
                curPos = viewToCursorMap.get(holder.getAdapterPosition() + 1);
            } else {
                curPos = viewToCursorMap.get(holder.getAdapterPosition());
            }
            mediaDateViewHolder.mDate.setText(mDateList.get(cursorToDateMap.get(curPos)));
        }
    }

    private void setCheckStatus(Item item, MediaGrid mediaGrid) {
        if (mSelectionSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum > 0) {
                mediaGrid.setCheckEnabled(true);
                mediaGrid.setCheckedNum(checkedNum);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    mediaGrid.setCheckEnabled(false);
                    mediaGrid.setCheckedNum(CheckView.UNCHECKED);
                } else {
                    mediaGrid.setCheckEnabled(true);
                    mediaGrid.setCheckedNum(checkedNum);
                }
            }
        } else {
            boolean selected = mSelectedCollection.isSelected(item);
            if (selected) {
                mediaGrid.setCheckEnabled(true);
                mediaGrid.setChecked(true);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    mediaGrid.setCheckEnabled(false);
                    mediaGrid.setChecked(false);
                } else {
                    mediaGrid.setCheckEnabled(true);
                    mediaGrid.setChecked(false);
                }
            }
        }
    }

    @Override
    public void onThumbnailClicked(ImageView thumbnail, Item item, RecyclerView.ViewHolder holder) {
        if (mOnMediaClickListener != null) {
            mOnMediaClickListener.onMediaClick(null, item, holder.getAdapterPosition());
        }
    }

    @Override
    public void onCheckViewClicked(CheckView checkView, Item item, RecyclerView.ViewHolder holder) {
        if (mSelectionSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum == CheckView.UNCHECKED) {
                if (assertAddSelection(holder.itemView.getContext(), item)) {
                    mSelectedCollection.add(item);
                    if (!mSelectedUris.contains(item.uri)) {
                        mSelectedUris.add(item.uri);
                    }
                    notifyCheckStateChanged();
                }
            } else {
                mSelectedCollection.remove(item);
                mSelectedUris.remove(item.uri);
                notifyCheckStateChanged();
            }
        } else {
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item);
                mSelectedUris.remove(item.uri);
                notifyCheckStateChanged();
            } else {
                if (assertAddSelection(holder.itemView.getContext(), item)) {
                    mSelectedCollection.add(item);
                    if (mSelectedUris.contains(item.uri)) {
                        mSelectedUris.add(item.uri);
                    }
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
    public int getItemViewType(int position) {
        if (!mSelectionSpec.groupByDate) {
            return super.getItemViewType(position);
        }
        //enable groupByDate
        if (viewToCursorMap.get(position) == null) {
            return VIEW_TYPE_DATE;
        } else {
            return VIEW_TYPE_MEDIA;
        } 

        /*int currentDatePos = cursorToDateMap.get(viewToCursorMap.get(position));
        int lastDatePos = cursorToDateMap.get(viewToCursorMap.get(position - 1));
        //represent this image is in same day with last image
        if (mDateList.get(currentDatePos).equals(mDateList.get(lastDatePos))) {
            return VIEW_TYPE_MEDIA;
        } else {
            return VIEW_TYPE_DATE;
        }*/
    }

    @Override
    public int getItemViewType(int position, Cursor cursor) {
        return Item.valueOf(cursor).isCapture() ? VIEW_TYPE_CAPTURE : VIEW_TYPE_MEDIA;
    }

    @Override
    public long getItemId(int position) {
        if (!mSelectionSpec.groupByDate) {
            return super.getItemId(position);
        }

        //enable groupByDate
        if (!isDataValid(mCursor)) {
            throw new IllegalStateException("Cannot lookup item id when cursor is in invalid state.");
        }

        return position;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (getItemViewType(position) == VIEW_TYPE_DATE) {
                    return mSelectionSpec.spanCount;
                } else {
                    return 1;
                }
            }
        });

    }

    private boolean assertAddSelection(Context context, Item item) {
        IncapableCause cause = mSelectedCollection.isAcceptable(item);
        IncapableCause.handleCause(context, cause);
        return cause == null;
    }

    public void registerCheckStateListener(CheckStateListener listener) {
        mCheckStateListener = listener;
    }

    public void unregisterCheckStateListener() {
        mCheckStateListener = null;
    }

    public void registerOnMediaClickListener(OnMediaClickListener listener) {
        mOnMediaClickListener = listener;
    }

    public void unregisterOnMediaClickListener() {
        mOnMediaClickListener = null;
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
            if (holder instanceof MediaViewHolder) {
                if (cursor.moveToPosition(i)) {
                    setCheckStatus(Item.valueOf(cursor), ((MediaViewHolder) holder).mMediaGrid);
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
                    R.dimen.media_grid_spacing) * (spanCount - 1);
            mImageResize = availableWidth / spanCount;
            mImageResize = (int) (mImageResize * mSelectionSpec.thumbnailScale);
        }
        return mImageResize;
    }

    public interface CheckStateListener {
        void onUpdate();
    }

    public interface OnMediaClickListener {
        void onMediaClick(Album album, Item item, int adapterPosition);
    }

    public interface OnPhotoCapture {
        void capture();
    }

    private static class MediaViewHolder extends RecyclerView.ViewHolder {

        private MediaGrid mMediaGrid;

        MediaViewHolder(View itemView) {
            super(itemView);
            mMediaGrid = (MediaGrid) itemView;
        }
    }

    private static class CaptureViewHolder extends RecyclerView.ViewHolder {

        private TextView mHint;

        CaptureViewHolder(View itemView) {
            super(itemView);

            mHint = (TextView) itemView.findViewById(R.id.hint);
        }
    }

    private static class MediaDateViewHolder extends RecyclerView.ViewHolder {
        private TextView mDate;

        public MediaDateViewHolder(View itemView) {
            super(itemView);
            mDate = (TextView) itemView;
        }
    }

}
