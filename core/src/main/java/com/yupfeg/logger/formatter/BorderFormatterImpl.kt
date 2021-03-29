package com.yupfeg.logger.formatter


/**
 * 提供双层线的格式化日志输出的格式帮助类
 * @author: yuPFeG
 * @date: 2020/12/31
 */
object BorderFormatterImpl : Formatter {

    //---------Drawing tool logBox---------------

    private const val TOP_LEFT_CORNER    = '╔'
    private const val BOTTOM_LEFT_CORNER = '╚'
    private const val MIDDLE_CORNER      = '╟'
    private const val DOUBLE_DIVIDER     = "═════════════════════════════════════════════════"
    private const val SINGLE_DIVIDER     = "─────────────────────────────────────────────────"

    private val TOP_BORDER      = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private val BOTTOM_BORDER   = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private val MIDDLE_BORDER   = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER



    override fun top()          = Formatter.BR + TOP_BORDER + Formatter.BR

    override fun middle()       = Formatter.BR + MIDDLE_BORDER + Formatter.BR

    override fun bottom()       = Formatter.BR + BOTTOM_BORDER + Formatter.BR

    //IFormatter.HORIZONTAL_DOUBLE_LINE
    override fun leftSplitter() = Formatter.BLANK + Formatter.BLANK
}