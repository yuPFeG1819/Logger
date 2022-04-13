package com.yupfeg.logger.converter

import android.os.Bundle
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
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
    @Throws(RuntimeException::class)
    fun <T> fromJson(json: String, type: Type): T

    /**
     * 将对象序列化成字符串
     * @param data
     * @return
     */
    @Throws(RuntimeException::class)
    fun toJson(data: Any): String
}

/**
 * json字符串每层的缩进数
 */
private const val JSON_INDENT = 2

/**
 * [JSONObject]拓展函数，格式化为json对象字符串
 * @param spaces json字符串中每个嵌套层级缩进格数，默认缩进2格
 * */
fun JSONObject.formatJSONString(spaces : Int = JSON_INDENT) : String = this.toString(spaces)

/**
 * [JSONArray]拓展函数，格式化为json数组字符串
 * @param spaces json字符串中每个嵌套层级缩进格数，默认缩进2格
 * */
fun JSONArray.formatJSONString(spaces: Int = JSON_INDENT) : String = this.toString(spaces)


/**
 * 判断 [value] 是否为基本数据类型，可以直接使用`toString`转化为字符串，不需要特殊处理
 * - 通常用于泛型类型的判断，非基本数据类型需要进行特殊处理
 * @param value 集合、键值对，内部的值
 */
fun isPrimitiveTypeValue(value: Any?) = when(value){
    is Boolean -> true
    is Char    -> true
    is Byte    -> true
    is Short   -> true
    is Int     -> true
    is Long    -> true
    is Float   -> true
    is Double  -> true
    is String  -> true
    else       -> false
}

/**
 * [Bundle]拓展函数，解析 [Bundle] ，并存储到 [JSONObject]
 * * 仅用于Logger日志输出使用
 * @param jsonConverter json解析类
 */
@Throws(JSONException::class,RuntimeException::class)
internal fun Bundle.parseToJSONObject(jsonConverter: JsonConverter) : JSONObject {
    val bundle = this
    return JSONObject().also { jsonObject ->
        bundle.keySet().forEach {
            val value = bundle.get(it)
            value?:return@forEach
            val isPrimitiveType = isPrimitiveTypeValue(value)
            if (isPrimitiveType) {
                jsonObject.put(it, value)
            } else {
                jsonObject.put(it, JSONObject(jsonConverter.toJson(this)))
            }
        }
    }
}
