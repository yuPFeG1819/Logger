package com.yupfeg.logger.handle.wrap

import com.yupfeg.logger.formatter.Formatter
import com.yupfeg.logger.handle.config.PrintHandleConfig
import org.jetbrains.annotations.TestOnly


/**
 * 日志内容包装类
 * @author yuPFeG
 * @date 2022/04/12
 */
class LogContentWrapHelper internal constructor(
    private val printHandleConfig : PrintHandleConfig
) {

    companion object{
        /**最小的调用栈偏移量，尽可能确保过滤调用栈内的无用信息，方便快速定位到实际调用日志输出的调用位置*/
        private const val MIN_STACK_OFFSET = 12
    }

    /**
     * 创建格式化的日志内容包装字符串
     * @param formatter 日志输入格式化类
     * @return 包含`"%s"`的日志输出字符串，需要外部使用[String.format]方法，将“%s“格式化替换为实际日志内容
     * */
    fun createFormatWrapString(formatter: Formatter): String {
        return StringBuilder().apply {
            append(formatter.top)
            //顶部额外信息
            appendLogHeaderContent(formatter,printHandleConfig.logHeaders)
            if (printHandleConfig.isPrintThreadInfo){
                //调用所在线程信息
                appendThreadInfo(formatter)
            }
            if (printHandleConfig.isPrintClassInfo){
                //当前调用位置信息
                appendLogInvokeStack(formatter)
            }

            append(formatter.left)
            //实际日志内容，通过String.format进行替换
            append("%s")
            //日志框底部格式
            append(formatter.bottom)
        }.toString()
    }

    /**
     * [StringBuilder]拓展函数，添加日志内容顶部额外信息
     * @param formatter 日志输出的格式化类
     * @param logHeaders 日志顶部额外信息
     * */
    private fun StringBuilder.appendLogHeaderContent(
        formatter: Formatter,
        logHeaders : List<String>?
    ){
        logHeaders?.takeIf { it.isNotEmpty() }
            ?.also { headers->
                for (header in headers) {
                    append(formatter.left)
                    append("$header ${Formatter.BR}")
                }
                deleteCharAt(this.lastIndex)
                append(formatter.middle)
            }
    }

    /**
     * [StringBuilder]拓展函数，添加日志方法调用的线程信息
     * @param formatter 日志输出的格式化类
     * */
    private fun StringBuilder.appendThreadInfo(
        formatter: Formatter,
    ){
        //显示当前线程名
        append(formatter.left)
        append("Thread : ${Thread.currentThread().name}")
        append(formatter.middle)
    }

    /**
     * [StringBuilder]的拓展函数，添加日志方法调用位置的栈信息
     * * 当前外部调用日志输出的调用栈信息，包括 类名、方法名、行数
     * @param formatter 日志输出的格式化类
     * */
    private fun StringBuilder.appendLogInvokeStack(formatter: Formatter){
        val sElements = Thread.currentThread().stackTrace
        if (sElements.isNullOrEmpty()) return
        val stackOffset = sElements.calculateStackOffset()
        sElements[stackOffset]?.apply {
            append(formatter.left)
            append(if (className.isNullOrEmpty()) "unknownClass" else className)
            append(".")
            append(if (methodName.isNullOrEmpty()) "unknownMethod" else methodName)
            append(" (")
            append(if (fileName.isNullOrEmpty()) "UnknownFile" else fileName)
            append(" : line :")
            append(lineNumber)
            append(")")
            append(formatter.middle)
        }
    }

    /**
     * [StackTraceElement]数组的拓展函数，计算调用栈的调用层次
     * */
    private fun Array<StackTraceElement?>.calculateStackOffset(): Int {
        var i = MIN_STACK_OFFSET
        looper@ while (i < this.size) {
            var isFilter: Boolean
            val currElement = this[i]
            currElement?:continue@looper
            for (stackFilter in printHandleConfig.invokeStackFilters){
                //在混淆条件下，特定的栈信息里的名称可能为空，如果此时使用栈的类名进行判断会有空指针风险
                isFilter = try {
                    if (currElement.className.isNotEmpty()){
                        stackFilter.onFilter(currElement)
                    }else{
                        false
                    }
                }catch (e : Exception){
                    //ignore
                    false
                }

                //只要不满足其中一个过滤器条件，抛弃该栈信息
                if (!isFilter) {
                    i++
                    continue@looper
                }
            }
            //所有过滤器都满足条件，直接使用该栈信息
            return i
        }
        return 0
    }

    /**
     * [StringBuilder]的拓展函数，打印当前所有调用栈信息
     * -仅测试用的，用于测试调用栈打印信息
     * */
    @Suppress("unused")
    @TestOnly
    private fun StringBuilder.appendLogStackTrace(formatter: Formatter){
        val sElements = Thread.currentThread().stackTrace
        for (sElement in sElements) {
            append(formatter.left)
            append(sElement.className)
            append(".")
            append(sElement.methodName)
            append(" ")
            append("(")
            append(sElement.fileName?:"UnknownFile")
            append(": line :")
            append(sElement.lineNumber)
            append(")")
            append("\n")
        }
        append(formatter.middle)
    }
}