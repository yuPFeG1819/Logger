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

/**
 * 输出verbose等级的日志
 * * 使用全局的日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logv(msg : Any) = Logger.GLOBAL.v(msg = msg)

/**
 * 输出debug等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logv(tag : String,msg: Any) = Logger.GLOBAL.v(tag, msg)

/**
 * 输出debug等级的日志
 * * 使用全局的日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logd(msg : Any) = Logger.GLOBAL.d(msg = msg)

/**
 * 输出debug等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logd(tag : String, msg : Any?) = Logger.GLOBAL.d(tag,msg)

/**
 * 输出info等级的日志
 * * 使用全局的日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logi(msg : Any?) = Logger.GLOBAL.i(msg = msg)

/**
 * 输出info等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logi(tag : String, msg : Any) = Logger.GLOBAL.i(tag,msg)

/**
 * 输出waring等级的日志
 * * 使用全局的日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logw(msg: Any?) = Logger.GLOBAL.w(msg = msg)

/**
 * 输出waring等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logw(tag: String,msg: Any?) = Logger.GLOBAL.w(tag,msg)

/**
 * 输出error等级的日志
 * * 使用全局的日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun loge(msg: Any?) = Logger.GLOBAL.e(msg = msg)

/**
 * 输出error等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun loge(tag : String,msg : Any?) = Logger.GLOBAL.e(tag,msg)
