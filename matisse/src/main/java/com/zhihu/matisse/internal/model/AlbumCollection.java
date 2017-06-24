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
package com.zhihu.matisse.internal.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.zhihu.matisse.internal.loader.AlbumLoader;

import java.lang.ref.WeakReference;

public class AlbumCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    private static final String STATE_CURRENT_SELECTION = "state_current_selection";
    private WeakReference<Context> mContext;
    private LoaderManager mLoaderManager;
    private AlbumCallbacks mCallbacks;
    private Cursor mCursor;
    private int mCurrentSelection = -1;
    private String mCurrentSelectionId;  // Used to track the real bucket.
    private boolean mContentsDirty;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }
        return AlbumLoader.newInstance(context);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCursor = data;

        mCallbacks.onAlbumLoad(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCursor = null;

        mCallbacks.onAlbumReset();
    }

    public void onCreate(FragmentActivity activity, AlbumCallbacks callbacks) {
        mContext = new WeakReference<Context>(activity);
        mLoaderManager = activity.getSupportLoaderManager();
        mCallbacks = callbacks;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        mCurrentSelection = savedInstanceState.getInt(STATE_CURRENT_SELECTION);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_SELECTION, mCurrentSelection);
    }

    public void onDestroy() {
        mLoaderManager.destroyLoader(LOADER_ID);
        mCallbacks = null;
    }

    public void loadAlbums() {
        loadAlbums(false);
    }

    public void loadAlbums(boolean force) {
        if (force) {
            Loader<?> existingLoader = mLoaderManager.getLoader(LOADER_ID);
            if (existingLoader != null) {
                existingLoader.forceLoad();
                return;
            }
        }

        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    /**
     * Takes and clears the current flag indicating whether the contents are dirty.
     *
     * @return the flag
     */
    public boolean takeContentsDirty() {
        boolean res = mContentsDirty;
        mContentsDirty = false;
        return res;
    }

    public int getCurrentSelection() {
        if (mCurrentSelection == -1) {
            setStateCurrentSelection(0);
        }

        if (mCurrentSelectionId == null
            || !mCurrentSelectionId.equals(getBucketId(mCurrentSelection))) {
            // The bucket current selection underlying is changed, reset the selection.
            setStateCurrentSelection(0);
            mContentsDirty = true;
        }

        return mCurrentSelection;
    }

    public void setStateCurrentSelection(int currentSelection) {
        mCurrentSelection = currentSelection;

        mCurrentSelectionId = getBucketId(currentSelection);
    }

    private String getBucketId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getString(mCursor.getColumnIndex("bucket_id"));
    }

    public interface AlbumCallbacks {
        void onAlbumLoad(Cursor cursor);

        void onAlbumReset();
    }
}
