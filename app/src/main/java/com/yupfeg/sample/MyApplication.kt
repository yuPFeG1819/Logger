package com.yupfeg.sample

import android.app.Application
import com.yupfeg.logger.BuildConfig
import com.yupfeg.logger.ext.setDslLoggerConfig
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
            logPrinters = listOf(LogcatPrinter())
        }
    }
}