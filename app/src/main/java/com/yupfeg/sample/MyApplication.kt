package com.yupfeg.sample

import android.app.Application
import com.yupfeg.logger.BuildConfig
import com.yupfeg.logger.ext.setDslLoggerConfig

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
            isDebug = BuildConfig.DEBUG
            isDisplayClassInfo = true
            logHeaders = listOf(
                "test log headers","second log header"
            )
        }
    }
}