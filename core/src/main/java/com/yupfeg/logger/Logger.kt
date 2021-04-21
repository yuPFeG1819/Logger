package com.yupfeg.logger

import com.yupfeg.logger.converter.GsonConverter
import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.formatter.BorderFormatterImpl
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.StringPrintHandler
import com.yupfeg.logger.handle.ThrowablePrintHandler
import com.yupfeg.logger.handle.config.PrintHandlerConfig
import com.yupfeg.logger.printer.BaseLogPrinter
import com.yupfeg.logger.printer.LogcatPrinter
import com.yupfeg.logger.handle.*
import com.yupfeg.logger.handle.BundlePrintHandler
import org.jetbrains.annotations.TestOnly
import java.util.*

/**
 * 日志库的核心类，全局单例
 * * 日志库整体参考修改自[SAF-Kotlin-log](https://github.com/fengzhizi715/SAF-Kotlin-log)
 * @author yuPFeG
 * @date 2020/12/31
 */
object Logger {

    private const val MIN_STACK_OFFSET = 9

    /**全局默认的日志tag*/
    private var mGlobalLogTag = "Logger"

    /**是否为debug状态*/
    var isDebug : Boolean = true
        private set

    /**日志额外信息*/
    private var mLogHeaders : MutableList<String> ?= null

    /**是否在日志中打印当前线程信息*/
    private var isDisplayThreadInfo : Boolean = true

    /**是否在日志中打印当前打印日志的位置*/
    private var isDisplayClassInfo : Boolean = true

    /**日志输出类集合
     * * 实际输出日志时，会遍历集合内的所有类输出日志
     * */
    private val mLogPrinters = Collections.synchronizedSet(mutableSetOf<BaseLogPrinter>())

    /**日志内容处理类的集合*/
    private val mHandlers = mutableListOf<BasePrintHandler>()

    /**当前第一个执行日志内容处理的类*/
    private val mPrintHandlerChain : BasePrintHandler

    /**日志内容json解析器*/
    var jsonConverter : JsonConverter = GsonConverter()

    init {
        mLogPrinters.add(LogcatPrinter())

        mHandlers.run {
            add(StringPrintHandler())
            add(ThrowablePrintHandler())
            add(BundlePrintHandler())
            add(IntentPrintHandler())
            add(MapPrintHandler())
            add(CollectionPrintHandler())
            add(ObjectPrintHandler())
        }

        for (i in 0 until mHandlers.size) {
            if (i == 0) continue
            mHandlers[i - 1].setNextChain(mHandlers[i])
        }

        mPrintHandlerChain = mHandlers[0]
    }

    //<editor-fold desc="日志输出方法">

    @Suppress("unused")
    @JvmStatic
    fun v(msg: Any?) = prepareLog(LoggerLevel.VERBOSE, mGlobalLogTag,msg)

    @Suppress("unused")
    @JvmStatic
    fun v(tag : String = mGlobalLogTag,msg: Any?) = prepareLog(LoggerLevel.VERBOSE,tag,msg)

    @Suppress("unused")
    @JvmStatic
    fun i(msg: Any?) = prepareLog(LoggerLevel.INFO, mGlobalLogTag,msg)

    @Suppress("unused")
    @JvmStatic
    fun i(tag: String = mGlobalLogTag, msg : Any?) = prepareLog(LoggerLevel.INFO,tag,msg)

    @Suppress("unused")
    @JvmStatic
    fun d(msg: Any?) = prepareLog(LoggerLevel.DEBUG, mGlobalLogTag,msg)

    @Suppress("unused")
    @JvmStatic
    fun d(tag: String = mGlobalLogTag, msg: Any?) = prepareLog(LoggerLevel.DEBUG,tag, msg)

    @Suppress("unused")
    @JvmStatic
    fun w(msg: Any?) = prepareLog(LoggerLevel.WARN, mGlobalLogTag,msg)

    @Suppress("unused")
    @JvmStatic
    fun w(tag: String = mGlobalLogTag, msg: Any?) = prepareLog(LoggerLevel.WARN,tag, msg)

    @Suppress("unused")
    @JvmStatic
    fun e(msg: Any?) = prepareLog(LoggerLevel.ERROR, mGlobalLogTag,msg)

    @Suppress("unused")
    @JvmStatic
    fun e(tag: String = mGlobalLogTag, msg: Any?) = prepareLog(LoggerLevel.ERROR,tag, msg)


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
     * 是否是debug，输出到日志
     * @param isDebug
     * @return [Logger]类本身，便于链式调用
     * */
    @JvmStatic
    fun setDebug(isDebug : Boolean) : Logger {
        Logger.isDebug = isDebug
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
        isDisplayThreadInfo = isDisplay
        return this
    }

    /**
     * 设置是否显示日志输出调用位置
     * @param isDisplay 显示状态
     * @return [Logger]类本身，便于链式调用
     * */
    @JvmStatic
    fun setDisplayClassInfo(isDisplay : Boolean) : Logger {
        isDisplayClassInfo = isDisplay
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
     * 自定义 PrintHandler 来解析日志内容，并指定 Handler 在责任链中的位置
     * @param handler 拓展的[BasePrintHandler]解析处理日志内容，并输出日志
     * @param index [handler]在责任链数组中的位置
     */
    @Suppress("unused")
    @JvmStatic
    fun addPrintHandler(handler : BasePrintHandler, index : Int = mHandlers.size) : Logger {
        mHandlers.add(index, handler)
        val len = mHandlers.size
        for (i in 0 until len){
            if (i == 0) continue
            mHandlers[i - 1].setNextChain(mHandlers[i])
        }

        return this
    }

    /**
     * 自定义 PrintHandler 来解析日志内容，并指定 Handler 在责任链中的位置
     * @param list 拓展的[BasePrintHandler]解析处理日志内容的list，并将处理后的内容输出
     * @param index [list]在责任链数组中的位置
     */
    @JvmStatic
    fun addPrintHandlerList(list : List<BasePrintHandler>?, index : Int = mHandlers.size) : Logger {
        list?:return this
        mHandlers.addAll(index,list)
        val len = mHandlers.size
        for (i in 0 until len){
            if (i == 0) continue
            mHandlers[i - 1].setNextChain(mHandlers[i])
        }
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

    //</editor-fold desc="配置方法">

    /**
     * 输出日志
     * @param level 日志等级
     * @param tag 日志tag，默认为[mGlobalLogTag]
     * @param msg 日志内容
     * */
    private fun prepareLog(level: LoggerLevel, tag : String = mGlobalLogTag, msg : Any?){
        @Suppress("unused")
        msg?:return
        mPrintHandlerChain.handlePrintContent(
            msg, PrintHandlerConfig(level,tag,printers = mLogPrinters)
        )
    }

    /**
     * 获取格式化后的日志信息
     * @param formatter 日志输入格式化类，默认为[BorderFormatterImpl]
     * @return 包含["%s"]的日志输出字符串，需要外部使用[String.format]方法，将“%s“替换为实际日志内容
     * */
    @JvmStatic
    @JvmOverloads
    fun getFormatLogContent(formatter: Formatter = BorderFormatterImpl): String {
        return StringBuilder().apply {
            append(formatter.top())
            //顶部额外信息
            appendLogHeaderContent(formatter)

            if (isDisplayThreadInfo) {
                //显示当前线程名
                append(formatter.leftSplitter())
                append("Thread : ${Thread.currentThread().name}")
                append(formatter.middle())
            }
            //当前调用位置信息
            appendLogInvokeStack(formatter)
            append(formatter.leftSplitter())
            //实际日志内容
            append("%s")
            //日志框底部格式
            append(formatter.bottom())
        }.toString()
    }

    /**
     * [StringBuilder]拓展函数，添加日志内容顶部额外信息
     * @param formatter
     * */
    private fun StringBuilder.appendLogHeaderContent(formatter: Formatter){
        mLogHeaders?.takeIf { it.isNotEmpty() }
            ?.map {header->
                append(formatter.leftSplitter())
                append("$header \n")
            }?.also {
                deleteCharAt(this.lastIndex)
                append(formatter.middle())
            }
    }

    /**
     * 添加日志方法调用位置的栈信息
     * * 当前外部调用日志输出的类名、方法名、行数
     * @param formatter
     * */
    private fun StringBuilder.appendLogInvokeStack(formatter: Formatter){
        if (!isDisplayClassInfo) return

        val sElements = Thread.currentThread().stackTrace
        val stackOffset = sElements.calculateStackOffset()
        append(formatter.leftSplitter())
        append(sElements[stackOffset].className)
            .append(".")
            .append(sElements[stackOffset].methodName)
            .append(" ")
            .append("(")
            .append(sElements[stackOffset].fileName)
            .append(": line :")
            .append(sElements[stackOffset].lineNumber)
            .append(")")
            .append(formatter.middle())
    }

    /**
     * [StackTraceElement]数组的拓展函数，计算调用栈的调用层次
     * */
    private fun Array<StackTraceElement>.calculateStackOffset(): Int {
        var i = MIN_STACK_OFFSET
        while (i < this.size) {
            val name = this[i].className
            takeIf {
                !name.contains("LoggerExtKt")
                        && name != BaseLogPrinter::class.java.name
            }?.also {
                return i
            }?: run {
                i++
            }
        }
        return 0
    }

    /**仅测试用的，打印所有调用栈信息*/
    @Suppress("unused")
    @TestOnly
    private fun StringBuilder.appendLogStackTrace(formatter: Formatter){
        if (!isDisplayClassInfo) return

        val sElements = Thread.currentThread().stackTrace
        for (sElement in sElements) {
            append(formatter.leftSplitter())
            append(sElement.className)
            append(".")
            append(sElement.methodName)
            append(" ")
            append("(")
            append(sElement.fileName)
            append(": line :")
            append(sElement.lineNumber)
            append(")")
            append("\n")
        }
        append(formatter.middle())
    }


}