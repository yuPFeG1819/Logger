package com.yupfeg.logger

import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.handle.BasePrintHandler
import com.yupfeg.logger.printer.BaseLogPrinter

/**
 * 日志库的配置类
 * @author yuPFeG
 * @date 2020/12/31
 */
class LoggerConfig {
    /**
     * 日志额外信息集合
     * */
    var logHeaders : List<String>? = null
    /**
     * 日志输出全局的tag，为空则表示使用默认全局tag
     * */
    var tag : String? = null
    /**
     * 是否显示当前线程信息
     * */
    var isDisplayThreadInfo : Boolean  = true
    /**
     * 是否显示当前调用栈位置
     * */
    var isDisplayClassInfo : Boolean   = true
    /**
     * 日志输出类型处理的集合
     * * 按集合顺序以责任链模式依次尝试处理日志类型，优先于内置处理器
     * */
    var printHandlers : List<BasePrintHandler>? = null
    /**
     * 日志输出目标的集合
     * * 按集合顺序，依次尝试输出日志
     * */
    var logPrinters : List<BaseLogPrinter> ?= null

    /**
     * 对象json格式化解析类，默认使用Gson
     * */
    var jsonConverter : JsonConverter? = null

    /**
     * 日志请求对象缓存池大小，默认为10，如果日志频率高尝试可适当提高缓存
     * */
    var requestPoolSize : Int = 10
}