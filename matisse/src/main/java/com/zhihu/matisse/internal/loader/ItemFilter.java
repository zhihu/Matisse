package com.zhihu.matisse.internal.loader;

import android.support.annotation.NonNull;

import com.zhihu.matisse.internal.entity.Item;

public class ItemFilter {
    public boolean blockItem(@NonNull Item sourceItem)
    {
        return false;
    }
}
