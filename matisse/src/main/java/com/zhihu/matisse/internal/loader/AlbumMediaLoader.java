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
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.utils.MediaStoreCompat;

/**
 * Load images and videos into a single cursor.
 */
public class AlbumMediaLoader extends CursorLoader {
    public enum Capture {
        Video, Image, All, Nothing
    }

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            "duration"};

    // === params for album ALL && showSingleMediaType: false ===
    private static final String SELECTION_ALL =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    private static final String[] SELECTION_ALL_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };
    // ===========================================================

    // === params for album ALL && showSingleMediaType: true ===
    private static final String SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }
    // =========================================================

    // === params for ordinary album && showSingleMediaType: false ===
    private static final String SELECTION_ALBUM =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static String[] getSelectionAlbumArgs(String albumId) {
        return new String[]{
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                albumId
        };
    }
    // ===============================================================

    // === params for ordinary album && showSingleMediaType: true ===
    private static final String SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static String[] getSelectionAlbumArgsForSingleMediaType(int mediaType, String albumId) {
        return new String[]{String.valueOf(mediaType), albumId};
    }
    // ===============================================================

    private static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";
    private final Capture mCapture;

    private AlbumMediaLoader(Context context, String selection, String[] selectionArgs,
                             Capture capture) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY);
        mCapture = capture;
    }

    public static CursorLoader newInstance(Context context, Album album, Capture capture) {
        String selection;
        String[] selectionArgs;
        Capture mCapture = capture;

        if (album.isAll()) {
            if (SelectionSpec.getInstance().onlyShowImages()) {
                selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                if (capture == Capture.Image
                        || capture == Capture.All) {
                    mCapture = Capture.Image;
                }
            } else if (SelectionSpec.getInstance().onlyShowVideos()) {
                selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                if (capture == Capture.Video
                        || capture == Capture.All) {
                    mCapture = Capture.Video;
                }
            } else {
                selection = SELECTION_ALL;
                selectionArgs = SELECTION_ALL_ARGS;
                mCapture = capture;
            }
        } else {
            if (SelectionSpec.getInstance().onlyShowImages()) {
                selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                        album.getId());
            } else if (SelectionSpec.getInstance().onlyShowVideos()) {
                selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                        album.getId());
            } else {
                selection = SELECTION_ALBUM;
                selectionArgs = getSelectionAlbumArgs(album.getId());
            }
            mCapture = Capture.Nothing;
        }
        return new AlbumMediaLoader(context, selection, selectionArgs, mCapture);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor result = super.loadInBackground();
        if (!MediaStoreCompat.hasCameraFeature(getContext())) {
            return result;
        }
        MatrixCursor dummy = new MatrixCursor(PROJECTION);
        switch (mCapture) {
            case All:
                dummy.addRow(new Object[]{Item.ITEM_ID_CAPTURE_PHOTO,
                        Item.ITEM_DISPLAY_NAME_CAPTURE_PHOTO, "", 0, 0});
                dummy.addRow(new Object[]{Item.ITEM_ID_CAPTURE_VIDEO,
                        Item.ITEM_DISPLAY_NAME_CAPTURE_VIDEO, "", 0, 0});
                break;
            case Image:
                dummy.addRow(new Object[]{Item.ITEM_ID_CAPTURE_PHOTO,
                        Item.ITEM_DISPLAY_NAME_CAPTURE_PHOTO, "", 0, 0});
                break;
            case Video:
                dummy.addRow(new Object[]{Item.ITEM_ID_CAPTURE_VIDEO,
                        Item.ITEM_DISPLAY_NAME_CAPTURE_VIDEO, "", 0, 0});
                break;
            default:
                return result;
        }
        return new MergeCursor(new Cursor[]{dummy, result});
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}
