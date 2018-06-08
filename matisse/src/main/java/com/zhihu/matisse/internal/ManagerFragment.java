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
package com.zhihu.matisse.internal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.SelectionListener;

/**
 * For onActivityResult callback with the request
 */
public class ManagerFragment extends Fragment {

    public final static int DEFAULT_REQUEST_CODE = 99;
    private SelectionListener mSelectionListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (DEFAULT_REQUEST_CODE == requestCode) {
            // This is our request
            if (mSelectionListener == null) {
                return;
            }

            if (resultCode == Activity.RESULT_OK) {
                mSelectionListener.onSelectSucceeded(Matisse.obtainResult(data), Matisse.obtainPathResult(data));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mSelectionListener.onSelectCanceled();
            }
        }
    }

    /**
     * @return SelectionListener for callback
     */
    public SelectionListener getSelectionListener() {
        return mSelectionListener;
    }

    /**
     * Set SelectionListener for callback
     * @param selectionListener listener
     */
    public void setSelectionListener(SelectionListener selectionListener) {
        mSelectionListener = selectionListener;
    }
}
