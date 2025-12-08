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

package com.che2n3jigw.android.libs.net.intercepotr

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Header 拦截器
 * 统一Header处理
 */
class HeaderInterceptor(
    private val extraHeaders: Map<String, String> = emptyMap() // 额外自定义 Header
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()

        // 自定义额外 Header
        for ((key, value) in extraHeaders) {
            requestBuilder.header(key, value)
        }

        // 保持原请求体和方法
        val request = requestBuilder.method(original.method, original.body).build()

        return chain.proceed(request)
    }
}