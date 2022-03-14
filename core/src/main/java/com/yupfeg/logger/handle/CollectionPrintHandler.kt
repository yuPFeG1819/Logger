package com.yupfeg.logger.handle

import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.converter.json.formatJSONString
import com.yupfeg.logger.converter.json.isPrimitiveType
import com.yupfeg.logger.converter.json.parseToJSONArray
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.LogPrintRequest
import com.yupfeg.logger.handle.parse.Parsable

/**
 * [Collection]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/22
 */
class CollectionPrintHandler : BasePrintHandler(), Parsable<Collection<*>> {

    private val mListHeaderFormat by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        "%s size = %d${Formatter.BR}"
    }

    override fun isHandleContent(request: LogPrintRequest): Boolean {
        return request.logContent is Collection<*>
    }

    override fun formatLogContent(logFormatter: Formatter, request : LogPrintRequest): String {
        val collect = (request.logContent as Collection<*>)
        val firstItem = collect.firstOrNull()
        val isPrimitiveType = isPrimitiveType(firstItem)
        val extraContent = String.format(mListHeaderFormat, collect.javaClass, collect.size)
        val logFormatContent = getFormatLogContentWrapper(logFormatter,request)
        return if (isPrimitiveType){
            String.format(
                logFormatContent, "${extraContent}${logFormatter.leftSplitter()}${collect}"
            )
        }else{
            val parseContent = parse2String(
                collect,logFormatter,request.jsonConverter
            )
            String.format(logFormatContent, "${extraContent}$parseContent")
        }
    }

    override fun parse2String(
        content: Collection<*>,
        formatter: Formatter,
        jsonConverter: JsonConverter
    ): String {
        return content
            .parseToJSONArray(jsonConverter)
            .formatJSONString()
            .replace("\n", "\n${formatter.leftSplitter()}")
    }
}