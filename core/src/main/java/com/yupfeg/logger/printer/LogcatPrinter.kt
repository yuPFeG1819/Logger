package com.yupfeg.logger.printer

import android.util.Log
import com.yupfeg.logger.Logger
import com.yupfeg.logger.LoggerLevel
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.formatter.SimpleFormatterImpl

/**
 * 默认输出日志到Logcat的日志输出类
 * @author yuPFeG
 * @date 2020/12/31
 */
class LogcatPrinter(
    /**格式化日志输出格式*/
    formatter : Formatter = SimpleFormatterImpl
) : BaseLogPrinter(formatter){

    companion object {
        /**
         * Logcat输出日志的最大长度
         * * Android ide的Logcat日志长度会有限度，超出4K长度会截断
         * */
        private const val MAX_STRING_LENGTH = 3 * 1024
    }

    override val isEnable: Boolean
        get() = Logger.isDebug

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
    private fun preparePrintLongLog(logLevel: LoggerLevel, tag: String, content: String) {
        val logLength = content.length
        if (logLength <= MAX_STRING_LENGTH){
            printToLogcat(logLevel, tag, content)
            return
        }

        var i = 0
        while (i < logLength) {
            if (i + MAX_STRING_LENGTH < content.length) {
                if (i==0) {
                    printToLogcat(logLevel, tag, content.substring(i, i + MAX_STRING_LENGTH))
                } else {
                    printToLogcat(logLevel, "", content.substring(i, i + MAX_STRING_LENGTH))
                }
            } else
                printToLogcat(logLevel, "", content.substring(i, content.length))
            i += MAX_STRING_LENGTH
        }

    }

    /**
     * 输出日志到Logcat
     * @param logLevel 日志等级
     * @param tag 日志tag
     * @param content 日志内容
     */
    private fun printToLogcat(logLevel: LoggerLevel, tag: String, content: String){
        when(logLevel) {
            LoggerLevel.ERROR -> Log.e(tag, content)
            LoggerLevel.WARN  -> Log.w(tag, content)
            LoggerLevel.INFO  -> Log.i(tag, content)
            LoggerLevel.DEBUG -> Log.d(tag, content)
            LoggerLevel.VERBOSE -> Log.v(tag,content)
        }
    }

}