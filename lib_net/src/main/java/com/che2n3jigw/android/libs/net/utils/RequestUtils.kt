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

package com.che2n3jigw.android.libs.net.utils

import com.che2n3jigw.android.libs.net.bean.RequestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException

/**
 * 请求帮助类
 */
object RequestUtils {

    /**
     * 全局异常回调接口
     */
    var globalErrorHandler: (suspend (code: Int, message: String) -> Unit)? = null

    /**
     * 安全的API调用
     * @return RequestResult<T> 成功返回 Success，失败返回 Error
     */
    suspend fun <T> safeApiCall(apiCall: suspend () -> T?): RequestResult<T> {
        return try {
            val result = withContext(Dispatchers.IO) {
                apiCall()
            }
            if (result != null) {
                RequestResult.Success(result)
            } else {
                RequestResult.Error(-1, "No data")
            }
        } catch (e: HttpException) {
            val code = e.code()
            val message = e.message() ?: "Http error"
            globalErrorHandler?.invoke(code, message)
            RequestResult.Error(code, message)
        } catch (e: IOException) {
            val code = -1
            val message = "Network error: ${e.message}"
            globalErrorHandler?.invoke(code, message)
            RequestResult.Error(code, message)
        } catch (e: Exception) {
            val code = -1
            val message = e.message ?: "Unknown error"
            globalErrorHandler?.invoke(code, message)
            RequestResult.Error(code, message)
        }
    }
}