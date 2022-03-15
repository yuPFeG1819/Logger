package com.yupfeg.logger

/**
 * 日志库的对外公开类，全局单例
 * @author yuPFeG
 * @date 2020/12/31
 */
object Logger {

    private var mLogConfig : LoggerConfig = LoggerConfig()
    @Volatile
    private var mLoggerCore : LoggerCore? = null

    //<editor-fold desc="配置方法">

    /**
     * 准备日志库配置
     * @param config 配置方法类
     * */
    @JvmStatic
    fun prepare(config: LoggerConfig) : Logger{
        mLoggerCore?.also {
            throw IllegalArgumentException("you must prepare Logger before print log")
        }
        mLogConfig = config
        return this
    }

    // </editor-fold>

    //<editor-fold desc="日志输出方法">

    @Suppress("unused")
    @JvmStatic
    @JvmOverloads
    fun v(tag : String?= null,msg: Any?) = performPrintLog(LoggerLevel.VERBOSE,tag,msg)

    @Suppress("unused")
    @JvmStatic
    @JvmOverloads
    fun i(tag: String?= null, msg : Any?) = performPrintLog(LoggerLevel.INFO,tag,msg)

    @Suppress("unused")
    @JvmStatic
    @JvmOverloads
    fun d(tag: String? = null, msg: Any?) = performPrintLog(LoggerLevel.DEBUG,tag, msg)

    @Suppress("unused")
    @JvmStatic
    @JvmOverloads
    fun w(tag: String? = null, msg: Any?) = performPrintLog(LoggerLevel.WARN,tag, msg)


    @Suppress("unused")
    @JvmStatic
    @JvmOverloads
    fun e(tag: String? = null, msg: Any?) = performPrintLog(LoggerLevel.ERROR,tag, msg)

    /**
     * 执行输出日志操作
     * @param level 日志等级
     * @param tag 日志tag
     * @param message 日志内容
     * */
    private fun performPrintLog(
        level: LoggerLevel,
        tag : String?,
        message : Any?
    ){
        message?:return
        getNewCoreInstance().printLogWithHandlerChain(level,tag, message)
    }

    private fun getNewCoreInstance() : LoggerCore{
        return mLoggerCore ?: synchronized(Logger::class.java){
            mLoggerCore ?: LoggerCore(mLogConfig).also { mLoggerCore = it }
        }
    }

    //</editor-fold desc="日志输出方法">

}