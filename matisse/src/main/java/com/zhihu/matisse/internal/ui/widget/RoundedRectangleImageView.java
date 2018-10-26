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
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

public class RoundedRectangleImageView extends AppCompatImageView {

    private float mRadius; // dp
    private Path mRoundedRectPath;
    private RectF mRectF;

    public RoundedRectangleImageView(Context context) {
        super(context);
        init(context);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        mRadius = 2.0f * density;
        mRoundedRectPath = new Path();
        mRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
        mRoundedRectPath.addRoundRect(mRectF, mRadius, mRadius, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(mRoundedRectPath);
        super.onDraw(canvas);
    }
}
