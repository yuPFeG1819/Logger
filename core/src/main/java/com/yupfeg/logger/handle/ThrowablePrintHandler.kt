package com.yupfeg.logger.handle

import com.yupfeg.logger.Logger
import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.PrintHandlerConfig
import com.yupfeg.logger.handle.parse.Parsable
import java.io.PrintWriter
import java.io.StringWriter

/**
 * [Throwable]类型的日志输出处理类
 * @author yuPFeG
 * @date 2021/01/04
 */
internal class ThrowablePrintHandler : BasePrintHandler(), Parsable<Throwable> {
    companion object{
        private const val DEF_STRING_WRITER_SIZE = 256
    }

    override fun handleContent(content: Any, handleConfig: PrintHandlerConfig): Boolean {
        if (content is Throwable) {
            handleConfig.printers.forEach { printer->
                val extraHeader = Logger.getFormatLogContent(printer.logFormatter)
                val logMsg = String.format(extraHeader,parse2String(content,printer.logFormatter))
                printer.printLog(handleConfig.logLevel,handleConfig.tag,logMsg)
            }
            return true
        }
        return false
    }

    override fun parse2String(content: Throwable, formatter: Formatter): String {
        val sw = StringWriter(DEF_STRING_WRITER_SIZE)
        val pw = PrintWriter(sw, false)
        content.printStackTrace(pw)
        pw.flush()
        return sw.toString().replace("\n", "\n${formatter.leftSplitter()}")
    }
}