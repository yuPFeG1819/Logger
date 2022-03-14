package com.yupfeg.logger.formatter

/**
 * 格式化日志接口，便于 Printer 进行打印。
 * 每一个 Printer 包含一个自身的 Formatter
 * @author: yuPFeG
 */
interface Formatter {
    companion object{
        /**换行符*/
        val BR = System.getProperty("line.separator")!!
        /**垂直方向的双竖线*/
        const val HORIZONTAL_DOUBLE_LINE = "║ "
        /**空格*/
        const val BLANK = " "
    }

    /**格式化日志框顶部分隔符，装饰于所有日志信息的顶部*/
    val top : String
    /**格式化日志框中间，装饰于分隔额外信息与日志正文内容*/
    val middle : String
    /**格式化的日志框底部边框分隔符，装饰于日志正文的底部*/
    val bottom : String
    /**格式化的日志框左侧分隔符，装饰于日志信息的左侧，包括额外信息与日志正文*/
    val left : String
}