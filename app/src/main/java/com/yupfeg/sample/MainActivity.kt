package com.yupfeg.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yupfeg.logger.ext.logd
import com.yupfeg.logger.ext.logw
import java.lang.NullPointerException

/**
 *
 * @author yuPFeG
 * @date 2021/02/26
 */
class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            throw NullPointerException("ddd")
        }catch (e : NullPointerException){
            logw(e)
        }
        logd("${this.javaClass.name} onCreate")
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