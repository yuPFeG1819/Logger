package com.yupfeg.logger.handle

import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.converter.formatJSONString
import com.yupfeg.logger.converter.isPrimitiveTypeToString
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.LogPrintRequest
import com.yupfeg.logger.handle.parse.Parsable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * [Collection]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/22
 */
internal class CollectionPrintHandler : BasePrintHandler(), Parsable<Collection<*>> {

    private val mListHeaderFormat by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        "%s size = %d${Formatter.BR}"
    }

    override fun isHandleContent(request: LogPrintRequest): Boolean {
        return request.logContent is Collection<*>
    }

    override fun formatLogContentOnlyWrap(
        logFormatter: Formatter,
        request: LogPrintRequest
    ): String {
        val collect = (request.logContent as Collection<*>)
        val extraContent = String.format(mListHeaderFormat, collect.javaClass, collect.size)
        val logFormatContent = getLogFormatContentWrap(logFormatter)
        return String.format(
            logFormatContent, "${extraContent}${logFormatter.left}${collect}"
        )
    }

    override fun formatLogContent(logFormatter: Formatter, request : LogPrintRequest): String {
        val collect = (request.logContent as Collection<*>)
        val firstItem = collect.firstOrNull()
        val extraContent = String.format(mListHeaderFormat, collect.javaClass, collect.size)
        val logFormatContent = getLogFormatContentWrap(logFormatter)
        return if (isPrimitiveTypeToString(firstItem)){
            //集合内部是基本数据类型，直接添加`toString`的内容
            String.format(
                logFormatContent, "${extraContent}${logFormatter.left}${collect}"
            )
        }else{
            //其他数据类型，需要特殊解析处理
            val parseContent = parse2String(collect,logFormatter)
            String.format(logFormatContent, "${extraContent}$parseContent")
        }
    }

    override fun parse2String(content: Collection<*>, formatter: Formatter): String {
        return try {
            content.parseToJSONArray(globalJsonConverter)
                .formatJSONString()
                .replace("\n", "${Formatter.BR}${formatter.left}")
        }catch (e : Exception){
            content.toString().replace("\n", "${Formatter.BR}${formatter.left}")
        }
    }

    /**
     * [Collection]拓展函数，解析 [Collection] 转化为 [JSONArray]
     * * 仅用于Logger日志输出使用
     * @param jsonConverter json解析类
     */
    @Throws(JSONException::class,RuntimeException::class)
    private fun Collection<*>.parseToJSONArray(jsonConverter: JsonConverter): JSONArray {
        return JSONArray().also { jsonArray->
            this.map { item ->
                item ?: return@map
                val objStr = jsonConverter.toJson(item)
                jsonArray.put(JSONObject(objStr))
            }
        }
    }
}