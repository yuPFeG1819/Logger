package com.yupfeg.logger.handle

import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.LogPrintRequest
import com.yupfeg.logger.printer.BaseLogPrinter
import org.jetbrains.annotations.TestOnly

/**
 * 处理日志输出的处理Handler链基类
 * * 采用责任链模式，高内聚，低耦合，拓展性强，可动态增加处理流程，并可动态调整处理顺序
 * * 相比策略模式，采用责任链模式，在外部添加PrintHandler类时，不需要修改Logger中对于Handler类的处理逻辑，
 * 只需要依照责任链依次向下执行
 * * 默认内部已实现了对日志内容输出格式化的逻辑，
 * 如果需要修改对输出日志内容有其他需要，可以覆写[formatLogContent]方法生成新的输出内容
 * @author yuPFeG
 * @date 2021/01/04
 */
abstract class BasePrintHandler {

    companion object{
        /**最小的调用栈偏移量，尽可能确保过滤的调用栈内无用信息*/
        private const val MIN_STACK_OFFSET = 10
    }

    /**下一个处理节点*/
    private var mNextChain : BasePrintHandler?= null

    /**
     * 输出日志内容的缓存，以当前格式化类为key，避免重复构建相同格式的日志内容
     * */
    private val mPrintContentCache : MutableMap<Formatter,String> by
        lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            mutableMapOf()
        }

    /**
     * 设置下一个处理节点
     * @param chain
     * */
    fun setNextChain(chain: BasePrintHandler){
        this.mNextChain = chain
    }

    /**
     * 处理日志输出内容
     * - 已内置日志输出缓存机制
     * @param
     */
    fun handlePrintContent(printRequest : LogPrintRequest){
        prepareHandle(printRequest)
        if (!isHandleContent(printRequest)){
            nextHandlerChain(printRequest)
        }else{
            onHandleLogContent(printRequest)
        }
    }

    /**
     * hook入口，在当前日志内容处理器执行之前调用
     * */
    protected open fun prepareHandle(printRequest: LogPrintRequest){}

    /**
     * 执行下一个日志处理类
     * @param request 日志输出请求
     * */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun nextHandlerChain(request: LogPrintRequest){
        mNextChain?.handlePrintContent(request)
    }

    /**
     * 处理当前的日志内容
     * @param request 当前日志输出请求
     * */
    protected open fun onHandleLogContent(request : LogPrintRequest){
        synchronized(request){
            //加锁防止并发情况下缓存混乱，同一个类型处理器只能同时发送一个日志
            request.printers.forEach { printer ->
                if (!printer.isEnable) return@forEach
                printLogContent(request,printer)
            }
            if (request.isMultiPrinter){
                cleanPrintContentCache()
            }
        }
    }

    /**
     * 输出日志内容
     * @param request 当前日志输出请求
     * @param printer 日志输出目标类
     * */
    protected open fun printLogContent(
        request : LogPrintRequest,
        printer : BaseLogPrinter
    ){
        val cache = tryGetPrintContentCache(printer.logFormatter, request.printers.size)
        val logContent = if (cache.isNullOrEmpty()) {
            val newContent = formatLogContent(printer.logFormatter,request)
            if (request.isMultiPrinter){
                //只在多个输出类时才开启缓存
                savePrintCache(printer.logFormatter,newContent)
            }
            newContent
        }else{
            cache
        }
        printer.printLog(request.logLevel,request.logTag,logContent)
    }

    /**
     * 校验是否能够处理这个日志内容
     * @param request 当前日志输出请求
     * @return 是否能够处理该输出内容，true-则结束任务链，输出日志，false-表示当前类无法处理，继续调用下一个节点处理类
     * */
    abstract fun isHandleContent(request : LogPrintRequest) : Boolean


    /**
     * 格式化输出日志内容
     * - 默认如果存在相同格式化类的日志内容，则该方法只会调用一次
     * - 默认调用于输出目标类的遍历内，注意避免频繁创建对象
     * @param logFormatter 日志内容的格式化类
     * @param request 当前日志输出请求
     * @return 需要输出的日志内容
     * */
    abstract fun formatLogContent(
        logFormatter: Formatter,
        request: LogPrintRequest
    ) : String

    /**
     * 缓存指定格式输出内容
     * @param formatter 日志格式化类
     * @param logContent 格式化后的日志内容
     * */
    protected fun savePrintCache(formatter: Formatter, logContent : String){
        mPrintContentCache[formatter] = logContent
    }

    /**
     * 尝试取出指定格式化类构建的缓存
     * @param formatter 日志格式化类
     * */
    protected fun tryGetPrintContentCache(formatter: Formatter,printerSize : Int) : String?{
        //只有存在多个输出器时才开启缓存
        if (printerSize <= 1) return null
        if (mPrintContentCache.isNullOrEmpty()) return null

        if (!mPrintContentCache.containsKey(formatter)) return null
        return mPrintContentCache[formatter]
    }

    /**
     * 清空所有输出日志缓存
     * */
    protected fun cleanPrintContentCache(){
        mPrintContentCache.clear()
    }

    // <editor-fold desc="格式化日志内容">

    /**
     * 获取格式化后的日志信息
     * @param formatter 日志输入格式化类
     * @return 包含`"%s"`的日志输出字符串，需要外部使用[String.format]方法，将“%s“格式化替换为实际日志内容
     * */
    protected fun getFormatLogContentWrapper(
        formatter: Formatter,
        handleConfig: LogPrintRequest
    ): String {
        return StringBuilder().apply {
            append(formatter.top())
            //顶部额外信息
            appendLogHeaderContent(formatter,handleConfig.logHeaders)
            if (handleConfig.isPrintThreadInfo){
                //调用所在线程信息
                appendThreadInfo(formatter)
            }
            if (handleConfig.isPrintClassInfo){
                //当前调用位置信息
                appendLogInvokeStack(formatter)
            }

            append(formatter.leftSplitter())
            //实际日志内容
            append("%s")
            //日志框底部格式
            append(formatter.bottom())
        }.toString()
    }

    /**
     * [StringBuilder]拓展函数，添加日志内容顶部额外信息
     * @param formatter 日志输出的格式化类
     * @param logHeaders 日志顶部额外信息
     * */
    private fun StringBuilder.appendLogHeaderContent(
        formatter: Formatter,
        logHeaders : List<String>?
    ){
        logHeaders?.takeIf { it.isNotEmpty() }
            ?.map {header->
                append(formatter.leftSplitter())
                append("$header \n")
            }?.also {
                deleteCharAt(this.lastIndex)
                append(formatter.middle())
            }
    }

    /**
     * [StringBuilder]拓展函数，添加日志方法调用的线程信息
     * @param formatter 日志输出的格式化类
     * */
    private fun StringBuilder.appendThreadInfo(
        formatter: Formatter,
    ){
        //显示当前线程名
        append(formatter.leftSplitter())
        append("Thread : ${Thread.currentThread().name}")
        append(formatter.middle())
    }

    /**
     * 添加日志方法调用位置的栈信息
     * * 当前外部调用日志输出的调用栈信息，包括 类名、方法名、行数
     * @param formatter 日志输出的格式化类
     * */
    private fun StringBuilder.appendLogInvokeStack(formatter: Formatter){
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
            val fileName = this[i].fileName
            takeIf {
                !name.contains("LoggerExtKt")
                        && name != BaseLogPrinter::class.java.name
                        && name != BasePrintHandler::class.java.name
                        && !name.contains("Logger")
                        && !fileName.contains("Logger.kt")
                        && !fileName.contains("LoggerExt.kt")
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
    // </editor-fold>
}
