package com.zhihu.matisse.engine;

import android.view.View;
import android.view.ViewGroup;

/**
 * author: EwenQin
 * since : 2017/7/28 下午1:33.
 */
@SuppressWarnings("unused")
public interface EngineView {

    /**
     * Register callback to be invoked when the view hierarchy is attached to a window.
     *
     * @param l The {@link OnEngineViewAttachListener} to set
     */
    void setOnEngineViewAttachListener(OnEngineViewAttachListener l);

    /**
     * Get callback when the view hierarchy is attached to a window.
     *
     * @return The {@link OnEngineViewAttachListener} from this EngineView.
     */
    OnEngineViewAttachListener getOnEngineViewAttachListener();

    /**
     * Interface definition for a callback to be invoked when this view is attached
     * or detached from its window.
     */
    interface OnEngineViewAttachListener {

        /**
         * Called when the view is attached to a window.
         *
         * @param v The view that was attached
         */
        void onViewAttachedToWindow(View v);

        /**
         * Called when the view is detached from a window.
         *
         * @param v The view that was detached
         */
        void onViewDetachedFromWindow(View v);


        /**
         * Called after {@link #onViewStartTemporaryDetach} when the container is done
         * changing the view.
         *
         * @param v The view that was attached
         */
        void onViewFinishTemporaryDetach(View v);

        /**
         * This is called when a container is going to temporarily detach a child, with
         * {@link ViewGroup#detachViewFromParent(View) ViewGroup.detachViewFromParent}.
         * It will either be followed by {@link #onViewFinishTemporaryDetach} or
         * {@link #onViewDetachedFromWindow} when the container is done.
         *
         * @param v The view that was detached
         */
        void onViewStartTemporaryDetach(View v);
    }
}
