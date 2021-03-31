package com.yupfeg.logger.handle

import android.content.Intent
import android.os.Bundle
import com.yupfeg.logger.converter.json.formatJSONString
import com.yupfeg.logger.Logger
import com.yupfeg.logger.converter.json.parseToJSONObject
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.PrintHandlerConfig
import com.yupfeg.logger.handle.parse.Parsable
import org.json.JSONObject

/**
 * [Intent]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/04
 */
class IntentPrintHandler : BasePrintHandler(), Parsable<Intent> {

    override fun handleContent(content: Any, handleConfig: PrintHandlerConfig): Boolean {
        //不属于Intent类型不予处理，转为下一个handler进行处理
        if (content !is Intent) return false
        handleConfig.printers.map { printer->
            val s = Logger.getFormatLogContent(printer.logFormatter)
            printer.printLog(handleConfig.logLevel,handleConfig.tag,
                String.format(s, parse2String(content,printer.logFormatter))
            )
        }
        return true
    }

    override fun parse2String(content: Intent, formatter: Formatter): String {
        //内部函数
        fun parseBundleString(extras: Bundle)
            = extras.parseToJSONObject().formatJSONString()

        val header = "${content.javaClass}${Formatter.BR}${formatter.leftSplitter()}"
        return header + JSONObject().apply {
            put("Scheme", content.scheme)
            put("Action", content.action)
            put("DataString", content.dataString)
            put("Type", content.type)
            put("Package", content.`package`)
            put("ComponentInfo", content.component)
            put("Categories", content.categories)
            content.extras?.also {
                this.put("Extras",parseBundleString(it))
            }
        }
            .formatJSONString()
            .replace("\n", "\n${formatter.leftSplitter()}")
    }


}