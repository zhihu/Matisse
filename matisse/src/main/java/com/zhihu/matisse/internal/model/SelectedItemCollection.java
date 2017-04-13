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
package com.zhihu.matisse.internal.model;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.entity.UncapableCause;
import com.zhihu.matisse.internal.ui.widget.CheckView;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class SelectedItemCollection {

    private static final String STATE_SELECTION = "state_selection";
    private final Context mContext;
    private Set<Item> mItems;
    private SelectionSpec mSpec;

    public SelectedItemCollection(Context context) {
        mContext = context;
    }

    public void onCreate(Bundle savedInstanceState, SelectionSpec spec) {
        if (savedInstanceState == null) {
            mItems = new LinkedHashSet<>();
        } else {
            List<Item> saved = savedInstanceState.getParcelableArrayList(STATE_SELECTION);
            mItems = new LinkedHashSet<>(saved);
        }
        mSpec = spec;
    }

    public void setDefaultSelection(List<Item> uris) {
        mItems.addAll(uris);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
    }

    public boolean add(Item item) {
        return mItems.add(item);
    }

    public boolean remove(Item item) {
        return mItems.remove(item);
    }

    public void overwrite(ArrayList<Item> items) {
        mItems.clear();
        mItems.addAll(items);
    }

    public List<Item> asList() {
        return new ArrayList<>(mItems);
    }

    public List<Uri> asListOfUri() {
        List<Uri> uris = new ArrayList<>();
        for (Item item : mItems) {
            uris.add(item.getContentUri());
        }
        return uris;
    }

    public boolean isEmpty() {
        return mItems == null || mItems.isEmpty();
    }

    public boolean isSelected(Item item) {
        return mItems.contains(item);
    }

    public UncapableCause isAcceptable(Item item) {
        if (maxSelectableReached()) {
            return new UncapableCause(mContext.getString(R.string.error_over_count, mSpec.maxSelectable));
        }

        return PhotoMetadataUtils.isAcceptable(mContext, item);
    }

    public boolean maxSelectableReached() {
        return mItems.size() == mSpec.maxSelectable;
    }

    public int count() {
        return mItems.size();
    }

    public int maxSelectable() {
        return mSpec.maxSelectable;
    }

    public int checkedNumOf(Item item) {
        int index = new ArrayList<>(mItems).indexOf(item);
        return index == -1 ? CheckView.UNCHECKED : index + 1;
    }
}
