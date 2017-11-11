package com.tsubasa.core.ui.component.statuslayout

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.tsubasa.core.ui.callback.*
import com.tsubasa.core.ui.component.BaseComponent
import com.tsubasa.core.ui.component.recyclerview.RecyclerViewComponent
import com.tsubasa.core.ui.component.swipetoload.SwipeToLoadComponent
import com.tsubasa.core.ui.component.viewholder.BaseComponentAdapter
import com.tsubasa.core.ui.ext.turnVisibleOrGone
import com.tsubasa.core.util.lifecycle.bind
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko._FrameLayout
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent

/**
 * 状态布局组件
 * Created by tsubasa on 2017/11/5.
 */
open class StatusUIComponent<ContentUI : BaseComponent<*>> : BaseComponent<_FrameLayout>(), StatusCallback<Status> {

    /**
     * 当前的布局状态
     */
    override val status: MutableLiveData<Status> = MutableLiveData()

    /**
     * 当前的提示文字
     */
    private val msg: MutableLiveData<CharSequence> = MutableLiveData()

    /**
     * 重试的动作
     */
    open var onRetryAction: (() -> Unit)? = null

    /**
     * 重试的动作, 这个优先级比onRetryAction高
     */
    open var onEmptyAction: (() -> Unit)? = null

    /**
     * 重试的动作, 这个优先级比onRetryAction高
     */
    open var onErrorAction: (() -> Unit)? = null

    /**
     * 空布局组件
     */
    open var emptyUI: BaseStatusItemUIComponent<*>? = DefaultStatusItemUIComponent()
        set(value) {
            container?.initItemUI(field, value, onEmptyAction)
            field = value
        }
    /**
     * 错误布局组件
     */
    open var errorUI: BaseStatusItemUIComponent<*>? = DefaultStatusItemUIComponent()
        set(value) {
            container?.initItemUI(field, value, onErrorAction)
            field = value
        }
    /**
     * 加载布局组件
     */
    open var loadingUI: BaseStatusItemUIComponent<*>? = DefaultLoadingUIComponent()
        set(value) {
            container?.initItemUI(field, value)
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

    override fun createContainer(context: AnkoContext<Any>): _FrameLayout = context.frameLayout() as _FrameLayout

    override fun createContent(parent: _FrameLayout) {
        parent.apply {
            contentUI?.createComponent(parent)?.lparams(matchParent, matchParent)
        }
    }

    override fun bindData(lifecycleOwner: LifecycleOwner) {
        status.bind(lifecycleOwner) {
            (status.value == Status.STATUS_LOADING).let {
                if (it.and(loadingUI?.container == null)) {
                    container?.initItemUI(null, loadingUI)
                }
                loadingUI?.isShow?.value = it
            }

            (status.value == Status.STATUS_EMPTY).let {
                if (it.and(emptyUI?.container == null)) {
                    container?.initItemUI(null, emptyUI)
                }
                emptyUI?.isShow?.value = it
            }

            (status.value == Status.STATUS_ERROR).let {
                if (it.and(errorUI?.container == null)) {
                    container?.initItemUI(null, errorUI)
                }
                errorUI?.isShow?.value = it
            }

            contentUI?.container?.visibility = (status.value == Status.STATUS_SUCCESS).or(status.value == Status.STATUS_SUCCESS_NO_MORE).turnVisibleOrGone()
        }
        msg.bind(lifecycleOwner) {
            when (status.value) {
                Status.STATUS_LOADING -> loadingUI?.msg?.value = it
                Status.STATUS_EMPTY -> emptyUI?.msg?.value = it
                Status.STATUS_ERROR -> errorUI?.msg?.value = it
                else -> {
                }
            }
        }
    }

    private fun _FrameLayout.initItemUI(old: BaseStatusItemUIComponent<*>?, component: BaseStatusItemUIComponent<*>?, btnAction: (() -> Unit)? = null) {
        val isShow = old?.isShow?.value
        val msg = old?.msg?.value
        old?.unBind()
        old?.container?.let {
            container?.removeView(it)
        }
        component?.createComponent(this)?.lparams(matchParent, matchParent)
        component?.container?.findViewById<View?>(BaseStatusItemUIComponent.VIEW_ID_BTN_RETRY)?.setOnClickListener {
            (btnAction ?: onRetryAction)?.invoke()
        }
        component?.isShow?.value = isShow
        component?.msg?.value = msg
    }

    override fun statusChange(status: Status, msg: CharSequence?) {
        this.status.value = status
        this.msg.value = msg
    }
}

object StatusUIComponentFactory {
    fun standardListComponent(initBlock: (RecyclerView.() -> RecyclerView.Adapter<*>)): StatusUIComponent<SwipeToLoadComponent<RecyclerViewComponent>> {
        return StatusUIComponent<SwipeToLoadComponent<RecyclerViewComponent>>().apply {
            val swipeToLoadComponent = SwipeToLoadComponent<RecyclerViewComponent>()
            swipeToLoadComponent.contentUI = RecyclerViewComponent(initBlock)
            contentUI = swipeToLoadComponent
        }
    }
}

fun StatusUIComponent<SwipeToLoadComponent<RecyclerViewComponent>>.getSwipeToLoadComponent(): SwipeToLoadComponent<RecyclerViewComponent>? {
    return contentUI
}

fun <DATA> StatusUIComponent<SwipeToLoadComponent<RecyclerViewComponent>>.getAdapter(): BaseQuickAdapter<DATA, *>? {
    @Suppress("UNCHECKED_CAST")
    return contentUI?.contentUI?.container?.adapter as? BaseQuickAdapter<DATA, *>
}