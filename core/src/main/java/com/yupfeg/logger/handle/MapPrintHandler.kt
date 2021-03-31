package com.yupfeg.logger.handle

import com.yupfeg.logger.converter.json.formatJSONString
import com.yupfeg.logger.Logger
import com.yupfeg.logger.converter.json.parseToJSONObject
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.PrintHandlerConfig
import com.yupfeg.logger.handle.parse.Parsable

/**
 * [Map]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/22
 */
class MapPrintHandler : BasePrintHandler(), Parsable<Map<*, *>> {
    override fun handleContent(content: Any, handleConfig: PrintHandlerConfig): Boolean {
        //不属于map类型不予处理，转为下一个handler进行处理
        if (content !is Map<*,*>) return false
        handleConfig.printers.map {
            val s = Logger.getFormatLogContent(it.logFormatter)
            val logMsg = String.format(s, parse2String(content,it.logFormatter))
            it.printLog(handleConfig.logLevel,handleConfig.tag,logMsg)
        }
        return true
    }

    override fun parse2String(content: Map<*, *>, formatter: Formatter): String {
        val header = "${content.javaClass}${Formatter.BR}${formatter.leftSplitter()}"
        val logContent = content
            .parseToJSONObject()
            .formatJSONString()
            .replace("\n", "\n${formatter.leftSplitter()}")
        return "$header$logContent"
    }
}