package com.zhihu.matisse.listener;

/**
 *  PreViewItemFragment 和  BasePreViewActivity 通信的接口 ，为了方便拿到 ImageViewTouch 的点击事件
 */
public interface OnFragmentInteractionListener {
    /**
     *  ImageViewTouch 被点击了
     */
    void onClick();
}
