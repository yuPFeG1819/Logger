package com.yupfeg.logger.formatter


/**
 * 提供双层线的格式化日志输出的格式帮助类
 * - 形成一个完整的双层线日志框
 * @author: yuPFeG
 * @date: 2020/12/31
 */
object BorderFormatterImpl : Formatter {

    //---------Drawing tool logBox---------------

    private const val TOP_LEFT_CORNER    = "╔"
    private const val BOTTOM_LEFT_CORNER = "╚"
    private const val MIDDLE_CORNER      = "╟"
    private const val DOUBLE_DIVIDER     = "═════════════════════════════════════════════════"
    private const val SINGLE_DIVIDER     = "─────────────────────────────────────────────────"

    private const val TOP_BORDER      = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private const val BOTTOM_BORDER   = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private const val MIDDLE_BORDER   = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER

    override val top = Formatter.BR + TOP_BORDER + Formatter.BR

    override val middle = Formatter.BR + MIDDLE_BORDER + Formatter.BR

    override val bottom = Formatter.BR + BOTTOM_BORDER + Formatter.BR

    override val left = Formatter.HORIZONTAL_DOUBLE_LINE
}