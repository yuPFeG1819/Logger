package com.yupfeg.logger.handle

import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.converter.formatJSONString
import com.yupfeg.logger.converter.isPrimitiveType
import com.yupfeg.logger.converter.parseToJSONArray
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.LogPrintRequest
import com.yupfeg.logger.handle.parse.Parsable

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

    override fun formatLogContent(logFormatter: Formatter, request : LogPrintRequest): String {
        val collect = (request.logContent as Collection<*>)
        val firstItem = collect.firstOrNull()
        val isPrimitiveType = isPrimitiveType(firstItem)
        val extraContent = String.format(mListHeaderFormat, collect.javaClass, collect.size)
        val logFormatContent = getLogFormatContentWrap(logFormatter)
        return if (isPrimitiveType){
            //集合内部是基本数据类型，直接添加`toString`的内容
            String.format(
                logFormatContent, "${extraContent}${logFormatter.left}${collect}"
            )
        }else{
            //其他数据类型，需要特殊解析处理
            val parseContent = parse2String(
                collect,logFormatter,globalJsonConverter
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
            .replace("\n", "\n${formatter.left}")
    }
}