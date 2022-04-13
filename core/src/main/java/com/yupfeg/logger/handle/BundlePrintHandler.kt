package com.yupfeg.logger.handle

import android.os.Bundle
import com.yupfeg.logger.LogPrintRequest
import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.converter.formatJSONString
import com.yupfeg.logger.converter.parseToJSONObject
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.parse.Parsable

/**
 * [Bundle]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/04
 */
internal class BundlePrintHandler : BasePrintHandler(), Parsable<Bundle> {

    override fun isHandleContent(request : LogPrintRequest): Boolean {
        return request.logContent is Bundle
    }

    override fun formatLogContent(logFormatter: Formatter, request : LogPrintRequest): String {
        val logContentFormat = getLogFormatContentWrap(logFormatter)
        val logContent = parse2String(
            request.logContent as Bundle,logFormatter,globalJsonConverter
        )
        return String.format(logContentFormat,logContent)
    }

    override fun parse2String(
        content: Bundle,
        formatter: Formatter,
        jsonConverter: JsonConverter
    ): String {
        val header = "${content.javaClass}${Formatter.BR}${formatter.left}"
        val logContent = try {
            content
                .parseToJSONObject(jsonConverter)
                .formatJSONString()
                .replace("\n", "\n${formatter.left}")
        }catch (e : Exception){
            content.toString().replace("\n", "\n${formatter.left}")
        }
        return "$header$logContent"
    }

}