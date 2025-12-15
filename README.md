# android-net

[![](https://jitpack.io/v/che2n3jigw/android-net.svg)](https://jitpack.io/#che2n3jigw/android-net)

https://jitpack.io/#che2n3jigw/android-net

## 添加依赖
settings.gradle
```gradle
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }
}
```

libs.versions.toml
```toml
[versions]
# kotlin 序列化依赖和插件版本
kotlinxSerializationJson = "1.9.0"
# kotlin协程
kotlinxCoroutinesCore = "1.10.2"
# retrofit 依赖版本
retrofit = "3.0.0"
# retrofit 封装库(根据实际版本)
androidNet = "{latest version}"

[libraries]
converter-kotlinx-serialization = { module = "com.squareup.retrofit2:converter-kotlinx-serialization", version.ref = "retrofit" }
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinxCoroutinesCore" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinxSerializationJson" }
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
android-net = { module = "com.github.che2n3jigw:android-net", version.ref = "androidNet" }

[plugins]
kotlinx-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

build.gradle.kts(project)
```gradle
plugins {
    ...
    alias(libs.plugins.kotlinx.serialization) apply false
}
```

build.gradle.kts(module)
```gradle
plugins {
    ...
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
  implementation(libs.android.net)
  // retrofit
  implementation(libs.retrofit)
  // 实体类转换器
  implementation(libs.converter.kotlinx.serialization)
  // kotlin序列化
  implementation(libs.kotlinx.serialization.json)
}
```

## 定义接收请求返回数据的实体类
```kotlin
import kotlinx.serialization.Serializable
// 根据实际返回数据定义
@Serializable
data class Contributor(val test: String? = null)
```

## 定义接口
```kotlin
import retrofit2.http.GET
import retrofit2.http.Path
interface DemoService {
    @GET("/repos/{owner}/{repo}/contributors")
    suspend fun contributors(
        @Path("owner") owner: String?,
        @Path("repo") repo: String?
    ): List<Contributor?>?
}
```

## 创建API服务
```kotlin
private val service = RequestClient.createService<DemoService>(
    baseUrl = "https://api.github.com/",
    enableLogging = BuildConfig.DEBUG
)
```

## 请求接口
1. 直接请求
   ```kotlin
   service.contributors("square", "retrofit")
   ```
   > 注意: 直接请求需要自行捕获异常

2. 使用工具类请求
    ```kotlin
    val result = RequestUtils.safeApiCall {
      service.contributors("square", "retrofit")
    }
    when (result) {
      // 请求成功
      is RequestResult.Success -> result.data
      // 请求失败
      is RequestResult.Error -> result.message
    }
    ```

## 全局异常监听
```kotlin
RequestUtils.globalErrorHandler = { code, message ->
    // 当使用RequestUtils.safeApiCall请求接口有异常时调用
}
```

## 自定义配置
```kotlin
private val service = RequestClient.createService<DemoService>(
    baseUrl = "https://server1.com/",
    // 是否启用日志
    enableLogging = false,
    // 是否清除API服务实例,当为true时,每次创建的API服务实例都是新的,false若已创建则使用旧的实例
    clearCache = true,
    // 自定义实体类转换器
    converters = listOf(CustomConverter()),
    // 自定义拦截器
    interceptors = listOf(CustomInterceptor())
)
```
## 深入阅读

想了解这个库的设计思路和实现细节？我写了一篇详细的技术文章：

[Retrofit 封装实战：多实例缓存与安全请求的 Kotlin 实践](https://che2n3jigw.github.io/android/retrofit-packaging-practice/)
