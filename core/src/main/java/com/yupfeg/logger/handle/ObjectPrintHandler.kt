package com.yupfeg.logger.handle

import com.yupfeg.logger.json.JsonUtils
import com.yupfeg.logger.json.formatJSONString
import com.yupfeg.logger.Logger
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.PrintHandlerConfig
import com.yupfeg.logger.handle.parse.Parsable
import org.json.JSONObject

/**
 * 对于[Object]类型的日志输出处理类。
 * * 默认为日志处理责任链的最后一节，承担兜底作用，防止存在日志无法进行处理
 * @author yuPFeG
 * @date 2021/01/22
 */
class ObjectPrintHandler : BasePrintHandler(), Parsable<Any> {
    override fun handleContent(content: Any, handleConfig: PrintHandlerConfig): Boolean {
        handleConfig.printers.forEach {printer->
            var logContent = "${content.javaClass}${Formatter.BR}${printer.logFormatter.leftSplitter()}"
            logContent += parse2String(content,printer.logFormatter)
            val logFormat = Logger.getFormatLogContent(printer.logFormatter)
            printer.printLog(
                handleConfig.logLevel,handleConfig.tag,
                String.format(logFormat,logContent)
            )
        }
        return true
    }

    override fun parse2String(content: Any, formatter: Formatter): String {
        return JsonUtils.toJson(content).run { JSONObject(this) }
            .formatJSONString()
            .replace("\n", "\n${formatter.leftSplitter()}")
    }
}