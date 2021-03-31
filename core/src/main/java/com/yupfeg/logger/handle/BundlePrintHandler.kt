package com.yupfeg.logger.handle

import android.os.Bundle
import com.yupfeg.logger.Logger
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.PrintHandlerConfig
import com.yupfeg.logger.handle.parse.Parsable
import com.yupfeg.logger.converter.json.formatJSONString
import com.yupfeg.logger.converter.json.parseToJSONObject

/**
 * [Bundle]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/04
 */
internal class BundlePrintHandler : BasePrintHandler(), Parsable<Bundle> {

    override fun handleContent(content: Any, handleConfig: PrintHandlerConfig): Boolean {
        if (content is Bundle){
            handleConfig.printers.map { printer->
                val logContentFormat = Logger.getFormatLogContent(printer.logFormatter)
                printer.printLog(
                    handleConfig.logLevel, handleConfig.tag,
                    String.format(logContentFormat, parse2String(content,printer.logFormatter))
                )
            }
            return true
        }
        return false
    }

    override fun parse2String(content: Bundle, formatter: Formatter): String {
        val header = "${content.javaClass}${Formatter.BR}${formatter.leftSplitter()}"
        val logContent = content
            .parseToJSONObject()
            .formatJSONString()
            .replace("\n", "\n${formatter.leftSplitter()}")
        return "$header$logContent"
    }

}