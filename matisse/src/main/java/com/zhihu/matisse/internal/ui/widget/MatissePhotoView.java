package com.zhihu.matisse.internal.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.zhihu.matisse.ui.PhotoView;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * author: EwenQin
 * since : 2017/7/28 下午2:10.
 */

public class MatissePhotoView extends ImageViewTouch implements PhotoView {

    public MatissePhotoView(Context context) {
        super(context);
    }

    public MatissePhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatissePhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override public void onViewCreated() {
        setDisplayType(DisplayType.FIT_TO_SCREEN);
    }

    @Override public void resetView() {
        resetMatrix();
    }

    @Override public boolean canScroll(int direction) {
        return super.canScroll(direction);
    }
}
