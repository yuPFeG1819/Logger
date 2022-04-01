package com.yupfeg.logger.handle.config

import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.printer.BaseLogPrinter

/**
 * 日志的输出配置类，不可变对象
 * - 仅在内部全局初始化时赋值
 * @author yuPFeG
 * @date 2022/03/31
 */
data class PrintHandleConfig internal constructor(
    /**当前日志可能的输出目标*/
    val printers : Set<BaseLogPrinter>,
    /**当前日志内容上，包含的顶部信息*/
    val logHeaders : List<String>?,
    /**是否在日志中打印当前线程信息*/
    val isPrintThreadInfo : Boolean = true,
    /**是否在日志中打印当前打印日志的位置*/
    val isPrintClassInfo : Boolean = true,
    /**json解析器*/
    val jsonConverter: JsonConverter,
    /**是否存在多个输出器*/
    val isMultiPrinter : Boolean = false
)
