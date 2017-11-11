package com.tsubasa.core.ui.component.swipetoload

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.tsubasa.core.ui.callback.Status
import com.tsubasa.core.ui.callback.StatusCallback
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
enum class Action {
    Init,
    Refresh,
    LoadMore
}

data class SwipeStatus(val status: Status, val action: Action, var message: CharSequence? = null)

class SwipeToLoadComponent<ContentUI : BaseComponent<*>> : BaseComponent<SwipeToLoadLayout>(), StatusCallback<SwipeStatus> {

    val autoLoadMore: MutableLiveData<Boolean> = MutableLiveData()
    val initStatus: MutableLiveData<Status> = MutableLiveData()
    val refreshStatus: MutableLiveData<Status> = MutableLiveData()
    val loadMoreStatus: MutableLiveData<Status> = MutableLiveData()
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

    override val status: MutableLiveData<SwipeStatus> = MediatorLiveData<SwipeStatus>().apply {
        addSource(initStatus) {
            value = refreshStatus.value?.let { SwipeStatus(it, Action.Init) }
        }
        addSource(refreshStatus) {
            value = refreshStatus.value?.let { SwipeStatus(it, Action.Refresh) }
        }
        addSource(loadMoreStatus) {
            value = loadMoreStatus.value?.let { SwipeStatus(it, Action.LoadMore) }
        }
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

    override fun createContainer(context: AnkoContext<Any>): SwipeToLoadLayout = context.swipeToLoadLayout()

    override fun createContent(parent: SwipeToLoadLayout) {
        parent.apply {
            contentUI?.createComponent(this)?.lparams(matchParent, matchParent)
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun bindData(lifecycleOwner: LifecycleOwner) {
        status.bind(lifecycleOwner) {
            when (it?.action) {
                Action.Refresh -> onRefreshStateChange(it.status)
                Action.LoadMore -> onLoadStateChange(it.status)
                else -> {
                }
            }
        }
        autoLoadMore.bind(lifecycleOwner) {
            container?.isEnableAutoLoadmore = it ?: false
        }
        container?.setOnRefreshListener { onRefresh?.invoke() }
        container?.setOnRefreshListener { onLoadMore?.invoke() }
    }

    private fun onRefreshStateChange(status: Status) {
        when (status) {
            Status.STATUS_LOADING -> container?.autoRefresh()
            else -> container?.finishRefresh()
        }

        when (status) {
            Status.STATUS_SUCCESS -> {
                container?.isLoadmoreFinished = false
                container?.isEnableAutoLoadmore = autoLoadMore.value ?: false
            }
            else -> {
                container?.isLoadmoreFinished = true
                container?.isEnableAutoLoadmore = false
            }
        }
    }

    private fun onLoadStateChange(status: Status) {
        when (status) {
            Status.STATUS_LOADING -> container?.autoLoadmore()
            Status.STATUS_ERROR -> container?.finishLoadmore(false)
            else -> container?.finishLoadmore(true)
        }

        when (status) {
            Status.STATUS_EMPTY, Status.STATUS_SUCCESS_NO_MORE -> {
                container?.isLoadmoreFinished = true
                container?.isEnableAutoLoadmore = false
            }
            Status.STATUS_LOADING -> {
            }
            else -> {
                container?.isLoadmoreFinished = false
                container?.isEnableAutoLoadmore = autoLoadMore.value ?: false
            }
        }
    }

    override fun statusChange(status: SwipeStatus, msg: CharSequence?) {
        status.message = msg
        this.status.value = status
    }
}