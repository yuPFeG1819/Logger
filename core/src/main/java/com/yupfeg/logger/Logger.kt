package com.yupfeg.logger

import android.util.Log

/**
 * 日志库的对外公开类
 * 壳对象，仅用于设置专属的局部日志输出Tag。
 * @author yuPFeG
 * @date 2020/12/31
 */
class Logger private constructor(private val globalTag : String?) {

    companion object{
        private var logConfig : LoggerConfig?= null
        @Volatile
        private var loggerCore : LoggerCore? = null

        /**
         * 全局的日志类，简化日志输出操作
         * */
        internal val GLOBAL = Logger(null)

        /**
         * 是否已进行初始化
         * */
        private var isPrepared : Boolean = false

        /**
         * 以kotlin-dsl的方式，初始化日志库配置
         * @param init dsl配置方法
         * @return Lo
         * */
        fun prepare(init : LoggerConfig.()->Unit) : Logger{
            return LoggerConfig().let {config->
                config.init()
                prepare(config)
            }
        }
        /**
         * 准备日志库配置
         * - 必须在执行[prepare]后，并配置日志输出目标才能才能输出日志
         * @param config 配置方法类
         * */
        @JvmStatic
        fun prepare(config: LoggerConfig) : Logger{
            require(!isPrepared){ "cant replay prepare Logger，only config once" }
            logConfig = config
            return GLOBAL
        }

        /**
         * 创建日志输出壳对象
         * @param clazz 以类名`simpleName`作为局部日志tag名称
         * */
        fun create(clazz : Class<*>) : Logger = Logger(globalTag = clazz.simpleName)

        /**
         * 创建日志输出壳对象
         * @param
         * */
        fun create(tag : String) : Logger = Logger(globalTag = tag)

    }

    /**
     * 全局控制当前壳对象是否开启日志输出
     * */
    private var isEnable = true

    /**
     * 关闭壳对象的日志输出
     * */
    fun silence() : Logger{
        this.isEnable = false
        return this
    }


    //<editor-fold desc="日志输出方法">

    @Suppress("unused")
    fun v(tag : String?= null,msg: Any?) = performPrintLog(LoggerLevel.VERBOSE,tag,msg)

    @Suppress("unused")
    fun i(tag: String?= null, msg : Any?) = performPrintLog(LoggerLevel.INFO,tag,msg)

    @Suppress("unused")
    fun d(tag: String? = null, msg: Any?) = performPrintLog(LoggerLevel.DEBUG,tag, msg)

    @Suppress("unused")
    fun w(tag: String? = null, msg: Any?) = performPrintLog(LoggerLevel.WARN,tag, msg)


    @Suppress("unused")
    fun e(tag: String? = null, msg: Any?) = performPrintLog(LoggerLevel.ERROR,tag, msg)

    // </editor-fold>

    /**
     * 执行输出日志操作
     * - 日志输出的实际入口
     * @param level 日志等级
     * @param tag 日志tag，如果为null，则使用壳对象的局部tag，
     * 如果局部tag也为null，则使用配置的全局tag。
     * @param message 日志内容
     * */
    private fun performPrintLog(
        level: LoggerLevel,
        tag : String?,
        message : Any?
    ){
        if (!isPrepared) {
            //避免日志未配置输出类
            Log.w(tag?:globalTag, "you should prepare logger config before print log" +
                    " \n ${message?.toString() ?: ""}")
            return
        }
        if (!isEnable) return
        message?:return
        getNewCoreInstance().printLogWithHandlerChain(level,tag?:globalTag, message)
    }

    /**
     * 获取日志输出核心类实例
     */
    private fun getNewCoreInstance() : LoggerCore{
        return loggerCore ?: synchronized(Logger::class.java){
            loggerCore ?: LoggerCore(logConfig?:LoggerConfig()).also { loggerCore = it }
        }
    }

}