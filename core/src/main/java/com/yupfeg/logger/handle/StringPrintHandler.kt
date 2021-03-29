package com.yupfeg.logger.handle

import com.yupfeg.logger.json.formatJSONString
import com.yupfeg.logger.Logger
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.PrintHandlerConfig
import com.yupfeg.logger.handle.parse.Parsable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.StringBuilder

/**
 * [String]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/04
 */
internal class StringPrintHandler : BasePrintHandler(), Parsable<String> {

    override fun handleContent(content: Any, handleConfig: PrintHandlerConfig): Boolean {
        val logMsg : String = if (content is StringBuilder || content is StringBuffer){
            content.toString().trim { it <= ' ' }
        }else if (content is String) {
            content.trim { it <= ' ' }
        }else{
            ""
        }
        if (logMsg.isEmpty()) return false

        //遍历所有输出类，输出对应到指定渠道
        handleConfig.printers.forEach { printer ->
            val extraHeader = Logger.getFormatLogContent(printer.logFormatter)
            val logContent = String.format(extraHeader,parse2String(logMsg,printer.logFormatter))
            printer.printLog(handleConfig.logLevel,handleConfig.tag,logContent)
        }
        return true
    }

    override fun parse2String(content: String, formatter: Formatter) : String{
        var message : String
        try {
            when {
                //json对象
                content.startsWith("{") -> {
                    val jsonObject = JSONObject(content)
                    message = jsonObject.formatJSONString().run {
                        replace("\n", "\n${formatter.leftSplitter()}")
                    }
                }
                //json数组
                content.startsWith("[") -> {
                    val jsonArray = JSONArray(content)
                    message = jsonArray.formatJSONString().run {
                        replace("\n", "\n${formatter.leftSplitter()}")
                    }
                }
                else -> {
                    // 普通的字符串
                    message = content.replace("\n", "\n${formatter.leftSplitter()}")
                }
            }
        } catch (e: JSONException) {
            message = ""
        }

        return message
    }


}