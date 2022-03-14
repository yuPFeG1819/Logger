package com.yupfeg.sample

import android.app.Application
import com.yupfeg.logger.BuildConfig
import com.yupfeg.logger.LoggerLevel
import com.yupfeg.logger.ext.setDslLoggerConfig
import com.yupfeg.logger.formatter.SimpleFormatterImpl
import com.yupfeg.logger.printer.BaseLogPrinter
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

private class TestPrinter : BaseLogPrinter(logFormatter = SimpleFormatterImpl){
    override fun performPrintLog(logLevel: LoggerLevel, tag: String, msg: String) {
        println(msg)
    }

}