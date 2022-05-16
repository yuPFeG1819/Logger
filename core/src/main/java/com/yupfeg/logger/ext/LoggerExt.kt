package com.yupfeg.logger.ext

import com.yupfeg.logger.Logger
import com.yupfeg.logger.LoggerConfig

/**
 * 使用kotlin DSL方式，配置Logger参数
 * @param init dsl方式配置
 * */
@Deprecated(
    message = "已统合到Logger类的静态方法",
    replaceWith = ReplaceWith(
        expression = "Logger.prepare(init)",
        imports = ["com.yupfeg.logger.Logger"]
    )
)
fun setDslLoggerConfig(init : LoggerConfig.()->Unit) : Logger{
    return Logger.prepare(init)
}

// <editor-fold desc="快捷输出Verbose等级的日志">

/**
 * 输出verbose等级的日志
 * * 使用全局的日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
@Deprecated(
    message = "当前命名会与AS自带的模板冲突，无法快速导入",
    replaceWith = ReplaceWith(
        expression = "loggv(msg)",
        imports = ["com.yupfeg.logger.ext.loggv"]
    )
)
fun logv(msg : Any) = Logger.GLOBAL.v(msg = msg)

/**
 * 输出debug等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
@Deprecated(
    message = "当前命名会与AS自带的模板冲突，无法快速导入",
    replaceWith = ReplaceWith(
        expression = "loggv(tag,msg)",
        imports = ["com.yupfeg.logger.ext.loggv"]
    )
)
fun logv(tag : String,msg: Any) = Logger.GLOBAL.v(tag, msg)

/**
 * 输出verbose等级的日志，默认使用全局配置日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun loggv(msg : Any) = Logger.GLOBAL.v(msg)

/**
 * 输出debug等级的日志，默认使用全局配置日志tag
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun loggv(tag : String,msg: Any) = Logger.GLOBAL.v(tag, msg)

// </editor-fold>

// <editor-fold desc="快捷输出Debug等级的日志">

/**
 * 输出debug等级的日志
 * * 使用全局的日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
@Deprecated(
    message = "当前命名会与AS自带的模板冲突，无法快速导入",
    replaceWith = ReplaceWith(
        expression = "loggd(msg)",
        imports = ["com.yupfeg.logger.ext.loggd"]
    )
)
fun logd(msg : Any) = Logger.GLOBAL.d(msg = msg)

/**
 * 输出debug等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
@Deprecated(
    message = "当前命名会与AS自带的模板冲突，无法快速导入",
    replaceWith = ReplaceWith(
        expression = "loggd(tag,msg)",
        imports = ["com.yupfeg.logger.ext.loggd"]
    )
)
fun logd(tag : String, msg : Any?) = Logger.GLOBAL.d(tag,msg)

/**
 * 输出debug等级的日志，默认使用全局配置日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun loggd(msg : Any) = Logger.GLOBAL.d(msg)

/**
 * 输出debug等级的日志，默认使用全局配置日志tag
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun loggd(tag : String,msg: Any) = Logger.GLOBAL.d(tag, msg)

// </editor-fold>

// <editor-fold desc="快捷输出info等级的日志">

/**
 * 输出info等级的日志
 * * 使用全局的日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
@Deprecated(
    message = "当前命名会与AS自带的模板冲突，无法快速导入",
    replaceWith = ReplaceWith(
        expression = "loggi(msg)",
        imports = ["com.yupfeg.logger.ext.loggi"]
    )
)
fun logi(msg : Any?) = Logger.GLOBAL.i(msg = msg)

/**
 * 输出info等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
@Deprecated(
    message = "当前命名会与AS自带的模板冲突，无法快速导入",
    replaceWith = ReplaceWith(
        expression = "loggi(tag,msg)",
        imports = ["com.yupfeg.logger.ext.loggi"]
    )
)
fun logi(tag : String, msg : Any) = Logger.GLOBAL.i(tag,msg)

/**
 * 输出info等级的日志，默认使用全局配置日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun loggi(msg : Any) = Logger.GLOBAL.i(msg)

/**
 * 输出info等级的日志，默认使用全局配置日志tag
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun loggi(tag : String,msg: Any) = Logger.GLOBAL.i(tag, msg)

// </editor-fold>

// <editor-fold desc="快捷输出warn等级的日志">

/**
 * 输出waring等级的日志
 * * 使用全局的日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
@Deprecated(
    message = "当前命名会与AS自带的模板冲突，无法快速导入",
    replaceWith = ReplaceWith(
        expression = "loggw(msg)",
        imports = ["com.yupfeg.logger.ext.loggw"]
    )
)
fun logw(msg: Any?) = Logger.GLOBAL.w(msg = msg)

/**
 * 输出waring等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
@Deprecated(
    message = "当前命名会与AS自带的模板冲突，无法快速导入",
    replaceWith = ReplaceWith(
        expression = "loggw(tag,msg)",
        imports = ["com.yupfeg.logger.ext.loggw"]
    )
)
fun logw(tag: String,msg: Any?) = Logger.GLOBAL.w(tag,msg)

/**
 * 输出warn等级的日志，默认使用全局配置日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun loggw(msg : Any) = Logger.GLOBAL.w(msg)

/**
 * 输出warn等级的日志，默认使用全局配置日志tag
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun loggw(tag : String,msg: Any) = Logger.GLOBAL.w(tag, msg)

// </editor-fold>

// <editor-fold desc="快捷输出error等级的日志">

/**
 * 输出error等级的日志
 * * 使用全局的日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
@Deprecated(
    message = "当前命名会与AS自带的模板冲突，无法快速导入",
    replaceWith = ReplaceWith(
        expression = "logge(tag,msg)",
        imports = ["com.yupfeg.logger.ext.logge"]
    )
)
fun loge(msg: Any?) = Logger.GLOBAL.e(msg = msg)

/**
 * 输出error等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
@Deprecated(
    message = "当前命名会与AS自带的模板冲突，无法快速导入",
    replaceWith = ReplaceWith(
        expression = "logge(tag,msg)",
        imports = ["com.yupfeg.logger.ext.logge"]
    )
)
fun loge(tag : String,msg : Any?) = Logger.GLOBAL.e(tag,msg)

/**
 * 输出error等级的日志，默认使用全局配置日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logge(msg : Any) = Logger.GLOBAL.e(msg)

/**
 * 输出error等级的日志，默认使用全局配置日志tag
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logge(tag : String,msg: Any) = Logger.GLOBAL.e(tag, msg)

// </editor-fold>