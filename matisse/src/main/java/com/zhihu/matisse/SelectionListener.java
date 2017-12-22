package com.zhihu.matisse;

import android.net.Uri;

import java.util.List;

/**
 * Callback for selection result
 */
public interface SelectionListener {
    /**
     * Callback when select succeeded.
     * The results include two kinds of list.
     *
     * @param uris result uri list
     * @param paths result path string list
     */
    void onSelectSucceeded(List<Uri> uris, List<String> paths);

    /**
     * Callback for select canceled
     */
    void onSelectCanceled();
}
