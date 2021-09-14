package com.yupfeg.logger

import com.yupfeg.logger.handle.BasePrintHandler
import com.yupfeg.logger.printer.BaseLogPrinter

/**
 * 日志库的配置类
 * @author yuPFeG
 * @date 2020/12/31
 */
class LoggerConfig {
    /**日志额外信息*/
    var logHeaders : List<String>? = null
    /**日志输出的tag*/
    var tag : String = "logger"
    /**是否显示当前线程信息*/
    var isDisplayThreadInfo : Boolean  = true
    /**是否显示当前调用栈位置*/
    var isDisplayClassInfo : Boolean   = true
    /**
     * 日志输出类型处理的集合
     * * 按集合顺序以责任链模式依次尝试处理
     * */
    var printHandlers : List<BasePrintHandler>? = null
    /**
     * 日志输出目标的集合
     * */
    var logPrinters : List<BaseLogPrinter> ?= null
}