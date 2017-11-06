package com.example.tsubasa.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tsubasa.core.ui.callback.STATUS_ERROR
import com.tsubasa.core.ui.callback.STATUS_LOADING
import com.tsubasa.core.ui.component.statuslayout.StatusUIComponent
import org.jetbrains.anko.setContentView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val statusUIComponent = StatusUIComponent()
        statusUIComponent.setContentView(this)
        statusUIComponent.start()
        statusUIComponent.container?.postDelayed({
            statusUIComponent.end(STATUS_LOADING, "正在加载中喔")
            statusUIComponent.container?.postDelayed({
                statusUIComponent.end(STATUS_ERROR, "加载错误")
            }, 4000)
        }, 4000)

    }
}