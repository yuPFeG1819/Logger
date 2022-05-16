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

        @Volatile
        private var global : Logger? = null

        /**
         * 全局的日志类，简化全局日志输出操作
         * */
        internal val GLOBAL : Logger
            get() = global ?: synchronized(this){
                global?: Logger(null).also { global = it }
            }

        /**
         * 是否已进行初始化
         * */
        private var isPrepared : Boolean = false

        /**
         * 以kotlin-dsl的方式，初始化日志库配置
         * @param init dsl配置方法
         * @return 全局日志Tag的壳对象
         * */
        @JvmStatic
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
         * @return 全局日志Tag的壳对象
         * */
        @JvmStatic
        @Synchronized
        fun prepare(config: LoggerConfig) : Logger{
            require(!isPrepared){ "cant replay prepare Logger，only config once" }
            logConfig = config
            isPrepared = true
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
     * 关闭当前日志tag作用域的日志输出
     * */
    fun silence() : Logger{
        this.isEnable = false
        return this
    }


    //<editor-fold desc="日志输出方法">

    /**
     * 输出verbose等级日志
     * - 优先以当前壳对象的局部日志tag
     * @param msg 日志内容
     * */
    fun v(msg: Any?) = v(globalTag,msg)
    /**
     * 输出verbose等级日志
     * @param tag 临时日志tag,如果为null，则使用当前对象的局部日志tag
     * @param msg 日志内容
     * */
    fun v(tag : String?= null,msg: Any?) = performPrintLog(LoggerLevel.VERBOSE,tag,msg)

    /**
     * 输出info等级日志
     * - 优先以当前壳对象的局部日志tag
     * @param msg 日志内容
     */
    fun i(msg: Any?) = i(globalTag,msg)
    /**
     * 输出info等级日志
     * @param tag 临时日志tag,如果为null，则使用当前对象的局部日志tag
     * @param msg 日志内容
     */
    fun i(tag: String?= null, msg : Any?) = performPrintLog(LoggerLevel.INFO,tag,msg)

    /**
     * 输出debug等级日志
     * - 优先以当前壳对象的局部日志tag
     * @param msg 日志内容
     */
    fun d(msg: Any?) = d(globalTag,msg)
    /**
     * 输出debug等级日志
     * @param tag 临时日志tag,如果为null，则使用当前对象的局部日志tag
     * @param msg 日志内容
     */
    fun d(tag: String? = null, msg: Any?) = performPrintLog(LoggerLevel.DEBUG,tag, msg)

    /**
     * 输出warn等级日志
     * - 优先以当前壳对象的局部日志tag
     * @param msg 日志内容
     */
    fun w(msg: Any?) = w(globalTag,msg)
    /**
     * 输出warn等级日志
     * @param tag 临时日志tag,如果为null，则使用当前对象的局部日志tag
     * @param msg 日志内容
     */
    fun w(tag: String? = null, msg: Any?) = performPrintLog(LoggerLevel.WARN,tag, msg)

    /**
     * 输出error等级日志
     * - 优先以当前壳对象的局部日志tag
     * @param msg 日志内容
     */
    fun e(msg: Any?) = e(globalTag,msg)
    /**
     * 输出error等级日志
     * @param tag 临时日志tag,如果为null，则使用当前对象的局部日志tag
     * @param msg 日志内容
     */
    fun e(tag: String? = null, msg: Any?) = performPrintLog(LoggerLevel.ERROR,tag, msg)

    // </editor-fold>

    /**
     * 执行输出日志操作
     * - 日志输出的实际入口
     * @param level 日志等级
     * @param tag 日志tag，如果为null，则使用壳对象的局部tag，
     * 如果局部tag也为null，则使用配置的全局tag。
     * @param message 日志内容，如果为null则不进行输出
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