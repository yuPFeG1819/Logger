package com.yupfeg.sample

import android.app.Application
import com.yupfeg.logger.LoggerLevel
import com.yupfeg.logger.ext.setDslLoggerConfig
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.formatter.SimpleFormatterImpl
import com.yupfeg.logger.printer.ILogPrinter
import com.yupfeg.logger.printer.LogcatPrinter

/**
 *
 * @author yuPFeG
 * @date 2021/03/28
 */
class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        //初始化日志管理器
        setDslLoggerConfig {
            isDisplayClassInfo = true
            logHeaders = listOf(
                "test log headers","second log header"
            )
            logPrinters = listOf(LogcatPrinter(),TestPrinter())
        }
    }
}

private class TestPrinter : ILogPrinter{
    override val logFormatter: Formatter
        get() = SimpleFormatterImpl
    override val isEnable: Boolean
        get() = true

    override fun printLog(logLevel: LoggerLevel, tag: String, msg: String) {
        println(msg)
    }

}