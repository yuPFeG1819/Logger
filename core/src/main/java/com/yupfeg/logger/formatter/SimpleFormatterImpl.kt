package com.yupfeg.logger.formatter

/**
 * 默认日志格式化类，仅提供顶部双实线间隔，左侧只添加缩进，方便复制网络接口返回json
 * @author yuPFeG
 * @date 2021/03/15
 */
object SimpleFormatterImpl : Formatter {
    private const val TOP_LEFT_CORNER    = '╔'
    private const val BOTTOM_LEFT_CORNER = '╚'
    private const val MIDDLE_CORNER      = '╟'
    private const val DOUBLE_DIVIDER     = "═════════════════════════════════════════════════"

    override val top = Formatter.BR + TOP_LEFT_CORNER +
            DOUBLE_DIVIDER + DOUBLE_DIVIDER + Formatter.BR

    override val middle = Formatter.BR + MIDDLE_CORNER + Formatter.BR

    override val bottom = Formatter.BR + BOTTOM_LEFT_CORNER + Formatter.BR

    //只显示两格缩进
    override val left = Formatter.BLANK+ Formatter.BLANK
}