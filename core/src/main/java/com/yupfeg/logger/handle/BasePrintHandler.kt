package com.yupfeg.logger.handle

import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.LogPrintRequest
import com.yupfeg.logger.handle.config.PrintHandleConfig
import com.yupfeg.logger.handle.wrap.LogContentWrapHelper
import com.yupfeg.logger.printer.ILogPrinter
import kotlin.properties.Delegates

/**
 * 处理日志输出的处理Handler链基类
 * * 采用责任链模式，高内聚，低耦合，拓展性强，可动态增加处理流程，并可动态调整处理顺序，
 * 相比策略模式，采用责任链模式，在外部添加PrintHandler类时，不需要修改Logger中对于Handler类的处理逻辑，
 * 只需要依照责任链依次向下执行
 * * 默认内部已实现了对日志内容输出格式化的逻辑，
 * 如果需要修改对输出日志内容有其他需要，可以覆写[formatLogContent]方法生成新的输出内容
 * @author yuPFeG
 * @date 2021/01/04
 */
abstract class BasePrintHandler {

    companion object{
        /**
         * 日志库的处理配置，所有日志处理类共享同一个配置对象实例
         * - 共享的不可变配置对象
         * */
        var printHandleConfig : PrintHandleConfig by Delegates.notNull()
            private set

        /**
         * 日志内容包装帮助类
         * */
        var logContentWrapHelper : LogContentWrapHelper by Delegates.notNull()
            private set

        /**
         * 注入日志全局的处理配置
         * @param config 共享的不可变配置对象
         * */
        @JvmStatic
        internal fun injectPrintHandleConfig(config: PrintHandleConfig){
            printHandleConfig = config
            logContentWrapHelper = LogContentWrapHelper(printHandleConfig)
        }
    }

    /**下一个处理节点*/
    private var mNextChain : BasePrintHandler?= null

    /**
     * 输出日志内容的缓存，以当前格式化类为key，避免重复构建相同格式的日志内容
     * - 默认仅在有多个输出方式时使用
     * */
    private val mPrintContentCache : MutableMap<Formatter,String> by
        lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            mutableMapOf()
        }

    /**
     * 日志库全局的json解析器
     * - 如需单独使用其他json解析器，可以在[BasePrintHandler]实现类的内部单独实现[JsonConverter]，通常不推荐单独设置
     * */
    protected val globalJsonConverter : JsonConverter
        get() = printHandleConfig.jsonConverter

    /**
     * 是否对日志内容开启json解析格式化
     * */
    protected val isJsonParseFormatEnable : Boolean
        get() = printHandleConfig.isJsonParseFormatEnable

    /**
     * 设置下一个处理节点
     * @param chain
     * */
    internal fun setNextChain(chain: BasePrintHandler){
        this.mNextChain = chain
    }

    /**
     * 处理日志输出内容
     * - 已内置日志输出缓存机制
     * @param request 日志输出请求
     */
    internal fun handlePrintContent(request : LogPrintRequest){
        prepareHandle(request)
        //没有配置输出类，不会处理任何日志输出
        if (printHandleConfig.printers.isNullOrEmpty()) return
        if (!isHandleContent(request)){
            nextHandlerChain(request)
        }else{
            try {
                onHandleLogContent(request)
            }finally {
                LogPrintRequest.release(request)
            }
        }
    }

    /**
     * hook入口，在当前日志内容处理器执行之前调用
     * - 在`isHandleContent`之前被调用，允许子类继承实现额外操作
     * */
    protected open fun prepareHandle(printRequest: LogPrintRequest) = Unit

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
        synchronized(this){
            //加锁防止并发情况下从缓存获取到其他日志内容
            //同一个类型的处理器只能同时处理一个日志内容
            printHandleConfig.printers.forEach { printer ->
                if (!printer.isEnable) return@forEach
                printLogContent(request,printer)
            }
            if (printHandleConfig.isMultiPrinter){
                //存在多个输出类，需要移除缓存
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
        printer : ILogPrinter
    ){
        val cache = tryGetPrintContentCache(printer.logFormatter, printHandleConfig.printers.size)
        val logContent = if (cache.isNullOrEmpty()) {
            val newContent = if (isJsonParseFormatEnable){
                formatLogContent(printer.logFormatter,request)
            }else{
                formatLogContentOnlyWrap(printer.logFormatter,request)
            }
            if (printHandleConfig.isMultiPrinter){
                //只有存在多个输出类时才开启缓存
                savePrintCache(printer.logFormatter,newContent)
            }
            newContent
        }else{
            cache
        }
        printer.printLog(request.logLevel,request.logTag,logContent)
    }

    /**
     * 只对日志内容进行包装格式化，需要确保原始日志内容`toString`方法正常调用
     * @param logFormatter 日志内容的包装格式化类型
     * @param request 日志输出请求
     * @return 格式化包装后的日志内容字符串
     * */
    protected open fun formatLogContentOnlyWrap(
        logFormatter: Formatter,
        request: LogPrintRequest
    ) : String{
        val logContentFormat = getLogFormatContentWrap(logFormatter)
        val logContent = request.logContent.toString()
        return String.format(logContentFormat,logContent)
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
     * - 如果设置了`isJsonParseFormat`为false，默认不会调用该函数，会调用`formatLogContentOnlyWrap`方法
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
     * 清空当前处理类的所有输出日志缓存
     * - 必须要在当前日志内容输出完毕后才进行移除操作
     * */
    protected fun cleanPrintContentCache(){
        mPrintContentCache.clear()
    }

    /**
     * 获取格式化的日志内容包装字符串
     * @param formatter 日志输入格式化类
     * @return 包含`"%s"`的日志输出字符串，需要外部使用[String.format]方法，将“%s“格式化替换为实际日志内容
     * */
    protected open fun getLogFormatContentWrap(formatter: Formatter): String
        = logContentWrapHelper.createFormatWrapString(formatter)

}
