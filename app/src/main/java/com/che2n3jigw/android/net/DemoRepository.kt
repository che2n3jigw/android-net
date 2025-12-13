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

import com.che2n3jigw.android.libs.net.RequestClient
import com.che2n3jigw.android.libs.net.bean.RequestResult
import com.che2n3jigw.android.libs.net.utils.RequestUtils

/**
 * 远程库
 */
class DemoRepository {
    private val service = RequestClient.createService<DemoService>(
        baseUrl = Constants.BASE_URL,
        enableLogging = BuildConfig.DEBUG
    )

    suspend fun getContributors(): List<Contributor?> {
        val result = RequestUtils.safeApiCall {
            service.contributors("square", "retrofit")
        }
        if (result is RequestResult.Success) {
            return result.data
        }
        return emptyList()
    }
}
