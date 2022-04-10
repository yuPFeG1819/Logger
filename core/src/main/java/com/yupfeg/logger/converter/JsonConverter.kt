package com.yupfeg.logger.converter

import android.os.Bundle
import com.yupfeg.logger.ext.logw
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
    fun <T> fromJson(json: String, type: Type): T

    /**
     * 将对象序列化成字符串
     * @param data
     * @return
     */
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
 * 判断 [value] 是否为基本数据类型
 * - 通常用于泛型类型的判断，非基本数据类型需要进行特殊处理
 */
fun isPrimitiveType(value: Any?) = when(value){
    is Boolean -> true
    is Char    -> true
    is String  -> true
    is Byte    -> true
    is Short   -> true
    is Int     -> true
    is Long    -> true
    is Float   -> true
    is Double  -> true
    else       -> false
}

/**
 * [Bundle]拓展函数，解析 bundle ，并存储到 JSONObject
 * * 仅用于Logger日志输出使用
 * @param jsonConverter json解析类
 */
internal fun Bundle.parseToJSONObject(jsonConverter: JsonConverter) : JSONObject {
    val bundle = this
    return JSONObject().also { jsonObject ->
        bundle.keySet().forEach {
            val value = bundle.get(it)
            value?:return@forEach
            val isPrimitiveType = isPrimitiveType(this)
            try {
                if (isPrimitiveType) {
                    jsonObject.put(it, bundle.get(it))
                } else {
                    jsonObject.put(it, JSONObject(jsonConverter.toJson(this)))
                }
            } catch (e: JSONException) {
                logw("Invalid Log Bundle content Json")
            }
        }
    }
}

/**
 * [Map]的拓展函数，解析 map 为 JSONObject
 * * 仅用于Logger日志输出使用
 * @param jsonConverter json解析类
 */
internal fun Map<*, *>.parseToJSONObject(jsonConverter: JsonConverter): JSONObject {
    val originMap = this
    return JSONObject().also { jsonObject->
        val firstValue = originMap.values.firstOrNull()
        val isPrimitiveType = isPrimitiveType(firstValue)
        originMap.keys.map {item->
            item?:return@map
            try {
                if (isPrimitiveType) {
                    jsonObject.put(item.toString(), originMap[item])
                } else {
                    jsonObject.put(
                        item.toString(),
                        JSONObject(jsonConverter.toJson(originMap[item] ?: "{}"))
                    )
                }
            } catch (e: JSONException) {
                logw("Invalid Log Map content Json")
            }
        }
    }
}

/**
 * [Collection]拓展函数，解析 collection 转化为 [JSONArray]
 * * 仅用于Logger日志输出使用
 * @param jsonConverter 日志解析类
 */
internal fun Collection<*>.parseToJSONArray(jsonConverter: JsonConverter): JSONArray {
    return JSONArray().also { jsonArray->
        this.map { item ->
            item ?: return@map
            val objStr = jsonConverter.toJson(item)
            objStr.run<String, Unit> {
                try {
                    jsonArray.put(JSONObject(this))
                } catch (e: JSONException) {
                    logw("Invalid Log Collection content Json")
                }
            }
        }
    }
}