package com.yupfeg.logger.ext

import com.yupfeg.logger.Logger
import com.yupfeg.logger.LoggerConfig

/**
 * 日志输出相关的Top-Level方法
 * @author yuPFeG
 * @date 2019/9/9
 */

/**默认日志输出tag*/
private const val DEFAULT_GLOBAL_TAG = "logger"

/**
 * 使用kotlin DSL方式，配置Logger参数
 * @param init dsl方式配置
 * */
fun setDslLoggerConfig(init : LoggerConfig.()->Unit) : Logger {
    return LoggerConfig().let {
        it.init()
        performConfigLogger(it)
    }
}

/**
 * 执行配置Logger操作
 * @param config
 * */
private fun performConfigLogger(config: LoggerConfig) : Logger {
    return Logger.addLogHeaders(if(config.logHeaders.isNullOrEmpty()) null else config.logHeaders)
        .setGlobalLogTag(config.tag)
        .setDisplayClassInfo(config.isDisplayClassInfo)
        .setDisplayThreadInfo(config.isDisplayThreadInfo)
        .addPrintHandlerList(list = config.printHandlers)
        .addLogPrinters(list = config.logPrinters)
}
/**
 * 输出verbose等级的日志
 * * 默认的日志tag[Logger.mGlobalLogTag]
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logv(msg : Any) = Logger.v(msg)

/**
 * 输出debug等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logv(tag : String,msg: Any) = Logger.v(tag, msg)

/**
 * 输出debug等级的日志
 * * 默认的日志tag[Logger.mGlobalLogTag]
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logd(msg : Any) = Logger.d(msg)

/**
 * 输出debug等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logd(tag : String, msg : Any?) = Logger.d(tag,msg)

/**
 * 输出info等级的日志
 * * 默认的日志tag[DEFAULT_GLOBAL_TAG]
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logi(msg : Any?) = Logger.i(msg)

/**
 * 输出info等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logi(tag : String, msg : Any) = Logger.i(tag,msg)

/**
 * 输出waring等级的日志
 * * 默认的日志tag[DEFAULT_GLOBAL_TAG]
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logw(msg: Any?) = logw(DEFAULT_GLOBAL_TAG,msg)

/**
 * 输出waring等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun logw(tag: String,msg: Any?) {
    Logger.w(tag,msg)
}

/**
 * 输出error等级的日志
 * * 默认的日志tag[DEFAULT_GLOBAL_TAG]
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun loge(msg: Any?) = loge(DEFAULT_GLOBAL_TAG,msg)

/**
 * 输出error等级的日志
 * @param tag 日志tag
 * @param msg 日志内容
 */
@Suppress("SpellCheckingInspection", "unused")
fun loge(tag : String,msg : Any?) = Logger.e(tag,msg)
