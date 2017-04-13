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
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.internal.entity;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.zhihu.matisse.internal.ui.widget.UncapableDialog;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@SuppressWarnings("unused")
public class UncapableCause {
    public static final int TOAST = 0x00;
    public static final int DIALOG = 0x01;
    public static final int NONE = 0x02;

    @Retention(SOURCE)
    @IntDef({TOAST, DIALOG, NONE})
    public @interface Form {
    }

    private int mForm = TOAST;
    private String mTitle;
    private String mMessage;

    public UncapableCause(String message) {
        mMessage = message;
    }

    public UncapableCause(String title, String message) {
        mTitle = title;
        mMessage = message;
    }

    public UncapableCause(@Form int form, String message) {
        mForm = form;
        mMessage = message;
    }

    public UncapableCause(@Form int form, String title, String message) {
        mForm = form;
        mTitle = title;
        mMessage = message;
    }

    public static void handleCause(Context context, UncapableCause cause) {
        if (cause == null)
            return;

        switch (cause.mForm) {
            case NONE:
                // do nothing.
                break;
            case TOAST:
                Toast.makeText(context, cause.mMessage, Toast.LENGTH_SHORT).show();
                break;
            case DIALOG:
                UncapableDialog uncapableDialog = UncapableDialog.newInstance(cause.mTitle, cause.mMessage);
                uncapableDialog.show(((FragmentActivity) context).getSupportFragmentManager(),
                        UncapableDialog.class.getName());
                break;
        }
    }
}
