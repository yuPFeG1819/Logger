package com.yupfeg.logger.printer

import com.yupfeg.logger.LoggerLevel
import com.yupfeg.logger.formatter.Formatter

/**
 * 执行日志输出操作基类
 * @author yuPFeG
 * @date 2020/12/31
 */
abstract class BaseLogPrinter (
    /**输出日志的格式化类*/
    val logFormatter : Formatter
) {

    /**是否开启日志输出*/
    open val isEnable
        get() = true

    /**
     * 输出日志内容
     * @param logLevel 日志等级[LoggerLevel]
     * @param tag 日志tag
     * @param msg content
     * */
    open fun printLog(logLevel: LoggerLevel, tag: String, msg: String){
        if (!isEnable) return
        this.performPrintLog(logLevel, tag, msg)
    }

    /**
     * 实际执行输出日志
     * @param logLevel 日志等级[LoggerLevel]
     * @param tag 日志tag
     * @param msg content
     */
    protected abstract fun performPrintLog(logLevel: LoggerLevel, tag: String, msg: String)

}