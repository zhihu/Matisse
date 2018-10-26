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

package com.zhihu.matisse.listener;

import android.net.Uri;
import androidx.annotation.NonNull;

import java.util.List;

public interface OnSelectedListener {
    /**
     * @param uriList the selected item {@link Uri} list.
     * @param pathList the selected item file path list.
     */
    void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList);
}
