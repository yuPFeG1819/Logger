package com.yupfeg.logger.handle

import android.content.Intent
import android.os.Bundle
import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.converter.formatJSONString
import com.yupfeg.logger.converter.parseToJSONObject
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.LogPrintRequest
import com.yupfeg.logger.handle.parse.Parsable
import org.json.JSONException
import org.json.JSONObject

/**
 * [Intent]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/04
 */
internal class IntentPrintHandler : BasePrintHandler(), Parsable<Intent> {

    override fun isHandleContent(request: LogPrintRequest): Boolean {
        //不属于Intent类型不予处理，转为下一个handler进行处理
        return request.logContent is Intent
    }

    override fun formatLogContent(logFormatter: Formatter, request : LogPrintRequest): String {
        val format = getLogFormatContentWrap(logFormatter)
        return String.format(
            format, parse2String(request.logContent as Intent,logFormatter,globalJsonConverter)
        )
    }

    override fun parse2String(
        content: Intent,
        formatter: Formatter,
        jsonConverter: JsonConverter
    ): String {
        val header = "${content.javaClass}${Formatter.BR}${formatter.left}"
        return header + createIntentJSONObject(content,jsonConverter)
            .formatJSONString()
            .replace("\n", "\n${formatter.left}")
    }

    private fun createIntentJSONObject(content: Intent,jsonConverter: JsonConverter) : JSONObject{
        return JSONObject().apply {
            put("Scheme", content.scheme)
            put("Action", content.action)
            put("DataString", content.dataString)
            put("Type", content.type)
            put("Package", content.`package`)
            put("ComponentInfo", content.component)
            put("Categories", content.categories)
            content.extras?.also {
                this.put("Extras",parseBundleString(it,jsonConverter))
            }
        }
    }

    private fun parseBundleString(extras: Bundle,jsonConverter: JsonConverter) : String{
        return try {
            extras.parseToJSONObject(jsonConverter).formatJSONString()
        }catch (e : JSONException){
            "Invalid Log Bundle content Json"
        }
    }


}