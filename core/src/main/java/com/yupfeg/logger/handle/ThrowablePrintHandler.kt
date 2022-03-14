package com.yupfeg.logger.handle

import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.LogPrintRequest
import com.yupfeg.logger.handle.parse.Parsable
import java.io.PrintWriter
import java.io.StringWriter

/**
 * [Throwable]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/04
 */
internal class ThrowablePrintHandler : BasePrintHandler(), Parsable<Throwable> {
    companion object{
        private const val DEF_STRING_WRITER_SIZE = 256
    }

    override fun isHandleContent(request: LogPrintRequest): Boolean {
        return request.logContent is Throwable
    }

    override fun formatLogContent(logFormatter: Formatter, request: LogPrintRequest): String {
        val formatContent = getFormatLogContentWrapper(logFormatter,request)
        return String.format(
            formatContent,parse2String(
                request.logContent as Throwable,logFormatter,request.jsonConverter
            )
        )
    }

    override fun parse2String(
        content: Throwable,
        formatter: Formatter,
        jsonConverter: JsonConverter
    ): String {
        val sw = StringWriter(DEF_STRING_WRITER_SIZE)
        val pw = PrintWriter(sw, false)
        content.printStackTrace(pw)
        pw.flush()
        return sw.toString().replace("\n", "\n${formatter.left}")
    }
}