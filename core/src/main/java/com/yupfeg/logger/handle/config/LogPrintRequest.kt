package com.yupfeg.logger.handle.config

import com.yupfeg.logger.LoggerLevel
import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.printer.BaseLogPrinter

/**
 * 日志内容处理类的配置信息
 * - 提供给处理链上所有内容处理类的同一个不可变对象
 * @author yuPFeG
 * @date 2021/01/22
 */
data class LogPrintRequest(
    val logContent : Any,
    /**日志输出的等级*/
    val logLevel : LoggerLevel = LoggerLevel.INFO,
    /**日志输出的标签*/
    val logTag : String,
    /**当前日志可能的输出目标*/
    val printers : Set<BaseLogPrinter>,
    /**当前日志内容上，包含的顶部信息*/
    val logHeaders : List<String>?,
    /**是否在日志中打印当前线程信息*/
    val isPrintThreadInfo : Boolean = true,
    /**是否在日志中打印当前打印日志的位置*/
    val isPrintClassInfo : Boolean = true,
    val jsonConverter: JsonConverter
){
    /**
     * 是否存在多个输出器
     * */
    val isMultiPrinter = printers.size > 1
}