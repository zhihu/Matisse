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
package com.zhihu.matisse.internal.ui.adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.ui.PreviewItemFragment;

import java.util.ArrayList;
import java.util.List;

public class PreviewPagerAdapter extends FragmentStateAdapter {
    private ArrayList<Item> mItems = new ArrayList<>();
    private Context mContext;
    private FragmentManager mFragmentManager;

    public PreviewPagerAdapter(@NonNull Context context, @NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.mContext = context;
        this.mFragmentManager = fragmentActivity.getSupportFragmentManager();
    }

    public PreviewPagerAdapter(@NonNull Context context, @NonNull Fragment fragment) {
        super(fragment);
        this.mContext = context;
        this.mFragmentManager = fragment.getChildFragmentManager();
    }

    public PreviewPagerAdapter(@NonNull Context context,
                               @NonNull FragmentManager fragmentManager,
                               @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment bizContainer = mFragmentManager.getFragmentFactory().instantiate(mContext.getClassLoader(),
                PreviewItemFragment.class.getName());
        Bundle bundle = new Bundle();
        bundle.putParcelable(PreviewItemFragment.ARGS_ITEM, mItems.get(position));
        bizContainer.setArguments(bundle);
        return bizContainer;
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public void addAll(List<Item> items) {
        mItems.addAll(items);
    }

    public Item getMediaItem(int position) {
        return mItems.get(position);
    }
}
