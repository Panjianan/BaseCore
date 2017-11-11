package com.tsubasa.core.ui.component

import android.arch.lifecycle.LifecycleOwner
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.tsubasa.core.common.base.yes
import com.tsubasa.core.ui.callback.Status
import com.tsubasa.core.ui.component.recyclerview.RecyclerViewComponent
import com.tsubasa.core.ui.component.statuslayout.StatusUIComponent
import com.tsubasa.core.ui.component.swipetoload.SwipeToLoadComponent
import com.tsubasa.core.ui.widget.swipetoload.SwipeToLoadLayout
import com.tsubasa.core.util.lifecycle.bind

/**
 * 标准的列表界面
 * 三层
 * 最外层 状态布局页面
 * 中间层 下拉刷新上拉加载更多页面
 * 最里层 RecyclerView
 * Created by tsubasa on 2017/11/11.
 */
class StandardListComponent<DATA>(swipeViewStyle: (SwipeToLoadLayout.() -> Unit)? = null, block: RecyclerView.() -> RecyclerView.Adapter<*>) : SwipeToLoadComponent<StatusUIComponent<RecyclerViewComponent>>(swipeViewStyle) {

    init {
        contentUI = StatusUIComponent()
        contentUI?.contentUI = RecyclerViewComponent(block)
    }

    var onInit: (() -> Unit)?
        set(value) {
            contentUI?.onInit = value
        }
        get() = contentUI?.onInit

    override fun bindData(lifecycleOwner: LifecycleOwner) {
        super.bindData(lifecycleOwner)
        initStatus.bind(lifecycleOwner) {
            container?.isEnableRefresh = it?.status != Status.STATUS_LOADING
            container?.isEnableLoadmore = it?.status != Status.STATUS_LOADING
            contentUI?.status?.value = it
        }
        refreshStatus.bind(lifecycleOwner) {
            (it?.status == Status.STATUS_EMPTY).or(it?.status == Status.STATUS_ERROR).yes {
                contentUI?.status?.value = it
            }
        }
    }

    fun getAdapter(): BaseQuickAdapter<DATA, *>? {
        @Suppress("UNCHECKED_CAST")
        return contentUI?.contentUI?.container?.adapter as? BaseQuickAdapter<DATA, *>
    }
}