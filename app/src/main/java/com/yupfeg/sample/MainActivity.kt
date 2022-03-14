package com.yupfeg.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.yupfeg.logger.ext.logd
import com.yupfeg.logger.ext.logi
import com.yupfeg.logger.ext.logw
import java.lang.NullPointerException

/**
 *
 * @author yuPFeG
 * @date 2021/02/26
 */
class MainActivity : AppCompatActivity(){

    private data class TestMan(val name : String,val age : Int = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            throw NullPointerException("ddd")
        }catch (e : NullPointerException){
            logw(e)
        }
        logd("${this.javaClass.name} onCreate")
        logd(List(22){
            TestMan("name ${it * 2}", age = it + 1)
        })
        logd(List(30){
            it * 3
        })

        logd(mutableMapOf("11" to 22,"22" to 33,"33" to 121))

        intent?.also {
            logi(it)
        }

        logd(bundleOf("test" to "newTest"))
    }

    override fun onStart() {
        super.onStart()
        logd("${this.javaClass.name} onStart")
    }

    override fun onResume() {
        super.onResume()
        logd("${this.javaClass.name} onResume")
    }

    override fun onPause() {
        super.onPause()
        logd("${this.javaClass.name} onPause")
    }

    override fun onStop() {
        super.onStop()
        logd("${this.javaClass.name} onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        logd("${this.javaClass.name} onDestroy")
    }


}