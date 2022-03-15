package com.yupfeg.logger.handle.config

import com.yupfeg.logger.LoggerLevel
import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.printer.BaseLogPrinter

/**
 * 日志内容输出请求
 * - 提供给处理链上所有内容处理类的同一个不可变对象
 * @author yuPFeG
 * @date 2021/01/22
 */
class LogPrintRequest internal constructor(
    /**当前日志可能的输出目标*/
    val printers : Set<BaseLogPrinter>,
    /**当前日志内容上，包含的顶部信息*/
    val logHeaders : List<String>?,
    /**是否在日志中打印当前线程信息*/
    val isPrintThreadInfo : Boolean = true,
    /**是否在日志中打印当前打印日志的位置*/
    val isPrintClassInfo : Boolean = true,
    val jsonConverter: JsonConverter,
    /**是否存在多个输出器*/
    val isMultiPrinter : Boolean = false
){
    /**
     * 日志原始内容
     * */
    val logContent : Any
        get() = requireNotNull(mOriginContent)

    private var mOriginContent : Any? = null

    /**日志输出的等级*/
    var logLevel : LoggerLevel = LoggerLevel.VERBOSE
        internal set

    /**日志输出的标签*/
    val logTag : String
        get() = requireNotNull(mOriginLogTag)

    private var mOriginLogTag : String? = null

    internal fun reset(){
        mOriginContent = null
        mOriginLogTag = null
    }

    internal fun setNewContent(level : LoggerLevel, tag : String, message : Any){
        logLevel = level
        mOriginContent = message
        mOriginLogTag = tag
    }
}