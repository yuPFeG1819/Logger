# Logger
支持多种日志类型直接输出字符串的自用日志库
- 利用责任链模式，顺序执行日志内容类型解析器，尝试解析日志内容类型，内置多种类型，简化外部日志输出的使用
- 支持多个输出位置，可根据项目需要配置是否开启输出位置
- 支持装饰日志内容正文，添加额外信息、显示调用栈位置、显示调用所在线程，
- 支持在所有额外日志内容外再添加装饰日志框，与其他日志做出明显区分，方便查看日志


## 依赖方式

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
只要是存在`PrintHandler`都能将其转化为`String`进行输出



如，
直接输出`ArrayList`类型
```
D/logger: -print to logcat- 
    ╔══════════════════════════════════════════════════════════════════════════════════════════════════
      test log headers 
      second log header 
    ╟
      Thread : main
    ╟
      com.yupfeg.sample.MainActivity.onCreate (MainActivity.kt: line :31)
    ╟
      class java.util.ArrayList size = 30
      [0, 3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36, 39, 42, 45, 48, 51, 54, 57, 60, 63, 66, 69, 72, 75, 78, 81, 84, 87]
    ╚
```

直接输出`Intent`类型
```
I/logger: -print to logcat- 
    ╔══════════════════════════════════════════════════════════════════════════════════════════════════
      test log headers 
      second log header 
    ╟
      Thread : main
    ╟
      com.yupfeg.sample.MainActivity.onCreate (MainActivity.kt: line :38)
    ╟
      class android.content.Intent
      {
        "Action": "android.intent.action.MAIN",
        "ComponentInfo": "ComponentInfo{com.yupfeg.logger\/com.yupfeg.sample.MainActivity}",
        "Categories": "{android.intent.category.LAUNCHER}"
      }
    ╚
```
直接输出`HashMap`类型
```
D/logger: -print to logcat- 
    ╔══════════════════════════════════════════════════════════════════════════════════════════════════
      test log headers 
      second log header 
    ╟
      Thread : main
    ╟
      com.yupfeg.sample.MainActivity.onCreate (MainActivity.kt: line :35)
    ╟
      class java.util.LinkedHashMap
      {
        "11": 22,
        "22": 33,
        "33": 121
      }
    ╚
```
直接输出`Bundle`类型
```
D/logger: -print to logcat- 
    ╔══════════════════════════════════════════════════════════════════════════════════════════════════
      test log headers 
      second log header 
    ╟
      Thread : main
    ╟
      com.yupfeg.sample.MainActivity.onCreate (MainActivity.kt: line :41)
    ╟
      class android.os.Bundle
      {
        "test": {
          "mMap": {
            "test": "newTest"
          }
        }
      }
    ╚
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
    jsonConverter = GsonConverter() //默认的json解析类
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
默认内置了`LogcatPrinter`的输出类，输出到logcat ，支持输出长日志 ，超出3K长度的日志 ，会自动拆分为多个日志。
> 1.0.4版本后，需要通过`Logger.addPrinter`配置继承添加`LogcatPrint`才能输出到Logcat。
> 或者通过kotlin-dsl方式，通过`setDslLoggerConfig`函数的`logPrinters`属性添加`LogcatPrint`

- 如果需要输出到其他位置，如本地文件或者上传到服务器，则可继承`BaseLogPrinter`自定义输出目标。

同时可配置`isEnable`属性，仅在指定状态下开启。

> - 如果使用`kotlin`，则可用dsl方式，设置`setDslLoggerConfig`函数配置`logPrinters`属性添加其他输出类。
>
> - 或者直接通过`Logger.addPrinter`函数添加。

每输出一条日志内容，都会将所有的日志输出类遍历一遍，**所有已开启的的输出类都会执行输出操作**。

通过在`Printer`类的构造函数上，设置`Formatter`类，可在每条日志内容外部添加一个**日志格式框**。
默认使用内置的`SimpleFormatterImpl`，添加左侧双格缩进的日志装饰，方便进行复制日志内容

## Tanks
[SAF-Kotlin-log](https://github.com/fengzhizi715/SAF-Kotlin-log)
