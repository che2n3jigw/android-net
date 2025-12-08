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

package com.che2n3jigw.android.libs.net

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * 网络请求客户端
 */
object RequestClient {

    /**
     * 存储多个Retrofit实例
     */
    private val mRetrofitMap = ConcurrentHashMap<String, Retrofit>()

    /**
     * OkHttp日志拦截器
     */
    private val mLogging by lazy {
        HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    private val mJsonFactory by lazy {
        val json = Json {
            // 忽略未定义的key
            ignoreUnknownKeys = true
        }
        json.asConverterFactory("application/json; charset=UTF8".toMediaType())
    }

    /**
     * 创建 Retrofit 实例
     */
    private fun createRetrofit(
        baseUrl: String,
        connectTimeout: Long = 30_000,
        readTimeout: Long = 30_000,
        writeTimeout: Long = 30_000,
        enableLogging: Boolean = true,
        converters: List<Converter.Factory> = emptyList(),
        interceptors: List<Interceptor> = emptyList()
    ): Retrofit {
        val builder = Retrofit.Builder().apply {
            // 域名
            baseUrl(baseUrl)
            // OkHttp客户端
            val okHttpClient = provideOkHttpClient(
                connectTimeout, readTimeout, writeTimeout, enableLogging, interceptors
            )
            client(okHttpClient)
            // JSON转换器
            if (converters.isNotEmpty()) {
                for (factory in converters) {
                    addConverterFactory(factory)
                }
            }
            addConverterFactory(mJsonFactory)
        }
        return builder.build()
    }

    /**
     * 提供OkHttp客户端
     */
    private fun provideOkHttpClient(
        connectTimeout: Long,
        readTimeout: Long,
        writeTimeout: Long,
        enableLogging: Boolean = true,
        interceptors: List<Interceptor> = emptyList()
    ): OkHttpClient {
        val builder = OkHttpClient.Builder().apply {
            connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
            // 添加日志拦截器
            if (enableLogging) {
                addInterceptor(mLogging)
            }
            if (interceptors.isNotEmpty()) {
                for (interceptor in interceptors) {
                    addInterceptor(interceptor)
                }
            }
        }
        return builder.build()
    }

    /**
     * 获取 Retrofit 实例
     */
    fun getRetrofit(
        baseUrl: String,
        connectTimeout: Long = 30_000,
        readTimeout: Long = 30_000,
        writeTimeout: Long = 30_000,
        enableLogging: Boolean = true,
        clearCache: Boolean = false,
        converters: List<Converter.Factory> = emptyList(),
        interceptors: List<Interceptor> = emptyList()
    ): Retrofit {
        if (clearCache) {
            mRetrofitMap.remove(baseUrl)
        }
        return mRetrofitMap.getOrPut(baseUrl) {
            createRetrofit(
                baseUrl,
                connectTimeout,
                readTimeout,
                writeTimeout,
                enableLogging,
                converters,
                interceptors
            )
        }
    }

    /**
     * 创建 API 服务
     * @param baseUrl           域名
     * @param connectTimeout    连接超时时间
     * @param readTimeout       读取超时时间
     * @param writeTimeout      写入超时时间
     * @param enableLogging     是否启用日志
     * @param converters        转换器
     * @param interceptors      拦截器
     */
    inline fun <reified T> createService(
        baseUrl: String,
        connectTimeout: Long = 30_000,
        readTimeout: Long = 30_000,
        writeTimeout: Long = 30_000,
        enableLogging: Boolean = true,
        clearCache: Boolean = false,
        converters: List<Converter.Factory> = emptyList(),
        interceptors: List<Interceptor> = emptyList()
    ): T {
        return getRetrofit(
            baseUrl,
            connectTimeout,
            readTimeout,
            writeTimeout,
            enableLogging,
            clearCache,
            converters,
            interceptors
        ).create(T::class.java)
    }
}