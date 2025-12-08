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

package com.che2n3jigw.android.libs.net.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 基础VO
 */
@Serializable
data class BaseVO<T>(
    @SerialName("error_code") val code: Int? = null,
    @SerialName("error_message") val message: String? = null,
    val data: T? = null
)