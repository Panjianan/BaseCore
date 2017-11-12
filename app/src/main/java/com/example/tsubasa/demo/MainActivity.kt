package com.example.tsubasa.demo

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.LinearLayout
import com.tsubasa.core.common.base.otherwise
import com.tsubasa.core.common.base.yes
import com.tsubasa.core.model.Status
import com.tsubasa.core.model.StatusResponse
import com.tsubasa.core.ui.component.BaseComponent
import com.tsubasa.core.ui.component.StandardListComponent
import com.tsubasa.core.ui.component.recyclerview.adapter.createAdapter
import com.tsubasa.core.util.lifecycle.bind
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

    private lateinit var standardListComponent: StandardListComponent<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        standardListComponent = StandardListComponent(swipeViewStyle = {
            //            isEnableAutoLoadmore = false
        }) {
            createAdapter(initUI = { CustomMainListItemComponent() }, initObserver = { this })
        }
        standardListComponent.apply {
            setContentView(this@MainActivity)
            onInit = {
                fakeLoadData(initStatus)
            }
            onRefresh = {
                fakeLoadData(refreshStatus)
            }
            onLoadMore = {
                fakeLoadData(loadMoreStatus, false)
            }
        }

        standardListComponent.onInit?.invoke()
    }

    private fun fakeLoadData(status: MutableLiveData<StatusResponse<*>>?, isRefresh: Boolean = true) {
        status?.value = StatusResponse<Any>(Status.STATUS_LOADING, "加载中")
        contentView?.postDelayed({
            isRefresh.yes {
                status?.value = StatusResponse<Any>(Status.STATUS_SUCCESS, "数据为空")
                val dataList = Array(30) {
                    it.toString()
                }.toList()
                standardListComponent.getAdapter()?.data = dataList
            }.otherwise {
                val size = standardListComponent.getAdapter()?.data?.size ?: 0
                val dataList = Array((size > 100).yes { 0 }.otherwise { 30 }) {
                    size.plus(it).toString()
                }.toList()
                status?.value = (dataList.isNotEmpty()).yes { StatusResponse<Any>(Status.STATUS_SUCCESS) }.otherwise { StatusResponse(Status.STATUS_EMPTY, "没有更多了") }
                standardListComponent.getAdapter()?.addDatas(dataList)
            }
        }, 2000)
    }
}

class CustomMainListItemComponent : BaseComponent<LinearLayout>(), Observer<String> {

    val data: MutableLiveData<String> = MutableLiveData()

    override fun onChanged(t: String?) {
        data.value = t
    }

    override fun createContainer(context: AnkoContext<Any>): LinearLayout = context.linearLayout().apply {
        layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
    }

    override fun createContent(parent: LinearLayout) {
        parent.apply {
            backgroundColor = Color.BLUE
            textView {
                gravity = Gravity.CENTER
                textColor = Color.WHITE
                data.bind(owner) {
                    text = data.value
                }
            }.apply {
                layoutParams = LinearLayout.LayoutParams(matchParent, dip(30))
            }
        }
    }
}