package com.zhihu.matisse.internal.ui.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.zhihu.matisse.R;

public class CheckRadioView extends AppCompatImageView {

    private Drawable mDrawable;

    private int mSelectedColor;
    private int mUnSelectUdColor;

    public CheckRadioView(Context context) {
        super(context);
        init();
    }



    public CheckRadioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mSelectedColor = ResourcesCompat.getColor(
                getResources(), R.color.zhihu_item_checkCircle_backgroundColor,
                getContext().getTheme());
        mUnSelectUdColor = ResourcesCompat.getColor(
                getResources(), R.color.zhihu_check_original_radio_disable,
                getContext().getTheme());
        setChecked(false);
    }

    public void setChecked(boolean enable) {
        if (enable) {
            setImageResource(R.drawable.ic_preview_radio_on);
            mDrawable = getDrawable();
            mDrawable.setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_IN);
        } else {
            setImageResource(R.drawable.ic_preview_radio_off);
            mDrawable = getDrawable();
            mDrawable.setColorFilter(mUnSelectUdColor, PorterDuff.Mode.SRC_IN);
        }
    }


    public void setColor(int color) {
        if (mDrawable == null) {
            mDrawable = getDrawable();
        }
        mDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
