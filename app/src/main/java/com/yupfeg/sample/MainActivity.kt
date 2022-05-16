package com.yupfeg.sample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.yupfeg.logger.Logger
import com.yupfeg.logger.ext.loggd
import com.yupfeg.logger.ext.loggi

/**
 *
 * @author yuPFeG
 * @date 2021/02/26
 */
class MainActivity : AppCompatActivity(R.layout.activity_main){

    private data class TestMan(val name : String,val age : Int = 0)

    private val log : Logger = Logger.create("MainActivity")

    private data class CustomDataBean(
        val newData : String,
        val newList : List<String>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loggd("${this.javaClass.name} onCreate")
        findViewById<Button>(R.id.btn_test_print_log).setOnClickListener {
            performPrintTestLog()
        }
    }

    private fun performPrintTestLog(){
        try {
            throw NullPointerException("test null ddd")
        }catch (e : NullPointerException){
            log.w(e)
        }
        log.d(List(22){
            TestMan("name ${it * 2}", age = it + 1)
        })
        log.v(List(30){
            it * 3
        })

        log.e(mutableMapOf("11" to 22,"22" to 33,"33" to 121))

        val set = HashSet<CustomDataBean>()
        set.add(CustomDataBean(newData = "11", newList = mutableListOf("111item1","111item2","111item3")))
        set.add(CustomDataBean(newData = "22", newList = mutableListOf("222item1","222item2","222item3")))
        loggi(set)

        intent?.also {
            loggi(it)
        }

        loggd(bundleOf("test" to "newTest","test2" to "newTest2"))
    }

    override fun onStart() {
        super.onStart()
        loggd("${this.javaClass.name} onStart")
    }

    override fun onResume() {
        super.onResume()
        loggd("${this.javaClass.name} onResume")
    }

    override fun onPause() {
        super.onPause()
        loggd("${this.javaClass.name} onPause")
    }

    override fun onStop() {
        super.onStop()
        loggd("${this.javaClass.name} onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        loggd("${this.javaClass.name} onDestroy")
    }


}