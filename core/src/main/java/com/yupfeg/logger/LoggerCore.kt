package com.yupfeg.logger

import com.yupfeg.logger.converter.GsonConverter
import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.handle.*
import com.yupfeg.logger.handle.config.LogPrintRequest
import com.yupfeg.logger.printer.BaseLogPrinter

/**
 * 日志库的内部中转核心类
 * * 日志库整体参考修改自[SAF-Kotlin-log](https://github.com/fengzhizi715/SAF-Kotlin-log)
 * @author yuPFeG
 * @date 2022/03/15
 */
internal class LoggerCore(val config : LoggerConfig) {

    companion object{
        /**默认日志输出tag*/
        private const val DEF_GLOBAL_TAG = "logger"
    }

    /**全局默认的日志tag*/
    internal val mGlobalLogTag : String = config.tag ?: DEF_GLOBAL_TAG

    /**日志额外信息*/
    private val mLogHeaders : List<String>?= config.logHeaders

    /**是否在日志中输出当前线程信息*/
    private var isPrintThreadInfo : Boolean = config.isDisplayThreadInfo

    /**是否在日志中输出当前调用栈位置信息*/
    private var isPrintClassInfo : Boolean = config.isDisplayClassInfo

    /**
     * 日志输出类集合
     * * 实际输出日志时，会遍历集合内的所有类输出日志
     * */
    private val mLogPrinters = mutableSetOf<BaseLogPrinter>()

    /**日志内容处理类的集合*/
    private val mPrintHandlers = mutableListOf<BasePrintHandler>()

    /**当前第一个执行日志内容处理的类*/
    private lateinit var mPrintHandlerChain : BasePrintHandler

    /**日志内容json解析器*/
    private var jsonConverter : JsonConverter = config.jsonConverter ?: GsonConverter()

    init {
        initPrintHandler()
        if (config.requestPoolSize > 0){
            LogPrintRequest.maxPoolSize = config.requestPoolSize
        }
        config.logPrinters?.also {
            mLogPrinters.addAll(it)
        }
    }

    // <editor-fold desc="初始化配置">

    private fun initPrintHandler(){
        //外部自定义的处理器
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

        //将所有类型处理器串联成单链表结构
        for (i in 0 until mPrintHandlers.size) {
            if (i == 0) continue
            mPrintHandlers[i - 1].setNextChain(mPrintHandlers[i])
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
        val request = obtainLogPrintRequest().apply {
            setNewContent(level,tag?:mGlobalLogTag,message)
        }
        mPrintHandlerChain.handlePrintContent(request)
    }

    private fun obtainLogPrintRequest() : LogPrintRequest{
        return LogPrintRequest.obtain() ?: LogPrintRequest(
            printers = mLogPrinters,
            logHeaders = mLogHeaders,
            isPrintClassInfo = isPrintClassInfo,
            isPrintThreadInfo = isPrintThreadInfo,
            jsonConverter = jsonConverter,
            isMultiPrinter = mLogPrinters.size > 1
        )
    }

}