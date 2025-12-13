/*
 * Copyright (c) 2025 che2n3jigw.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package com.che2n3jigw.android.net

import android.app.Activity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetActivity : Activity() {
    companion object {
        private const val TAG = "NetActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net)

        // 协程作用域即可
        CoroutineScope(Dispatchers.IO).launch {

            val contributors = DemoRepository().getContributors()
            Log.e(TAG, "onCreate: $contributors")
        }
    }
}