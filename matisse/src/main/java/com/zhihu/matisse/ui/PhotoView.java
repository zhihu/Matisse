package com.zhihu.matisse.ui;

/**
 * author: EwenQin
 * since : 2017/7/28 下午2:13.
 */
@SuppressWarnings("unused")
public interface PhotoView {

    /**
     * Call after the photo view was created
     */
    void onViewCreated();

    /**
     * Resume all actions while previewing the image
     */
    void resetView();

    /**
     * Determines whether this ImageViewTouch can be scrolled.
     *
     * @param direction - positive direction value means scroll from right to left,
     *                  negative value means scroll from left to right
     * @return true if there is some more place to scroll, false - otherwise.
     */
    boolean canScroll(int direction);
}
