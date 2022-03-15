package com.yupfeg.logger.pool

import com.yupfeg.logger.handle.config.LogPrintRequest


/**
 * 提供发起日志输出请求的对象缓存池，同时支持创建功能
 * @author yuPFeG
 * @date 2022/03/15
 */
class RequestPool(maxPoolSize : Int) {
    private val mPool : Array<LogPrintRequest?> = arrayOfNulls(maxPoolSize)
    private var mPoolSize : Int = 0

    private val mLock = Any()

    /**
     * 尝试获取日志请求对象
     * */
    fun acquire() : LogPrintRequest?{
        return synchronized(mLock){
            if (mPoolSize > 0){
                val lastIndex = mPoolSize - 1
                val request = mPool[lastIndex]
                mPool[lastIndex] = null
                mPoolSize--
                request
            }else{
                null
            }
        }
    }

    /**
     * 回收已使用完成的日志请求对象
     * @param request 已完成的请求对象
     * */
    fun release(request: LogPrintRequest) : Boolean{
        synchronized(mLock){
            request.reset()
            if (mPoolSize < mPool.size){
                mPool[mPoolSize] = request
                mPoolSize ++
                return true
            }
            return false
        }
    }

}