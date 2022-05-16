package com.yupfeg.logger.handle

import android.os.Bundle
import com.yupfeg.logger.LogPrintRequest
import com.yupfeg.logger.converter.formatJSONString
import com.yupfeg.logger.converter.parseToJSONObject
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.parse.Parsable
import org.json.JSONException

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
        val logContent = parse2String(request.logContent as Bundle,logFormatter)
        return String.format(logContentFormat,logContent)
    }

    override fun parse2String(content: Bundle, formatter: Formatter): String {
        val header = "${content.javaClass}${Formatter.BR}${formatter.left}"
        val logContent = try {
            content.parseToJSONObject(globalJsonConverter)
                .formatJSONString()
                .replace("\n", "${Formatter.BR}${formatter.left}")
        }catch (e : JSONException){
            content.toString().replace("\n", "${Formatter.BR}${formatter.left}")
        }
        return "$header$logContent"
    }

}