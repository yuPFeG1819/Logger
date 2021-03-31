package com.yupfeg.logger.converter

import com.yupfeg.logger.converter.json.GsonUtils
import java.lang.reflect.Type

/**
 * gson的日志内容解析类
 * @author yuPFeG
 * @date 2021/03/31
 */
class GsonConverter : JsonConverter{

    override fun <T> fromJson(json: String, type: Type) : T = GsonUtils.fromJson(json, type)

    override fun toJson(data: Any): String = GsonUtils.toJson(data)

}