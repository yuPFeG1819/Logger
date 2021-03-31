package com.yupfeg.logger.handle

import com.yupfeg.logger.converter.json.formatJSONString
import com.yupfeg.logger.converter.json.isPrimitiveType
import com.yupfeg.logger.Logger
import com.yupfeg.logger.converter.json.parseToJSONArray
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.PrintHandlerConfig
import com.yupfeg.logger.handle.parse.Parsable

/**
 * [Collection]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/22
 */
class CollectionPrintHandler : BasePrintHandler(), Parsable<Collection<*>> {
    override fun handleContent(content: Any, handleConfig: PrintHandlerConfig): Boolean {
        if (content !is Collection<*>) return false
        val firstItem = content.firstOrNull()
        val isPrimitiveType = isPrimitiveType(firstItem)
        takeIf { isPrimitiveType }
            ?.also {
                //属于基础数据类型
                printPrimitiveTypeList(content,handleConfig)
            }
            ?:run{
                //不属于基础数据类型
                handleConfig.printers.map {
                    val logContentFormat = Logger.getFormatLogContent(it.logFormatter)
                    it.printLog(
                        handleConfig.logLevel,handleConfig.tag,
                        String.format(logContentFormat, parse2String(content,it.logFormatter))
                    )
                }
            }
        return true
    }

    override fun parse2String(content: Collection<*>, formatter: Formatter): String {
        var listHeader = "%s size = %d ${Formatter.BR}"
        listHeader = String.format(listHeader, content.javaClass, content.size) + formatter.leftSplitter()
        val logJsonContent = content
            .parseToJSONArray()
            .formatJSONString()
            .replace("\n", "\n${formatter.leftSplitter()}")
        return "${listHeader}${logJsonContent}"
    }

    /**
     * 输出基础数据类型的列表日志数据
     * @param content
     * @param handleConfig
     */
    private fun printPrimitiveTypeList(content: Collection<*>,handleConfig: PrintHandlerConfig){
        val simpleName = content.javaClass
        val listHeaderFormat = "%s size = %d${Formatter.BR}"
        handleConfig.printers.map {
            val logHeader = String.format(listHeaderFormat, simpleName, content.size) +
                    it.logFormatter.leftSplitter()
            val logContent = Logger.getFormatLogContent(it.logFormatter)
            it.printLog(
                handleConfig.logLevel,handleConfig.tag,
                String.format(logContent, "${logHeader}$content")
            )
        }
    }
}