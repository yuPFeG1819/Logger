package com.yupfeg.logger

import com.yupfeg.logger.converter.GsonConverter
import com.yupfeg.logger.handle.*
import com.yupfeg.logger.handle.config.LogPrintRequest
import com.yupfeg.logger.handle.config.PrintHandleConfig
import com.yupfeg.logger.printer.BaseLogPrinter
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

    /**
     * 日志输出类集合
     * * 实际输出日志时，会遍历集合内的所有类输出日志
     * */
    private val mLogPrinters = mutableSetOf<BaseLogPrinter>()

    /**日志内容处理类的集合*/
    private val mPrintHandlers = mutableListOf<BasePrintHandler>()

    /**当前第一个执行日志内容处理的类*/
    private var mPrintHandlerChain : BasePrintHandler by Delegates.notNull()

    init {
        if (config.requestPoolSize > 0){
            LogPrintRequest.maxPoolSize = config.requestPoolSize
        }
        initPrintHandlers()
        val printHandleConfig = PrintHandleConfig(
            printers = initLogPrinters(),
            logHeaders = config.logHeaders,
            isPrintClassInfo = config.isDisplayThreadInfo,
            isPrintThreadInfo = config.isDisplayClassInfo,
            jsonConverter = config.jsonConverter ?: GsonConverter(),
            isMultiPrinter = mLogPrinters.size > 1
        )
        chainPrintHandler(printHandleConfig)
    }

    // <editor-fold desc="初始化配置">

    private fun initLogPrinters() : Set<BaseLogPrinter>{
        val printers = mutableSetOf<BaseLogPrinter>()
        config.logPrinters?.also {
            printers.addAll(it)
        }
        return printers
    }

    private fun initPrintHandlers(){
        //外部自定义的处理器，优先于内置处理器
        config.printHandlers?.also {
            mPrintHandlers += it
        }
        //添加内置的日志输出类型处理器
        mPrintHandlers.apply {
            this += StringPrintHandler()
            this += ThrowablePrintHandler()
            this += BundlePrintHandler()
            this += IntentPrintHandler()
            this += MapPrintHandler()
            this += CollectionPrintHandler()
            this += ObjectPrintHandler()
        }
    }

    private fun chainPrintHandler(handleConfig : PrintHandleConfig){
        //将所有类型处理器串联成单链表结构
        for (i in 0 until mPrintHandlers.size) {
            if (i == 0) continue
            mPrintHandlers[i - 1].apply {
                setPrintConfig(handleConfig)
                setNextChain(mPrintHandlers[i])
            }
        }
        mPrintHandlerChain = mPrintHandlers[0]
    }

    // </editor-fold>

    /**
     * 根据日志处理链输出日志
     * @param level 日志等级
     * @param tag 日志tag，默认为[mGlobalLogTag]
     * @param message 日志内容
     * */
    internal fun printLogWithHandlerChain(
        level: LoggerLevel,
        tag : String?,
        message : Any
    ){
        val request = LogPrintRequest.obtain().apply {
            setNewContent(level,tag?:mGlobalLogTag,message)
        }
        mPrintHandlerChain.handlePrintContent(request)
    }

}