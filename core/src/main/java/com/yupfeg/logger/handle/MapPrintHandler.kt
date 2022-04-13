package com.yupfeg.logger.handle

import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.converter.formatJSONString
import com.yupfeg.logger.converter.isPrimitiveTypeValue
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.LogPrintRequest
import com.yupfeg.logger.handle.parse.Parsable
import org.json.JSONException
import org.json.JSONObject

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
        val logContent = try {
            content.parseToJSONObject(jsonConverter)
                .formatJSONString()
                .replace("\n", "\n${formatter.left}")
        }catch (e: Exception){
            content.toString().replace("\n", "\n${formatter.left}")
        }
        return "$header$logContent"
    }

    /**
     * [Map]的拓展函数，解析 map 为 JSONObject
     * * 仅用于Logger日志输出使用
     * @param jsonConverter json解析类
     */
    @Throws(JSONException::class,RuntimeException::class)
    private fun Map<*, *>.parseToJSONObject(jsonConverter: JsonConverter): JSONObject {
        val originMap = this
        return JSONObject().also { jsonObject->
            val firstValue = originMap.values.firstOrNull()
            val isPrimitiveType = isPrimitiveTypeValue(firstValue)
            originMap.keys.map {item->
                item?:return@map
                if (isPrimitiveType) {
                    jsonObject.put(item.toString(), originMap[item])
                } else {
                    jsonObject.put(
                        item.toString(),
                        JSONObject(jsonConverter.toJson(originMap[item] ?: "{}"))
                    )
                }
            }
        }
    }
}