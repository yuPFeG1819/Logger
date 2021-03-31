package com.yupfeg.logger.converter

import java.lang.reflect.Type

/**
 * 日志内容json解析器接口声明
 * @author yuPFeG
 * @date 2021/03/31
 */
interface JsonConverter {
    /**
     * 将字符串转换成type类型的对象
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    fun <T> fromJson(json: String, type: Type): T

    /**
     * 将对象序列化成字符串对象
     * @param data
     * @return
     */
    fun toJson(data: Any): String
}