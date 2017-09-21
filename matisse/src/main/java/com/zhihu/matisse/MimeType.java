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

import android.content.ContentResolver;
import android.net.Uri;
import android.text.TextUtils;
import android.support.v4.util.ArraySet;
import android.webkit.MimeTypeMap;

import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * MIME Type enumeration to restrict selectable media on the selection activity. Matisse only supports images and
 * videos.
 * <p>
 * Good example of mime types Android supports:
 * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/MediaFile.java
 */
@SuppressWarnings("unused")
public enum MimeType {

    // ============== images ==============
    JPEG("image/jpeg", arraySetOf(
            "jpg",
            "jpeg"
    )),
    PNG("image/png", arraySetOf(
            "png"
    )),
    GIF("image/gif", arraySetOf(
            "gif"
    )),
    BMP("image/x-ms-bmp", arraySetOf(
            "bmp"
    )),
    WEBP("image/webp", arraySetOf(
            "webp"
    )),

    // ============== videos ==============
    MPEG("video/mpeg", arraySetOf(
            "mpeg",
            "mpg"
    )),
    MP4("video/mp4", arraySetOf(
            "mp4",
            "m4v"
    )),
    QUICKTIME("video/quicktime", arraySetOf(
            "mov"
    )),
    THREEGPP("video/3gpp", arraySetOf(
            "3gp",
            "3gpp"
    )),
    THREEGPP2("video/3gpp2", arraySetOf(
            "3g2",
            "3gpp2"
    )),
    MKV("video/x-matroska", arraySetOf(
            "mkv"
    )),
    WEBM("video/webm", arraySetOf(
            "webm"
    )),
    TS("video/mp2ts", arraySetOf(
            "ts"
    )),
    AVI("video/avi", arraySetOf(
            "avi"
    ));

    private final String mMimeTypeName;
    private final Set<String> mExtensions;

    MimeType(String mimeTypeName, Set<String> extensions) {
        mMimeTypeName = mimeTypeName;
        mExtensions = extensions;
    }

    public static Set<MimeType> ofAll() {
        return EnumSet.allOf(MimeType.class);
    }

    public static Set<MimeType> of(MimeType type, MimeType... rest) {
        return EnumSet.of(type, rest);
    }

    public static Set<MimeType> ofImage() {
        return EnumSet.of(JPEG, PNG, GIF, BMP, WEBP);
    }

    public static Set<MimeType> ofVideo() {
        return EnumSet.of(MPEG, MP4, QUICKTIME, THREEGPP, THREEGPP2, MKV, WEBM, TS, AVI);
    }

    private static Set<String> arraySetOf(String... suffixes) {
        return new ArraySet<>(Arrays.asList(suffixes));
    }

    @Override
    public String toString() {
        return mMimeTypeName;
    }

    public boolean checkType(ContentResolver resolver, Uri uri) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        if (uri == null) {
            return false;
        }
        String type = map.getExtensionFromMimeType(resolver.getType(uri));
        String path = null;
        // lazy load the path and prevent resolve for multiple times
        boolean pathParsed = false;
        for (String extension : mExtensions) {
            if (extension.equals(type)) {
                return true;
            }
            if (!pathParsed) {
                // we only resolve the path for one time
                path = PhotoMetadataUtils.getPath(resolver, uri);
                if (!TextUtils.isEmpty(path)) {
                    path = path.toLowerCase(Locale.US);
                }
                pathParsed = true;
            }
            if (path != null && path.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
