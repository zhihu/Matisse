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
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.zhihu.matisse.internal.entity.Album;

public class AlbumLoader extends CursorLoader {
    public static final String COLUMN_COUNT = "count";
    private static final String[] COLUMNS = {MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media._ID, COLUMN_COUNT};
    private static final String[] PROJECTION = {MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media._ID, "COUNT(*) AS " + COLUMN_COUNT};
    private static final String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
    private static final String BUCKET_ORDER_BY = "MAX(" + MediaStore.Images.Media.DATE_TAKEN + ") DESC";
    private static final String MEDIA_ID_DUMMY = String.valueOf(-1);

    public AlbumLoader(Context context) {
        // SELECT bucket_id, bucket_display_name, _id, COUNT(*) AS count FROM images WHERE (1)
        // GROUP BY 1,(2) ORDER BY MAX(datetaken) DESC
        super(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION, BUCKET_GROUP_BY, null,
                BUCKET_ORDER_BY);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor albums = super.loadInBackground();
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);
        int totalCount = 0;
        while (albums.moveToNext()) {
            totalCount += albums.getInt(albums.getColumnIndex(COLUMN_COUNT));
        }
        String allAlbumId;
        if (albums.moveToFirst()) {
            allAlbumId = albums.getString(albums.getColumnIndex(MediaStore.Images.Media._ID));
        } else {
            allAlbumId = MEDIA_ID_DUMMY;
        }
        allAlbum.addRow(new String[]{Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, allAlbumId, String.valueOf(totalCount)});

        return new MergeCursor(new Cursor[]{allAlbum, albums});
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}