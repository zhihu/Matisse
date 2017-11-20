package com.zhihu.matisse.internal.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chaichuanfa on 2017/11/19.
 */

public class BitmapUtils {

    private BitmapUtils() {
    }

    public static File saveBitmap(Context context, Bitmap bitmap, File path) throws IOException {
        String fileName = "crop_" + System.currentTimeMillis() + ".png";
        if (!path.exists()) {
            path.mkdirs();
        }
        File localFile = new File(path.getPath(), fileName);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(localFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            return localFile;
        } finally {
            if(outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }
    }
}
