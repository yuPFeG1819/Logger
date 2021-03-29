package com.yupfeg.logger.formatter

/**
 *
 * @author yuPFeG
 * @date 2021/03/15
 */
object SimpleFormatterImpl : Formatter {
    private const val TOP_LEFT_CORNER    = '╔'
    private const val BOTTOM_LEFT_CORNER = '╚'
    private const val MIDDLE_CORNER      = '╟'
    private const val DOUBLE_DIVIDER     = "═════════════════════════════════════════════════"

    override fun top() = Formatter.BR + TOP_LEFT_CORNER +
            DOUBLE_DIVIDER + DOUBLE_DIVIDER + Formatter.BR

    override fun middle() = Formatter.BR + MIDDLE_CORNER + Formatter.BR

    override fun bottom() = Formatter.BR + BOTTOM_LEFT_CORNER + Formatter.BR

    override fun leftSplitter() = Formatter.BLANK+ Formatter.BLANK
}