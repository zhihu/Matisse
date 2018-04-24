package com.zhihu.matisse.sample;

import android.content.Context;

import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;

import java.util.HashSet;
import java.util.Set;

public class OriginalSizeFilter extends Filter {

    private int mMaxSize;

    public OriginalSizeFilter(int mMaxSize) {
        this.mMaxSize = mMaxSize;
    }

    @Override
    protected Set<MimeType> constraintTypes() {
        return new HashSet<MimeType>() {{
            addAll(MimeType.ofImage());
        }};
    }

    @Override
    public IncapableCause filter(Context context, Item item) {
        if (!needFiltering(context, item))
            return null;

        if (item.size > mMaxSize) {
            return new IncapableCause(IncapableCause.DIALOG, context.getString(R.string.error_original,
                    String.valueOf(PhotoMetadataUtils.getSizeInMB(mMaxSize))));
        }
        return null;
    }
}
