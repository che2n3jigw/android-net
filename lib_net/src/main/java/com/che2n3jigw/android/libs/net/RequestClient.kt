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
    private val retrofitMap = ConcurrentHashMap<String, Retrofit>()

    /**
     * 全局共享的 OkHttpClient 基础实例
     * 共享连接池和线程池，提升性能并减少资源消耗。
     */
    private val baseClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * OkHttp日志拦截器
     */
    private val logging by lazy {
        HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    private val jsonFactory by lazy {
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
        connectTimeout: Long,
        readTimeout: Long,
        writeTimeout: Long,
        enableLogging: Boolean,
        converters: List<Converter.Factory>,
        interceptors: List<Interceptor>
    ): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(baseUrl)
            // 基于基础客户端创建新配置，共享连接池
            client(
                provideOkHttpClient(
                    connectTimeout,
                    readTimeout,
                    writeTimeout,
                    enableLogging,
                    interceptors
                )
            )
            // 添加自定义转换器
            converters.forEach { addConverterFactory(it) }
            // 默认添加 JSON 转换器
            addConverterFactory(jsonFactory)
        }.build()
    }

    /**
     * 提供 OkHttp 客户端
     * 使用 newBuilder() 确保共享线程池和连接池
     */
    private fun provideOkHttpClient(
        connectTimeout: Long,
        readTimeout: Long,
        writeTimeout: Long,
        enableLogging: Boolean,
        interceptors: List<Interceptor>
    ): OkHttpClient {
        return baseClient.newBuilder().apply {
            connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
            // 添加日志拦截器
            if (enableLogging) {
                addInterceptor(logging)
            }
            interceptors.forEach { addInterceptor(it) }
        }.build()
    }

    /**
     * 获取 Retrofit 实例
     * @param refresh 是否刷新实例（如果为 true，则丢弃旧实例并重新创建）
     */
    fun getRetrofit(
        baseUrl: String,
        connectTimeout: Long,
        readTimeout: Long,
        writeTimeout: Long,
        enableLogging: Boolean,
        refresh: Boolean,
        converters: List<Converter.Factory>,
        interceptors: List<Interceptor>
    ): Retrofit {
        if (refresh) {
            close(baseUrl)
        }
        return retrofitMap.getOrPut(baseUrl) {
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
     * @param refresh           是否强制刷新客户端实例
     * @param converters        转换器
     * @param interceptors      拦截器
     */
    inline fun <reified T> createService(
        baseUrl: String,
        connectTimeout: Long = 30_000,
        readTimeout: Long = 30_000,
        writeTimeout: Long = 30_000,
        enableLogging: Boolean = true,
        refresh: Boolean = false,
        converters: List<Converter.Factory> = emptyList(),
        interceptors: List<Interceptor> = emptyList()
    ): T {
        return getRetrofit(
            baseUrl,
            connectTimeout,
            readTimeout,
            writeTimeout,
            enableLogging,
            refresh,
            converters,
            interceptors
        ).create(T::class.java)
    }

    /**
     * 释放指定 [baseUrl] 的 Retrofit 引用
     * 由于底层共享 OkHttpClient，通常不需要手动调用 shutdown()
     */
    fun close(baseUrl: String) {
        retrofitMap.remove(baseUrl)
    }
}