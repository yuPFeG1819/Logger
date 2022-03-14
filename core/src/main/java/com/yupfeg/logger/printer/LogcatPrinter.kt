package com.yupfeg.logger.printer

import android.util.Log
import com.yupfeg.logger.LoggerLevel
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.formatter.SimpleFormatterImpl

/**
 * 默认输出日志到Logcat的日志输出类
 * @author yuPFeG
 * @date 2020/12/31
 */
@Suppress("unused")
open class LogcatPrinter(
    /**格式化日志输出格式*/
    formatter : Formatter = SimpleFormatterImpl,
    private val enable : Boolean = true
) : BaseLogPrinter(formatter){

    companion object {
        /**
         * Logcat输出日志的最大长度
         * * Android ide的Logcat日志长度会有限度，超出4K长度会截断
         * */
        private const val MAX_STRING_LENGTH = 3 * 1024
    }

    override val isEnable: Boolean
        get() = enable

    override fun performPrintLog(logLevel: LoggerLevel, tag: String, msg: String) {
        //输出到Logcat上的日志内容，需要在换行符前添加内容才能使换行符生效
        preparePrintLongLog(logLevel,tag,"-print to logcat- $msg")
    }

    /**
     * 准备输出长日志
     * * 支持打印长日志（Logcat有最大单次输出长度限制）
     * @param logLevel 日志等级
     * @param tag 日志tag
     * @param content 日志内容
     */
    protected open fun preparePrintLongLog(logLevel: LoggerLevel, tag: String, content: String) {
        val logLength = content.length
        if (logLength <= MAX_STRING_LENGTH){
            printToLogcat(logLevel, tag, content)
            return
        }

        var length = 0
        while (length < logLength) {
            if (length + MAX_STRING_LENGTH < content.length) {
                if (length==0) {
                    printToLogcat(logLevel, tag, content.substring(length, length + MAX_STRING_LENGTH))
                } else {
                    printToLogcat(logLevel, "", content.substring(length, length + MAX_STRING_LENGTH))
                }
            } else
                printToLogcat(logLevel, "", content.substring(length, content.length))
            length += MAX_STRING_LENGTH
        }

    }

    /**
     * 输出日志到Logcat
     * @param logLevel 日志等级
     * @param tag 日志tag
     * @param content 日志内容
     */
    protected open fun printToLogcat(logLevel: LoggerLevel, tag: String, content: String){
        when(logLevel) {
            LoggerLevel.ERROR -> Log.e(tag, content)
            LoggerLevel.WARN  -> Log.w(tag, content)
            LoggerLevel.INFO  -> Log.i(tag, content)
            LoggerLevel.DEBUG -> Log.d(tag, content)
            LoggerLevel.VERBOSE -> Log.v(tag,content)
        }
    }

}