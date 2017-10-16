package com.zhihu.matisse.engine.impl;

import com.facebook.common.internal.Supplier;
import com.facebook.imagepipeline.cache.MemoryCacheParams;

/**
 * Created by bhijuelos on 10/11/17.
 */

public class FrescoSupplier implements Supplier<MemoryCacheParams> {

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 8;

    private final MemoryCacheParams mMemoryCacheParams =
            new MemoryCacheParams(MAX_MEMORY_CACHE_SIZE, Integer.MAX_VALUE, MAX_MEMORY_CACHE_SIZE, Integer.MAX_VALUE,
                    Integer.MAX_VALUE);

    @Override
    public MemoryCacheParams get() {
        return mMemoryCacheParams;
    }
}
