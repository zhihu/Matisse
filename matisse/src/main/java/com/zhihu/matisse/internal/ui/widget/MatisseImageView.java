package com.zhihu.matisse.internal.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.zhihu.matisse.engine.EngineView;

/**
 * author: EwenQin
 * since : 2017/7/28 下午1:52.
 */
public class MatisseImageView extends AppCompatImageView implements EngineView {

    private OnEngineViewAttachListener mOnEngineViewAttachListener;

    public MatisseImageView(Context context) {
        super(context);
    }

    public MatisseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatisseImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public void setOnEngineViewAttachListener(OnEngineViewAttachListener l) {
        if (mOnEngineViewAttachListener != null) {
            mOnEngineViewAttachListener.onViewDetachedFromWindow(this);
        }

        mOnEngineViewAttachListener = l;
        if (mOnEngineViewAttachListener != null && ViewCompat.isAttachedToWindow(this)) {
            mOnEngineViewAttachListener.onViewAttachedToWindow(this);
        }
    }

    @Override public OnEngineViewAttachListener getOnEngineViewAttachListener() {
        return mOnEngineViewAttachListener;
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mOnEngineViewAttachListener != null){
            mOnEngineViewAttachListener.onViewAttachedToWindow(this);
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mOnEngineViewAttachListener != null){
            mOnEngineViewAttachListener.onViewDetachedFromWindow(this);
        }
    }

    @Override public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if(mOnEngineViewAttachListener != null){
            mOnEngineViewAttachListener.onViewStartTemporaryDetach(this);
        }
    }

    @Override public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if(mOnEngineViewAttachListener != null){
            mOnEngineViewAttachListener.onViewFinishTemporaryDetach(this);
        }
    }
}
