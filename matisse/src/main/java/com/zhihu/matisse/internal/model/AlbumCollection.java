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

import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.loader.AlbumLoader;
import com.zhihu.matisse.internal.ui.MediaSelectionFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class AlbumCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    private static final String STATE_CURRENT_SELECTION = "state_current_selection";
    private WeakReference<Context> mContext;
    private WeakReference<MediaSelectionFragment.SelectionProvider> mSelectionProvider;
    private LoaderManager mLoaderManager;
    private AlbumCallbacks mCallbacks;
    private Cursor mCursor;
    private int mCurrentSelection;
    private String mCurrentSelectionId;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }
        return new AlbumLoader(context, new AlbumLoader.OnAlbumContentChangedListener() {
            @Override
            public void onAlbumContentChanged() {
                loadAlbums(true);
            }
        });
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
        mCursor = null;
        mCallbacks = null;
    }

    public void setSelectionProvider(MediaSelectionFragment.SelectionProvider selectionProvider) {
        mSelectionProvider =
                new WeakReference<MediaSelectionFragment.SelectionProvider>(selectionProvider);
    }

    public void loadAlbums() {
        loadAlbums(false);
    }

    public void loadAlbums(boolean forceLoad) {
        if (forceLoad) {
            mLoaderManager.restartLoader(LOADER_ID, null, this);
        }
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    public int getCurrentSelection() {
        ensureCursor();
        mCursor.moveToPosition(mCurrentSelection);
        if (!mCursor.getString(mCursor.getColumnIndex("bucket_id")).equals(mCurrentSelectionId)) {
            // Albums has changed, we need to invalidate all selections.
            if (mSelectionProvider != null) {
                // SelectionProvider's lifespan is longer than this, no need to check its nullity.
                mSelectionProvider.get()
                        .provideSelectedItemCollection()
                        .overwrite(new ArrayList<Item>(), 0 /* this parameter is ignored*/);
            }
            setStateCurrentSelection(0);
        }
        return mCurrentSelection;
    }

    public void setStateCurrentSelection(int currentSelection) {
        mCurrentSelection = currentSelection;
        ensureCursor();
        mCursor.moveToPosition(currentSelection);
        mCurrentSelectionId = mCursor.getString(mCursor.getColumnIndex("bucket_id"));
    }

    private void ensureCursor() {
        if (mCursor == null || mCursor.isClosed()) {
            throw new IllegalStateException("Contents is dirty or need reloading");
        }
    }

    public interface AlbumCallbacks {
        void onAlbumLoad(Cursor cursor);

        void onAlbumReset();
    }
}
