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
