package com.tsubasa.core.ui.component

import android.arch.lifecycle.LifecycleOwner
import android.support.v7.widget.RecyclerView
import com.tsubasa.core.common.base.yes
import com.tsubasa.core.model.Status
import com.tsubasa.core.model.Resource
import com.tsubasa.core.ui.component.recyclerview.RecyclerViewComponent
import com.tsubasa.core.ui.component.recyclerview.adapter.ComponentAdapter
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

    /**
     * 初始化加载数据的操作
     */
    var onInit: (() -> Unit)?
        set(value) {
            contentUI?.onInit = value
        }
        get() = contentUI?.onInit

    override fun bindData(lifecycleOwner: LifecycleOwner) {
        super.bindData(lifecycleOwner)
        initStatus.bind(lifecycleOwner) {
            // 初始化加载的时候不允许上拉和下拉
            container?.isEnableRefresh = it?.status != Status.STATUS_LOADING
            container?.isEnableLoadmore = it?.status != Status.STATUS_LOADING
            // swipe页保持状态同步
            contentUI?.status?.value = it
        }
        refreshStatus.bind(lifecycleOwner) {
            // 上拉刷新时，出现错误或者空数据，同步到状态布局页面
            (it?.status == Status.STATUS_EMPTY).or((it?.status == Status.STATUS_ERROR).and(getAdapter()?.size?.value == 0)).yes {
                contentUI?.status?.value = it
            }
        }
        getAdapter()?.size?.bind(lifecycleOwner) {
            // 列表为空时显示空布局
            (it == 0).yes {
                contentUI?.status?.value = Resource<DATA>(Status.STATUS_EMPTY, contentUI?.emptyUI?.msg?.value)
            }
        }
    }

    fun getAdapter(): ComponentAdapter<DATA, *>? {
        @Suppress("UNCHECKED_CAST")
        return contentUI?.contentUI?.container?.adapter as? ComponentAdapter<DATA, *>
    }
}