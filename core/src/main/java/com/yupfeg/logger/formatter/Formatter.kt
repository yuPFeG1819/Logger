package com.yupfeg.logger.formatter

/**
 * 格式化日志接口，便于 Printer 进行打印。
 * 每一个 Printer 包含一个自身的 Formatter
 * @author: yuPFeG
 */
interface Formatter {
    companion object{
        /**换行符*/
        val BR                               = System.getProperty("line.separator")!!
        /**垂直方向的双竖线*/
        const val HORIZONTAL_DOUBLE_LINE     = "║ "
        /**空格*/
        const val BLANK                      = " "
        /**逗号分隔符*/
        const val COMMA                      = ","
    }

    /**格式化日志框顶部分隔符*/
    fun top():String
    /**格式化日志框中间，用于分隔额外信息与日志内容*/
    fun middle():String
    /**格式化的日志框底部边框分隔符*/
    fun bottom():String
    /**格式化的日志框左侧分隔符*/
    fun leftSplitter():String
}