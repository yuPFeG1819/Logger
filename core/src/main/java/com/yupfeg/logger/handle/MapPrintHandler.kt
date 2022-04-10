package com.yupfeg.logger.handle

import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.converter.formatJSONString
import com.yupfeg.logger.converter.parseToJSONObject
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.LogPrintRequest
import com.yupfeg.logger.handle.parse.Parsable

/**
 * [Map]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/22
 */
internal class MapPrintHandler : BasePrintHandler(), Parsable<Map<*, *>> {

    override fun isHandleContent(request: LogPrintRequest): Boolean {
        //不属于map类型不予处理，转为下一个handler进行处理
        return request.logContent is Map<*,*>
    }

    override fun formatLogContent(logFormatter: Formatter, request : LogPrintRequest): String {
        val logFormat = getLogFormatContentWrap(logFormatter)
        return String.format(
            logFormat,
            parse2String(request.logContent as Map<*, *>,logFormatter,globalJsonConverter)
        )
    }

    override fun parse2String(
        content: Map<*, *>,
        formatter: Formatter,
        jsonConverter: JsonConverter
    ): String {
        val header = "${content.javaClass} size = ${content.size}${Formatter.BR}${formatter.left}"
        val logContent = content
            .parseToJSONObject(jsonConverter)
            .formatJSONString()
            .replace("\n", "\n${formatter.left}")
        return "$header$logContent"
    }
}