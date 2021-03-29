package com.yupfeg.logger

/**
 * 日志打印等级
 * @author yuPFeG
 * @date 2020/12/11
 */
enum class LoggerLevel {
    VERBOSE {
        override val value: Int
            get() = 0
    },
    DEBUG {
        override val value: Int
            get() = 1
    },
    INFO {
        override val value: Int
            get() = 2
    },
    WARN {
        override val value: Int
            get() = 3
    },
    ERROR {
        override val value: Int
            get() = 4
    };

    abstract val value: Int
}