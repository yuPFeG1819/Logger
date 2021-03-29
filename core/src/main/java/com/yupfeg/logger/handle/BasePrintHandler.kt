package com.yupfeg.logger.handle

import com.yupfeg.logger.handle.config.PrintHandlerConfig

/**
 * 处理日志输出的处理Handler链基类
 * * 采用责任链模式，高内聚，低耦合，拓展性强，可动态增加处理流程，并可动态调整处理顺序
 * * 相比策略模式，采用责任链模式，在外部添加PrintHandler类时，不需要修改Logger中对于Handler类的处理逻辑，
 * 只需要依照责任链依次向下执行
 * @author yuPFeG
 * @date 2021/01/04
 */
abstract class BasePrintHandler {

    /**下一个处理节点*/
    private var mNextChain : BasePrintHandler?= null

    /**
     * 处理日志输出内容
     * @param content
     * @param handleConfig
     */
    open fun handlePrintContent(content : Any, handleConfig : PrintHandlerConfig){
        if (!handleContent(content,handleConfig)){
            mNextChain?.handlePrintContent(content, handleConfig)
        }
    }

    /**
     * 设置下一个处理节点
     * @param chain
     * */
    fun setNextChain(chain: BasePrintHandler){
        this.mNextChain = chain
    }

    /**
     * 子类实现的具体处理逻辑
     * @param content 日志内容
     * @param handleConfig 处理的配置类
     * @return 是否能够处理该输出内容，true-则结束任务链，输出日志，false-表示当前类无法处理，继续调用下一个节点处理类
     * */
    abstract fun handleContent(content : Any, handleConfig: PrintHandlerConfig) : Boolean

}
