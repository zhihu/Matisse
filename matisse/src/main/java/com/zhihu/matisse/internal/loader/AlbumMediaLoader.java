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
package com.zhihu.matisse.internal.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.utils.MediaStoreCompat;

/**
 * Load images and videos into a single cursor.
 */
public class AlbumMediaLoader extends CursorLoader {
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            "duration"};
    private static final String SELECTION_ALL =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    private static final String[] SELECTION_ALL_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };
    private static final String SELECTION_ALBUM =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
            + " AND "
            + " bucket_id=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    private static String[] getSelectionAlbumArgs(String albumId) {
        return new String[] {
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                albumId
        };
    }
    private static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
    private final boolean mEnableCapture;

    private AlbumMediaLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs,
                             String sortOrder, boolean capture) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        mEnableCapture = capture;
    }

    public static CursorLoader newInstance(Context context, Album album, boolean capture) {
        if (album.isAll()) {
            return new AlbumMediaLoader(
                    context,
                    QUERY_URI,
                    PROJECTION,
                    SELECTION_ALL,
                    SELECTION_ALL_ARGS,
                    ORDER_BY,
                    capture);
        } else {
            return new AlbumMediaLoader(
                    context,
                    QUERY_URI,
                    PROJECTION,
                    SELECTION_ALBUM,
                    getSelectionAlbumArgs(album.getId()),
                    ORDER_BY,
                    false);
        }
    }

    @Override
    public Cursor loadInBackground() {
        Cursor result = super.loadInBackground();
        if (!mEnableCapture || !MediaStoreCompat.hasCameraFeature(getContext())) {
            return result;
        }
        MatrixCursor dummy = new MatrixCursor(PROJECTION);
        dummy.addRow(new Object[]{Item.ITEM_ID_CAPTURE, Item.ITEM_DISPLAY_NAME_CAPTURE, "", 0, 0});
        return new MergeCursor(new Cursor[]{dummy, result});
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}
