package com.zhihu.matisse.manager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;


/**
 * CREATED: 2018/5/15 18:10
 * <p>
 * Author: junhua.lin<br>
 * Email: junhua.lin@jinfuzi.com<br>
 * Description: <br>
 * 用于管理返回结果和权限请求
 */
public class FragmentLifecycleManager extends Fragment {
    private final static String FRAGMENT_TAG = FragmentLifecycleManager.class.getName();

    private LifecycleCallback mLifecycle;

    public static FragmentLifecycleManager get(Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        Fragment resultFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (resultFragment == null) {
            resultFragment = new FragmentLifecycleManager();
            fragmentManager.beginTransaction()
                    .add(resultFragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }

        return (FragmentLifecycleManager) resultFragment;
    }


    public void setLifecycle(LifecycleCallback lifecycle) {
        this.mLifecycle = lifecycle;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mLifecycle != null) {
            mLifecycle.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mLifecycle != null) {
            mLifecycle.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLifecycle != null) {
            mLifecycle = null;
        }
    }
}