package com.yupfeg.logger

import com.yupfeg.logger.converter.GsonConverter
import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.handle.StringPrintHandler
import com.yupfeg.logger.handle.ThrowablePrintHandler
import com.yupfeg.logger.handle.config.LogPrintRequest
import com.yupfeg.logger.printer.BaseLogPrinter
import com.yupfeg.logger.handle.*
import com.yupfeg.logger.handle.BundlePrintHandler
import java.util.*

/**
 * 日志库的核心类，全局单例
 * * 日志库整体参考修改自[SAF-Kotlin-log](https://github.com/fengzhizi715/SAF-Kotlin-log)
 * @author yuPFeG
 * @date 2020/12/31
 */
object Logger {

    /**全局默认的日志tag*/
    private var mGlobalLogTag = "Logger"

    /**日志额外信息*/
    private var mLogHeaders : MutableList<String> ?= null

    /**是否在日志中输出当前线程信息*/
    private var isPrintThreadInfo : Boolean = true

    /**是否在日志中输出当前调用栈位置信息*/
    private var isPrintClassInfo : Boolean = true

    /**
     * 日志输出类集合
     * * 实际输出日志时，会遍历集合内的所有类输出日志
     * */
    private val mLogPrinters = Collections.synchronizedSet(mutableSetOf<BaseLogPrinter>())

    /**日志内容处理类的集合*/
    private val mHandlers = mutableListOf<BasePrintHandler>()

    /**当前第一个执行日志内容处理的类*/
    private var mPrintHandlerChain : BasePrintHandler

    /**日志内容json解析器*/
    private var mJsonConverter : JsonConverter = GsonConverter()

    init {
        //添加内置的日志输出类型处理器
        mHandlers.apply {
            add(StringPrintHandler())
            add(ThrowablePrintHandler())
            add(BundlePrintHandler())
            add(IntentPrintHandler())
            add(MapPrintHandler())
            add(CollectionPrintHandler())
            add(ObjectPrintHandler())
        }
        //将所有类型处理器串联成单链表结构
        for (i in 0 until mHandlers.size) {
            if (i == 0) continue
            mHandlers[i - 1].setNextChain(mHandlers[i])
        }

        mPrintHandlerChain = mHandlers[0]
    }

    //<editor-fold desc="日志输出方法">

    @Suppress("unused")
    @JvmStatic
    fun v(msg: Any?) = printLogWithHandlerChain(LoggerLevel.VERBOSE, mGlobalLogTag,msg)

    @Suppress("unused")
    @JvmStatic
    fun v(tag : String = mGlobalLogTag,msg: Any?) = printLogWithHandlerChain(LoggerLevel.VERBOSE,tag,msg)

    @Suppress("unused")
    @JvmStatic
    fun i(msg: Any?) = printLogWithHandlerChain(LoggerLevel.INFO, mGlobalLogTag,msg)

    @Suppress("unused")
    @JvmStatic
    fun i(tag: String = mGlobalLogTag, msg : Any?) = printLogWithHandlerChain(LoggerLevel.INFO,tag,msg)

    @Suppress("unused")
    @JvmStatic
    fun d(msg: Any?) = printLogWithHandlerChain(LoggerLevel.DEBUG, mGlobalLogTag,msg)

    @Suppress("unused")
    @JvmStatic
    fun d(tag: String = mGlobalLogTag, msg: Any?) = printLogWithHandlerChain(LoggerLevel.DEBUG,tag, msg)

    @Suppress("unused")
    @JvmStatic
    fun w(msg: Any?) = printLogWithHandlerChain(LoggerLevel.WARN, mGlobalLogTag,msg)

    @Suppress("unused")
    @JvmStatic
    fun w(tag: String = mGlobalLogTag, msg: Any?) = printLogWithHandlerChain(LoggerLevel.WARN,tag, msg)

    @Suppress("unused")
    @JvmStatic
    fun e(msg: Any?) = printLogWithHandlerChain(LoggerLevel.ERROR, mGlobalLogTag,msg)

    @Suppress("unused")
    @JvmStatic
    fun e(tag: String = mGlobalLogTag, msg: Any?) = printLogWithHandlerChain(LoggerLevel.ERROR,tag, msg)

    /**
     * 根据日志处理链输出日志
     * @param level 日志等级
     * @param tag 日志tag，默认为[mGlobalLogTag]
     * @param msg 日志内容
     * */
    private fun printLogWithHandlerChain(
        level: LoggerLevel,
        tag : String = mGlobalLogTag,
        msg : Any?
    ){
        msg?:return
        val request = LogPrintRequest(
            logContent = msg,
            logLevel = level,
            logTag = tag,
            printers = mLogPrinters,
            logHeaders = mLogHeaders,
            isPrintClassInfo, isPrintClassInfo,
            jsonConverter = mJsonConverter
        )
        mPrintHandlerChain.handlePrintContent(request)
    }

    //</editor-fold desc="日志输出方法">

    //<editor-fold desc="配置方法">

    /**
     * 设置全局日志tag
     * @param tag
     * @return [Logger]类本身，便于链式调用
     * */
    @JvmStatic
    fun setGlobalLogTag(tag : String) : Logger {
        mGlobalLogTag = tag
        return this
    }

    /**
     * 添加日志顶部额外信息
     * @param header App版本号、设备编号等信息，方便调试
     * @return [Logger]类本身，便于链式调用
     */
    @Suppress("unused")
    @JvmStatic
    fun addLogHeader(header : String) : Logger {
        mLogHeaders ?:run { mLogHeaders = mutableListOf()}
        mLogHeaders?.add(header)
        return this
    }

    /**
     * 添加日志顶部额外信息
     * @param headers App版本号、设备编号等信息，方便调试
     * @return [Logger]类本身，便于链式调用
     */
    @JvmStatic
    fun addLogHeaders(headers : List<String>?) : Logger {
        mLogHeaders ?:run { mLogHeaders = mutableListOf()}
        headers?.also { mLogHeaders?.addAll(it) }
        return this
    }

    /**
     * 清空日志顶部额外信息
     * @return [Logger]类本身，便于链式调用
     */
    @Suppress("unused")
    @JvmStatic
    fun clearLogHeader() : Logger {
        mLogHeaders?.clear()
        return this
    }

    /**
     * 设置是否显示日志输出的线程信息
     * @param isDisplay 显示状态
     * @return [Logger]类本身，便于链式调用
     * */
    @JvmStatic
    fun setDisplayThreadInfo(isDisplay : Boolean) : Logger {
        isPrintThreadInfo = isDisplay
        return this
    }

    /**
     * 设置是否显示日志输出调用位置
     * @param isDisplay 显示状态
     * @return [Logger]类本身，便于链式调用
     * */
    @JvmStatic
    fun setDisplayClassInfo(isDisplay : Boolean) : Logger {
        isPrintClassInfo = isDisplay
        return this
    }

    /**
     * 添加自定义输出类
     * * 默认仅有输出到Logcat
     * @param printer
     * @return
     */
    @Suppress("unused")
    @JvmStatic
    fun addPrinter(printer: BaseLogPrinter) : Logger {
        mLogPrinters.add(printer)
        return this
    }

    /**
     * 添加自定义 PrintHandler 来解析日志内容到责任链的头节点，优先执行
     * @param handler 拓展的[BasePrintHandler]解析处理日志内容，并输出日志
     */
    @Suppress("unused")
    @JvmStatic
    fun addPrintHandler(handler : BasePrintHandler) : Logger {
        mHandlers.add(0, handler)
        //重置责任链的结构
        val len = mHandlers.size
        for (i in 0 until len){
            if (i == 0) continue
            mHandlers[i - 1].setNextChain(mHandlers[i])
        }
        mPrintHandlerChain = mHandlers[0]
        return this
    }

    /**
     * 添加自定义 PrintHandler 来解析日志内容类型的集合，只能添加到原有处理器的前面，最先尝试处理日志内容
     * @param list 拓展的[BasePrintHandler]解析处理日志内容的list，并将处理后的内容输出
     */
    @JvmStatic
    fun addPrintHandlerList(list : List<BasePrintHandler>?) : Logger {
        list?:return this
        mHandlers.addAll(0,list)
        //重置责任链的结构
        val len = mHandlers.size
        for (i in 0 until len){
            if (i == 0) continue
            mHandlers[i - 1].setNextChain(mHandlers[i])
        }
        mPrintHandlerChain = mHandlers[0]
        return this
    }

    /**
     * 添加自定义日志输出类集合
     * @param list 拓展[BaseLogPrinter]的集合，添加额外的日志输出类
     * */
    @JvmStatic
    fun addLogPrinters(list : List<BaseLogPrinter>?) : Logger{
        list?:return this
        mLogPrinters.addAll(list)
        return this
    }

    /**
     * 设置json解析器
     * @param jsonConverter json解析器，用于解析集合类型内部的泛型对象，Bundle对象等
     * */
    fun setJsonConverter(jsonConverter: JsonConverter?) : Logger{
        jsonConverter?:return this
        mJsonConverter = jsonConverter
        return this
    }

    //</editor-fold desc="配置方法">

}