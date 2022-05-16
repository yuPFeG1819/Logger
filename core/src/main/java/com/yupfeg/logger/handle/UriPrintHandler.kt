package com.yupfeg.logger.handle

import android.net.Uri
import com.yupfeg.logger.LogPrintRequest
import com.yupfeg.logger.converter.formatJSONString
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.parse.Parsable
import org.json.JSONObject

/**
 * 支持[Uri]类型的日志类型处理器
 * @author yuPFeG
 * @date 2022/04/22
 */
class UriPrintHandler : BasePrintHandler() , Parsable<Uri> {
    override fun isHandleContent(request: LogPrintRequest): Boolean
        = request.logContent is Uri

    override fun formatLogContentOnlyWrap(
        logFormatter: Formatter,
        request: LogPrintRequest
    ): String = formatLogContent(logFormatter, request)

    override fun formatLogContent(logFormatter: Formatter, request: LogPrintRequest): String {
        val logContentFormat = getLogFormatContentWrap(logFormatter)
        val header = "${request.logContent.javaClass}${Formatter.BR}${logFormatter.left}"
        return String.format(
            logContentFormat,
            header + parse2String(request.logContent as Uri,logFormatter)
        )
    }

    override fun parse2String(content: Uri, formatter: Formatter): String {
        return JSONObject().apply {
            put("Scheme", content.scheme)
            put("Host", content.host)
            put("Port", content.port)
            put("Path", content.path)
            put("Query", content.query)
            put("Fragment", content.fragment)
        }
            .formatJSONString()
            .replace("\n", "${Formatter.BR}${formatter.left}")
    }
}