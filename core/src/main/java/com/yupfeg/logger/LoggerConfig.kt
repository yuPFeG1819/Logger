package com.yupfeg.logger

import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.handle.BasePrintHandler
import com.yupfeg.logger.handle.wrap.ILogInvokeStackFilter
import com.yupfeg.logger.printer.ILogPrinter

/**
 * 日志库的配置类
 * - 提供给外部对日志输出进行全局配置
 * @author yuPFeG
 * @date 2020/12/31
 */
class LoggerConfig {
    /**
     * 日志额外信息集合
     * - 每一个集合元素，单独占据一行
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
     * 是否显示当前日志函数的调用栈位置
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
    var logPrinters : List<ILogPrinter> ?= null

    /**
     * 是否开启json格式化日志内容
     * - 设置为true后，会对所有原始日志内容进行json解析格式化，相对美观方便查看日志，但会导致单条日志内容篇幅过多
     * - 默认为false，只通过`formatter`对日志内容添加额外的日志框美化，
     * 但需要注意日志的输出内容类型能够正常调用`toString`，
     * 或者在对应类型的`PrintHandler`内重写`formatLogContentOnlyWrap`方法进行特殊处理
     * */
    var isJsonParseFormat : Boolean = false

    /**
     * 日志内容对象进行json解析格式化的解析类
     * - 由外部控制使用何种json库进行json解析，如果为null，则默认内部会使用GSON进行解析
     * - 需要配合[isJsonParseFormat]设置为true使用
     * */
    var jsonConverter : JsonConverter? = null

    /**
     * 日志请求对象缓存池大小，默认为30，
     * - 如果日志频率高，可适当提高缓存池大小，或者适当降低缓存数量，减少内存占用
     * */
    var requestPoolSize : Int = 30

    /**
     * 日志输出调用栈的显示过滤器集合
     * * 按集合顺序依次尝试过滤多余的栈信息，确保定位到日志函数的调用位置
     * * 需要配合[isDisplayClassInfo]属性为true才能生效
     * */
    var invokeStackFilters : List<ILogInvokeStackFilter>? = null

    /**
     * 全局的日志过滤等级
     * */
    var globalLogFilterLevel : LoggerLevel = LoggerLevel.VERBOSE
}