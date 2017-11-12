package com.tsubasa.core.ui.component.swipetoload

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import com.tsubasa.core.model.Status
import com.tsubasa.core.model.StatusResponse
import com.tsubasa.core.ui.component.BaseComponent
import com.tsubasa.core.ui.widget.swipetoload.SwipeToLoadLayout
import com.tsubasa.core.ui.widget.swipetoload.swipeToLoadLayout
import com.tsubasa.core.util.lifecycle.bind
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent

/***
 * <br> Project BaseCore
 * <br> Package com.tsubasa.core.ui.component.swipetoload
 * <br> Description 下拉刷新的组件
 * <br> Version 1.0
 * <br> Author Tsubasa
 * <br> Creation 2017/11/10 17:33
 * <br> Mender Tsubasa
 * <br> Modification 2017/11/10 17:33
 * <br> Copyright Copyright © 2012 - 2017 Tsubasa.All Rights Reserved.
 */
open class SwipeToLoadComponent<ContentUI : BaseComponent<*>>(private val viewStyle: (SwipeToLoadLayout.() -> Unit)? = null) : BaseComponent<SwipeToLoadLayout>() {

    val initStatus: MutableLiveData<StatusResponse<*>> = MutableLiveData()
    val refreshStatus: MutableLiveData<StatusResponse<*>> = MutableLiveData()
    val loadMoreStatus: MutableLiveData<StatusResponse<*>> = MutableLiveData()
    var onRefresh: (() -> Unit)? = null
        set(value) {
            container?.setOnRefreshListener { value?.invoke() }
            field = value
        }
    var onLoadMore: (() -> Unit)? = null
        set(value) {
            container?.setOnLoadmoreListener { value?.invoke() }
            field = value
        }

    /**
     * 内容布局组件
     */
    var contentUI: ContentUI? = null
        set(value) {
            container?.let { parent ->
                field?.container?.let { parent.removeView(it) }
                value?.createComponent(parent)
            }
            field = value
        }

    override fun createContainer(context: AnkoContext<Any>): SwipeToLoadLayout = context.swipeToLoadLayout().apply {
        viewStyle?.invoke(this)
    }

    override fun createContent(parent: SwipeToLoadLayout) {
        parent.apply {
            contentUI?.createComponent(this)?.lparams(matchParent, matchParent)
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun bindData(lifecycleOwner: LifecycleOwner) {
        initStatus.bind(lifecycleOwner) {
            // 初始化加载的时候结束上拉和下拉的动画
            container?.finishRefresh(true)
            container?.finishLoadmore(true)
        }
        refreshStatus.bind(lifecycleOwner) {
            it?.let { onRefreshStateChange(it) }
        }
        loadMoreStatus.bind(lifecycleOwner) {
            it?.let { onLoadStateChange(it) }
        }
        container?.setOnRefreshListener { onRefresh?.invoke() }
        container?.setOnRefreshListener { onLoadMore?.invoke() }
    }

    private fun onRefreshStateChange(status: StatusResponse<*>) {
        when (status.status) {
            Status.STATUS_LOADING -> container?.autoRefresh()
            else -> container?.finishRefresh()
        }

        when (status.status) {
            Status.STATUS_SUCCESS -> {
                container?.isLoadmoreFinished = false
            }
            else -> {
                container?.isLoadmoreFinished = true
            }
        }
    }

    private fun onLoadStateChange(status: StatusResponse<*>) {
        when (status.status) {
            Status.STATUS_LOADING -> container?.autoLoadmore()
            Status.STATUS_ERROR -> container?.finishLoadmore(false)
            else -> container?.finishLoadmore(true)
        }

        when (status.status) {
            Status.STATUS_EMPTY, Status.STATUS_SUCCESS_NO_MORE -> {
                container?.isLoadmoreFinished = true
            }
            Status.STATUS_LOADING -> {
                // do noting
            }
            else -> {
                container?.isLoadmoreFinished = false
            }
        }
    }
}