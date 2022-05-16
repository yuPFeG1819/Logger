package com.yupfeg.logger

import com.yupfeg.logger.converter.GsonConverter
import com.yupfeg.logger.handle.*
import com.yupfeg.logger.handle.config.PrintHandleConfig
import com.yupfeg.logger.handle.wrap.DefaultLogInvokeStackFilter
import com.yupfeg.logger.handle.wrap.ILogInvokeStackFilter
import com.yupfeg.logger.printer.ILogPrinter
import kotlin.properties.Delegates

/**
 * 日志库的内部中转核心类
 * * 日志库整体参考修改自[SAF-Kotlin-log](https://github.com/fengzhizi715/SAF-Kotlin-log)
 * @author yuPFeG
 * @date 2022/03/15
 */
internal class LoggerCore(private val config : LoggerConfig) {

    companion object{
        /**默认日志输出tag*/
        private const val DEF_GLOBAL_TAG = "logger"
    }

    /**全局默认的日志tag*/
    private val mGlobalLogTag : String = config.tag ?: DEF_GLOBAL_TAG

    /**当前第一个执行日志内容处理的类*/
    private var mPrintHandlerChain : BasePrintHandler by Delegates.notNull()

    init {
        if (config.requestPoolSize > 0){
            LogPrintRequest.maxPoolSize = config.requestPoolSize
        }
        val printHandlers = initPrintHandlers()
        val printers = initLogPrinters()
        val filters = initLogInvokeStackFilters()
        val printHandleConfig = PrintHandleConfig(
            printers = printers,
            logHeaders = config.logHeaders,
            isPrintClassInfo = config.isDisplayThreadInfo,
            isPrintThreadInfo = config.isDisplayClassInfo,
            isJsonParseFormatEnable = config.isJsonParseFormat,
            jsonConverter = config.jsonConverter ?: GsonConverter(),
            isMultiPrinter = printers.size > 1,
            invokeStackFilters = filters
        )
        BasePrintHandler.injectPrintHandleConfig(printHandleConfig)
        chainPrintHandler(printHandlers)
    }

    // <editor-fold desc="初始化配置">

    /**
     * 初始化日志输出目标集合
     * */
    private fun initLogPrinters() : Set<ILogPrinter>{
        val printers = mutableSetOf<ILogPrinter>()
        config.logPrinters?.also {
            printers.addAll(it)
        }
        return printers
    }

    /**
     * 初始化输出处理类集合
     * */
    private fun initPrintHandlers() : List<BasePrintHandler>{
        val printHandlers = mutableListOf<BasePrintHandler>()
        //外部自定义的处理器，优先于内置处理器
        config.printHandlers?.also {
            printHandlers += it
        }
        //添加内置的日志输出类型处理器
        printHandlers.apply {
            this += StringPrintHandler()
            this += ThrowablePrintHandler()
            this += BundlePrintHandler()
            this += IntentPrintHandler()
            this += UriPrintHandler()
            this += MapPrintHandler()
            this += CollectionPrintHandler()
            this += ObjectPrintHandler()
        }
        return printHandlers
    }

    /**
     * 初始化日志调用栈过滤器集合
     * */
    private fun initLogInvokeStackFilters() : List<ILogInvokeStackFilter>{
        val filters = mutableListOf<ILogInvokeStackFilter>()
        //先添加外部自定义的过滤器
        config.invokeStackFilters?.also {
            filters += it
        }
        filters+= DefaultLogInvokeStackFilter()
        return filters
    }

    /**
     * 将所有类型处理器串联成单链表结构
     * @param printerHandlers 日志内容类型处理集合
     * */
    private fun chainPrintHandler(printerHandlers : List<BasePrintHandler>){
        for (i in printerHandlers.indices) {
            if (i == 0) continue
            printerHandlers[i - 1].apply {
                setNextChain(printerHandlers[i])
            }
        }
        mPrintHandlerChain = printerHandlers[0]
    }

    // </editor-fold>

    /**
     * 根据日志处理链输出日志
     * - 日志输出的实际入口
     * @param level 日志等级
     * @param tag 日志tag，默认为[mGlobalLogTag]
     * @param message 日志内容
     * */
    internal fun printLogWithHandlerChain(
        level: LoggerLevel,
        tag : String?,
        message : Any
    ){
        //过滤指定等级的日志，不进行处理
        if (level.value < config.globalLogFilterLevel.value) return
        val request = LogPrintRequest.obtain().apply {
            val logTag = if (tag.isNullOrEmpty()) mGlobalLogTag else tag
            setNewContent(level,logTag,message)
        }
        mPrintHandlerChain.handlePrintContent(request)
    }

}