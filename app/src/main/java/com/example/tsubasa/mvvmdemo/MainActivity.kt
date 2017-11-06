package com.example.tsubasa.mvvmdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.tsubasa.core.ui.callback.STATUS_ERROR
import com.tsubasa.core.ui.component.statuslayout.BaseStatusItemUIComponent
import com.tsubasa.core.ui.component.statuslayout.StatusUIComponent
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val statusUIComponent = StatusUIComponent()
        statusUIComponent.setContentView(this)
        statusUIComponent.loadingUI = EmptyUI()
        statusUIComponent.end(STATUS_ERROR, "正在加载中喔")
    }
}

class EmptyUI : BaseStatusItemUIComponent() {

    override fun AnkoContext<Any>.createContainer(): FrameLayout = frameLayout()

    override fun createContent(parent: ViewGroup) {
        (parent as FrameLayout).apply {
            textView {
                id = VIEW_ID_TV_MSG
                text = "hahahaha"
            }
        }
    }

    override fun showLayout(isShow: Boolean) {

    }

    override fun showMsg(msg: CharSequence?) {
        container?.find<TextView>(VIEW_ID_TV_MSG)?.text = msg
    }

}