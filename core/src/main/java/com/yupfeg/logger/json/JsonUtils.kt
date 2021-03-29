package com.yupfeg.baselibrary.tools.json

import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * json解析的工具类
 * @author yuPFeG
 * @date 2020/01/04
 */
object JsonUtils {
    private val gson: Gson by lazy(LazyThreadSafetyMode.SYNCHRONIZED){ Gson() }

    /**
     * 将json字符串转换成type类型的对象
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    @JvmStatic
    fun <T> fromJson(json: String, type: Type): T {
        return gson.fromJson(json, type)
    }

    /**
     * 将对象序列化成字符串对象
     * @param data
     * @return
     */
    @JvmStatic
    fun toJson(data: Any): String {
        return gson.toJson(data)
    }

}