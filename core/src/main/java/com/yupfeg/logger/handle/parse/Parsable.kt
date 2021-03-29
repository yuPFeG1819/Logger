package com.yupfeg.logger.handle.parse

import com.yupfeg.logger.formatter.Formatter

/**
 * 目标对象解析成字符串输出能力的接口
 * @author yuPFeG
 * @date 2021/01/04
 */
interface Parsable<T> {
    /**
     * 指定对象类型解析成字符串
     * @param content 日志输出对象
     * @param formatter 日志输出格式化类型
     * */
    fun parse2String(content : T,formatter : Formatter) : String
}