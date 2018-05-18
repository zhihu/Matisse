package com.zhihu.matisse.listener;

import android.net.Uri;

import java.util.List;

/**
 * CREATED: 2018/5/16 14:49
 * <p>
 * Author: junhua.lin<br>
 * Email: junhua.lin@jinfuzi.com<br>
 * Description: <br>
 */
public interface OnFinishedListener {

    /**
     * @param uris          User selected media' {@link Uri} list.
     * @param paths         User selected media path list.
     * @param originalState Whether use original photo
     */
    void onFinished(List<Uri> uris, List<String> paths, boolean originalState);
}
