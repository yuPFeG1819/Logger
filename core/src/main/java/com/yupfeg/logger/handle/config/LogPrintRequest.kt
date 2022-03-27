package com.yupfeg.logger.handle.config

import com.yupfeg.logger.LoggerLevel
import com.yupfeg.logger.converter.JsonConverter
import com.yupfeg.logger.printer.BaseLogPrinter

/**
 * 日志内容输出请求
 * - 提供给处理链上所有内容处理类的同一个不可变对象
 * - 请求对象只允许修改
 * @author yuPFeG
 * @date 2021/01/22
 */
class LogPrintRequest internal constructor(
    /**当前日志可能的输出目标*/
    val printers : Set<BaseLogPrinter>,
    /**当前日志内容上，包含的顶部信息*/
    val logHeaders : List<String>?,
    /**是否在日志中打印当前线程信息*/
    val isPrintThreadInfo : Boolean = true,
    /**是否在日志中打印当前打印日志的位置*/
    val isPrintClassInfo : Boolean = true,
    /**json解析器*/
    val jsonConverter: JsonConverter,
    /**是否存在多个输出器*/
    val isMultiPrinter : Boolean = false
){

    companion object{
        /**默认最大的日志对象缓存大小*/
        private const val DEF_MAX_POOL_SIZE = 30

        /**最大缓存数量*/
        var maxPoolSize : Int = DEF_MAX_POOL_SIZE

        /**维护一个Head，单链表构成的LIFO栈结构*/
        private var mPools : LogPrintRequest? = null
        private var mPoolSize : Int = 0

        private val mLock = Any()

        /**
         * 尝试获取日志请求对象，避免频繁分配新对象，如果没有缓存则返回null
         * */
        internal fun obtain() : LogPrintRequest?{
            return synchronized(mLock){
                var request : LogPrintRequest? = null
                mPools?.also {
                    request = it
                    mPools = it.next
                    it.next = null
                    mPoolSize--
                }
                request
            }
        }

        /**
         * 回收已使用完成的日志请求对象
         * @param request 已完成的请求对象
         * @return true-表示已成功缓存
         * */
        internal fun release(request: LogPrintRequest) : Boolean{
            request.reset()
            synchronized(mLock){
                if (mPoolSize < maxPoolSize){
                    request.next = mPools
                    //将回收的对象作为Head
                    mPools = request
                    mPoolSize ++
                    return true
                }
                return false
            }
        }

    }

    /**
     * 构建日志请求的单链表结构，下一个链表节点的指针
     * */
    internal var next : LogPrintRequest? = null
    /**
     * 原始日志内容，外部发起请求的内容对象
     * */
    val logContent : Any
        get() = requireNotNull(mOriginContent)

    private var mOriginContent : Any? = null

    /**日志输出的等级*/
    var logLevel : LoggerLevel = LoggerLevel.VERBOSE
        internal set

    /**日志输出的标签*/
    val logTag : String
        get() = requireNotNull(mOriginLogTag)

    private var mOriginLogTag : String? = null

    internal fun reset(){
        logLevel = LoggerLevel.VERBOSE
        mOriginContent = null
        mOriginLogTag = null
    }

    internal fun setNewContent(level : LoggerLevel, tag : String, message : Any){
        logLevel = level
        mOriginContent = message
        mOriginLogTag = tag
    }
}