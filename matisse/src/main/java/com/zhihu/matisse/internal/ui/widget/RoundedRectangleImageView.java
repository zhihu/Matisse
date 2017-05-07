/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.internal.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;

public class RoundedRectangleImageView extends AppCompatImageView {

    private static final float sRadius = 3.f; // Radius in dp

    private float mResolvedRadius; // Radius in px
    private Paint mMaskPaint;
    private Path mMaskPath;
    private Bitmap mPreRendered;

    public RoundedRectangleImageView(Context context) {
        this(context, null);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        resolveRadiusDimensions();

        mMaskPaint = new Paint();
        mMaskPaint.setColor(Color.GREEN);
        mMaskPaint.setAntiAlias(true);
        mMaskPaint.setFilterBitmap(true);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resolveRadiusDimensions();
        postInvalidate();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        RectF rect = new RectF(0, 0, r - l, b - t);
        mMaskPath = new Path();
        mMaskPath.addRoundRect(rect, mResolvedRadius, mResolvedRadius, Path.Direction.CW);
        mPreRendered = null;

        return super.setFrame(l, t, r, b);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mPreRendered = null;
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mPreRendered = null;
        super.setImageBitmap(bm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPreRendered == null) {
            Bitmap tempBitmap =
                    Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
            Canvas bitmapCanvas = new Canvas(tempBitmap);
            mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            bitmapCanvas.drawPath(mMaskPath, mMaskPaint);

            mPreRendered = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
            bitmapCanvas = new Canvas(mPreRendered);
            super.onDraw(bitmapCanvas);
            mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            bitmapCanvas.drawBitmap(tempBitmap, 0, 0, mMaskPaint);
        }

        canvas.drawBitmap(mPreRendered, 0, 0, null);
    }

    private void resolveRadiusDimensions() {
        mResolvedRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sRadius,
                getResources().getDisplayMetrics());
    }
}
