package com.yupfeg.logger.printer

import com.yupfeg.logger.LoggerLevel
import com.yupfeg.logger.formatter.Formatter

/**
 * 日志输出接口
 * @author yuPFeG
 * @date 2022/04/12
 */
interface ILogPrinter {
    /**输出日志的格式化类*/
    val logFormatter : Formatter

    /**是否开启日志输出*/
    val isEnable : Boolean

    /**
     * 输出日志内容
     * @param logLevel 日志等级[LoggerLevel]
     * @param tag 日志tag
     * @param msg content
     * */
    fun printLog(logLevel: LoggerLevel, tag: String, msg: String)
}