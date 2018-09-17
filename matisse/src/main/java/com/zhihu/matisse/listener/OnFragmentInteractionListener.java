package com.zhihu.matisse.listener;

/**
 * @author: zhuyongging
 * @date: 2018-09-17
 * @desc  PreViewItemFragment 和  BasePreViewActivity 通信的接口 ，为了方便拿到 ImageViewTouch 的点击事件
 */
public interface OnFragmentInteractionListener {
    /**
     *  ImageViewTouch 被点击了
     */
    void onClick();
}
