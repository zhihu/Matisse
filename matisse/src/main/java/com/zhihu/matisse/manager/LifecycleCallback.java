package com.zhihu.matisse.manager;

import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * CREATED: 2018/5/15 18:12
 * <p>
 * Author: junhua.lin<br>
 * Email: junhua.lin@jinfuzi.com<br>
 * Description: <br>
 */
public interface LifecycleCallback {
    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
