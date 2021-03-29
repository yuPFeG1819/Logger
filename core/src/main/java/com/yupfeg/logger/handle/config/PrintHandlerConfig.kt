package com.yupfeg.logger.handle.config

import com.yupfeg.logger.LoggerLevel
import com.yupfeg.logger.printer.BaseLogPrinter

/**
 * 日志handler的配置
 * @author yuPFeG
 * @date 2021/01/22
 */
data class PrintHandlerConfig(
    val logLevel: LoggerLevel = LoggerLevel.INFO,
    val tag:String,
    val printers : Set<BaseLogPrinter>
)