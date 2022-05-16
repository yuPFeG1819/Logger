package com.yupfeg.logger.handle.wrap

import com.yupfeg.logger.Logger
import com.yupfeg.logger.LoggerCore
import com.yupfeg.logger.handle.BasePrintHandler
import com.yupfeg.logger.printer.ILogPrinter

/**
 * 日志输出方法调用栈的显示过滤器
 * @author yuPFeG
 * @date 2022/04/13
 */
interface ILogInvokeStackFilter {
    /**
     * 调用栈信息过滤规则
     * @param element 调用栈对象
     * @return true - 满足条件，暂定为可使用的栈信息 ； false - 不满足条件，过滤该调用栈信息
     * */
    fun onFilter(element : StackTraceElement) : Boolean
}

internal class DefaultLogInvokeStackFilter : ILogInvokeStackFilter{
    override fun onFilter(element: StackTraceElement): Boolean {
        val name = element.className
        //避免混淆情况下，可能获取不到栈的类名
        if (name.isNullOrEmpty()) return false
        return name != ILogPrinter::class.java.name
                && name != BasePrintHandler::class.java.name
                && name != Logger::class.java.name
                && name != LoggerCore::class.java.name
                && !name.contains("LoggerExtKt")
    }

}