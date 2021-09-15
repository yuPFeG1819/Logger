## Logger
模仿[SAF-Kotlin-log](https://github.com/fengzhizi715/SAF-Kotlin-log)的自用日志库



### 依赖方式

[![](https://jitpack.io/v/com.gitee.yupfeg/logger.svg)](https://jitpack.io/#com.gitee.yupfeg/logger)

```groovy
//root project build.gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
//module build.gradle
dependencies {
    implementation 'com.gitee.yupfeg:logger:{$lastVersion}'
}
```

## 简单使用

通过`kotlin`的**top-level**函数包装，可在外部通过导入`com.yupfeg.logger.ext`包直接调用
> 1.0.4版本后，需要通过`Logger.addPrinter`配置继承添加`LogcatPrint`才能输出到Logcat。
> 或者通过kotlin-dsl方式，通过`setDslLoggerConfig`函数的`logPrinters`属性添加`LogcatPrint`

```kotlin
logv()

logi()

logd()

logw()

loge()
```



## 进阶配置

可通过`setDslLoggerConfig`函数通过kotlin-dsl方式配置`Logger`

``` kotlin
setDslLoggerConfig {
    isDebug = BuildConfig.DEBUG
    //是否显示当前线程信息
    isDisplayThreadInfo = false
    //是否显示当前线程信息
    isDisplayClassInfo = false
    //额外信息
    logHeaders = listOf(
        "test log headers","second log header"
    )
}
```

### 额外信息

可在每条日志顶部添加额外信息，如设备型号，设备系统版本，app版本等信息

> - 如果使用`kotlin`，则可用dsl方式，设置`setDslLoggerConfig`函数配置`logHeaders`属性添加额外信息。
>
> - 或者直接通过`Logger.addLogHeaders`函数添加。



### JSON解析

`Converter`层，隔离具体json解析实现

内置默认为`GsonConverter`解析json。

外部可实现`JsonConverter`接口创建自定义json解析策略，然后调用`Logger.jsonConverter`设置对日志内容的解析策略



### 类型处理

目前内置了
- `BundlePrintHandler`
- `CollectionPrintHandler`，处理集合对象类型
- `IntentPrintHandler`
- `MapPrintHandler`
- `StringPrintHandler`
- `ThrowablePrintHandler`
- `ObjectPrintHandler`
日志内容类型处理器，将对应类型转化为字符串日志输出，通常已足够应付日常使用
如果需要处理其他类型，通过继承`BasePrintHandler`来处理如何输出为日志字符串。

> - 如果使用`kotlin`，则可用dsl方式，设置`setDslLoggerConfig`函数配置`printHandlers`属性添加自定义日志内容处理类。
>
> - 或者直接通过`Logger.addPrintHandler`函数添加。

外部调用只需要将类型传入，通过处理类自动转化为字符串输出。



### 输出目标

~~库仅内置了输出到`Logcat`的输出类，且其默认与`Logger`类配置的`isDebug`绑定，仅debug状态下执行输出操作。~~
默认内置了`LogcatPrint`的输出类，输出到logcat
> 1.0.4版本后，需要通过`Logger.addPrinter`配置继承添加`LogcatPrint`才能输出到Logcat。
> 或者通过kotlin-dsl方式，通过`setDslLoggerConfig`函数的`logPrinters`属性添加`LogcatPrint`

如果需要输出到其他位置，如本地文件或者上传到服务器，则可继承`BaseLogPrinter`自定义输出目标。

同时可配置`isEnable`属性，仅在指定状态下开启。

> - 如果使用`kotlin`，则可用dsl方式，设置`setDslLoggerConfig`函数配置`logPrinters`属性添加其他输出类。
>
> - 或者直接通过`Logger.addPrinter`函数添加。

每输出日志内容，都会将所有的日志输出类遍历一遍，**可使用的输出类都会执行输出操作**。

