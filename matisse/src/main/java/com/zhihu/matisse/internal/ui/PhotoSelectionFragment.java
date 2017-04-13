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
package com.zhihu.matisse.internal.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.model.AlbumPhotoCollection;
import com.zhihu.matisse.internal.model.SelectedItemCollection;
import com.zhihu.matisse.internal.ui.adapter.AlbumPhotosAdapter;
import com.zhihu.matisse.internal.ui.widget.PhotoGridInset;
import com.zhihu.matisse.internal.utils.UIUtils;

public class PhotoSelectionFragment extends Fragment implements
        AlbumPhotoCollection.AlbumPhotoCallbacks, AlbumPhotosAdapter.CheckStateListener,
        AlbumPhotosAdapter.OnPhotoClickListener {

    public static final String EXTRA_ALBUM = "extra_album";

    private final AlbumPhotoCollection mPhotoCollection = new AlbumPhotoCollection();
    private RecyclerView mRecyclerView;
    private AlbumPhotosAdapter mAdapter;
    private SelectionProvider mSelectionProvider;
    private AlbumPhotosAdapter.CheckStateListener mCheckStateListener;
    private AlbumPhotosAdapter.OnPhotoClickListener mOnPhotoClickListener;

    public static PhotoSelectionFragment newInstance(Album album) {
        PhotoSelectionFragment fragment = new PhotoSelectionFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectionProvider) {
            mSelectionProvider = (SelectionProvider) context;
        } else {
            throw new IllegalStateException("Context must implement SelectionProvider.");
        }
        if (context instanceof AlbumPhotosAdapter.CheckStateListener) {
            mCheckStateListener = (AlbumPhotosAdapter.CheckStateListener) context;
        }
        if (context instanceof AlbumPhotosAdapter.OnPhotoClickListener) {
            mOnPhotoClickListener = (AlbumPhotosAdapter.OnPhotoClickListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_selection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Album album = getArguments().getParcelable(EXTRA_ALBUM);

        mAdapter = new AlbumPhotosAdapter(getContext(),
                mSelectionProvider.provideSelectedItemCollection(), mRecyclerView);
        mAdapter.registerCheckStateListener(this);
        mAdapter.registerOnPhotoClickListener(this);
        mRecyclerView.setHasFixedSize(true);

        int spanCount;
        SelectionSpec selectionSpec = SelectionSpec.getInstance();
        if (selectionSpec.spanCount > 0) {
            spanCount = selectionSpec.spanCount;
        } else {
            spanCount = UIUtils.spanCount(getContext(), selectionSpec.gridExpectedSize);
        }
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        int spacing = getResources().getDimensionPixelSize(R.dimen.photo_grid_spacing);
        mRecyclerView.addItemDecoration(new PhotoGridInset(spanCount, spacing, false));
        mRecyclerView.setAdapter(mAdapter);
        mPhotoCollection.onCreate(getActivity(), this);
        mPhotoCollection.load(album, selectionSpec.capture);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPhotoCollection.onDestroy();
    }

    public void refreshPhotoGrid() {
        mAdapter.notifyDataSetChanged();
    }

    public void refreshSelection() {
        mAdapter.refreshSelection();
    }

    @Override
    public void onLoad(Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onReset() {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onUpdate() {
        // notify outer Activity that check state changed
        if (mCheckStateListener != null) {
            mCheckStateListener.onUpdate();
        }
    }

    @Override
    public void onPhotoClick(Album album, Item item, int adapterPosition) {
        if (mOnPhotoClickListener != null) {
            mOnPhotoClickListener.onPhotoClick((Album) getArguments().getParcelable(EXTRA_ALBUM),
                    item, adapterPosition);
        }
    }

    public interface SelectionProvider {
        SelectedItemCollection provideSelectedItemCollection();
    }
}
