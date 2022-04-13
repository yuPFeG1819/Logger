package com.yupfeg.logger.handle

import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.converter.formatJSONString
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.LogPrintRequest
import com.yupfeg.logger.handle.parse.Parsable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * [String]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/04
 */
internal class StringPrintHandler : BasePrintHandler(), Parsable<String> {

    override fun isHandleContent(request: LogPrintRequest): Boolean {
        return trimLogContent(request.logContent).isNotEmpty()
    }

    override fun formatLogContent(logFormatter: Formatter, request: LogPrintRequest): String {
        val logContentFormat = getLogFormatContentWrap(logFormatter)
        val trimLog = trimLogContent(request.logContent)
        val parseContent = parse2String(trimLog,logFormatter,globalJsonConverter)
        return String.format(logContentFormat, parseContent)
    }

    private fun trimLogContent(content: Any) : String{
        return if (content is StringBuilder || content is StringBuffer){
            content.toString().trim { it <= ' ' }
        }else if (content is String) {
            content.trim { it <= ' ' }
        }else{
            ""
        }
    }

    override fun parse2String(
        content: String,
        formatter: Formatter,
        jsonConverter: JsonConverter
    ) : String{
        return try {
            when {
                //json对象
                content.startsWith("{") -> {
                    val jsonObject = JSONObject(content)
                    jsonObject.formatJSONString().run {
                        replace("\n", "\n${formatter.left}")
                    }
                }
                //json数组
                content.startsWith("[") -> {
                    val jsonArray = JSONArray(content)
                    jsonArray.formatJSONString().run {
                        replace("\n", "\n${formatter.left}")
                    }
                }
                // 普通的字符串
                else -> {
                    content.replace("\n", "\n${formatter.left}")
                }
            }
        } catch (e: JSONException) {
            //特殊情况，解析失败，按普通字符串处理
            content.replace("\n", "\n${formatter.left}")
        }
    }


}