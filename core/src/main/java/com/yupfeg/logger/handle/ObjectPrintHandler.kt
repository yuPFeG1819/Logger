package com.yupfeg.logger.handle

import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.converter.formatJSONString
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.LogPrintRequest
import com.yupfeg.logger.handle.parse.Parsable
import org.json.JSONException
import org.json.JSONObject

/**
 * 对于[Object]类型的日志输出处理类。
 * * 默认为日志处理责任链的最后一节，承担兜底作用，防止存在日志无法进行处理
 * @author yuPFeG
 * @date 2021/01/22
 */
internal class ObjectPrintHandler : BasePrintHandler(), Parsable<Any> {
    override fun isHandleContent(request: LogPrintRequest): Boolean {
        return true
    }

    override fun formatLogContent(logFormatter: Formatter, request : LogPrintRequest): String {
        val logFormat = getLogFormatContentWrap(logFormatter)
        val originContent = request.logContent
        val logContent = "${originContent.javaClass}${Formatter.BR}${logFormatter.left} " +
                parse2String(originContent,logFormatter)
        return String.format(logFormat,logContent)
    }

    override fun parse2String(content: Any, formatter: Formatter): String {
        return try {
            parse2JsonString(content, formatter, globalJsonConverter)
        }catch (e : Exception){
            content.toString().replace("\n", "${Formatter.BR}${formatter.left}")
        }
    }

    @Throws(JSONException::class,RuntimeException::class)
    private fun parse2JsonString(
        content: Any,
        formatter: Formatter,
        jsonConverter: JsonConverter
    ) : String{
        return jsonConverter.toJson(content).run { JSONObject(this) }
            .formatJSONString()
            .replace("\n", "${Formatter.BR}${formatter.left}")
    }
}