package com.yupfeg.logger.converter.json

import android.os.Bundle
import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.ext.logw
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

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
 * 判断 [value] 是否为基本类型
 */
fun isPrimitiveType(value: Any?) = when(value){
    is Boolean -> true
    is String  -> true
    is Int     -> true
    is Float   -> true
    is Double  -> true
    else       -> false
}

/**
 * [String]拓展函数，解析Json字符串转化为List<T>格式
 * @param jsonConverter json字符串的解析类
 * */
@Suppress("unused")
internal inline fun <reified T> String.parseJsonStringToList(jsonConverter: JsonConverter) : List<T>{
    return jsonConverter.fromJson(this, ListParameterizedTypeImpl(T::class.java))
}

/**GSON使用List泛型解析Type信息*/
internal class ListParameterizedTypeImpl(private val clazz: Class<*>) : ParameterizedType {
    /** List<T>里的List,所以返回值是List.class*/
    override fun getRawType(): Type {
        return List::class.java
    }
    /**表示此类型是其成员之一的类型，用于这个泛型中包含了内部类的情况,一般返回null*/
    override fun getOwnerType(): Type? {
        return null
    }
    /**返回实际类型组成的数组，即List<T>中的T，返回值为new []{T.class}*/
    override fun getActualTypeArguments(): Array<Type> {
        return arrayOf(clazz)
    }
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
    return JSONObject().also {jsonObject->
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
    return JSONArray().also {jsonArray->
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