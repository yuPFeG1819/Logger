package com.yupfeg.logger.converter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

/**
 * gson的日志内容解析类
 * - 用于其他对象内容类型的解析，如List、Map等
 * @author yuPFeG
 * @date 2021/03/31
 */
class GsonConverter : JsonConverter{

    private val gson: Gson by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        GsonBuilder().setLenient().create()
    }

    override fun <T> fromJson(json: String, type: Type) : T = gson.fromJson(json, type)

    override fun toJson(data: Any): String = gson.toJson(data)

}