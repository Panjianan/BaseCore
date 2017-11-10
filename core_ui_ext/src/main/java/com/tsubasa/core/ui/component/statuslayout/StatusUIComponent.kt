package com.tsubasa.core.ui.component.statuslayout

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.tsubasa.core.ui.callback.*
import com.tsubasa.core.ui.component.BaseComponent
import com.tsubasa.core.ui.ext.turnVisibleOrGone
import com.tsubasa.core.util.lifecycle.bind
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.frameLayout

/**
 * 状态布局组件
 * Created by tsubasa on 2017/11/5.
 */
open class StatusUIComponent : BaseComponent<FrameLayout>(), StatusCallback {

    /**
     * 当前的布局状态
     */
    override val status: MutableLiveData<Int> = MutableLiveData()

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
    open var contentUI: BaseComponent<*>? = null

    override fun AnkoContext<Any>.createContainer(): FrameLayout = frameLayout()

    override fun createContent(parent: FrameLayout) {
        parent.apply {
            contentUI?.createComponent(parent)
            (context as? LifecycleOwner)?.let {
                bind(it)
            }
        }
    }

    override fun bind(owner: LifecycleOwner) {
        status.bind(owner) {
            (status.value == STATUS_LOADING).let {
                if (it.and(loadingUI?.container == null)) {
                    container?.initItemUI(null, loadingUI)
                }
                loadingUI?.isShow?.value = it
            }

            (status.value == STATUS_EMPTY).let {
                if (it.and(emptyUI?.container == null)) {
                    container?.initItemUI(null, emptyUI)
                }
                emptyUI?.isShow?.value = it
            }

            (status.value == STATUS_ERROR).let {
                if (it.and(errorUI?.container == null)) {
                    container?.initItemUI(null, errorUI)
                }
                errorUI?.isShow?.value = it
            }

            contentUI?.container?.visibility = (status.value == STATUS_SUCCESS).turnVisibleOrGone()
        }
        msg.bind(owner) {
            when (status.value) {
                STATUS_LOADING -> loadingUI?.msg?.value = it
                STATUS_EMPTY -> emptyUI?.msg?.value = it
                STATUS_ERROR -> errorUI?.msg?.value = it
            }
        }
    }

    override fun unBind() {
        owner?.let {
            status.removeObservers(it)
            msg.removeObservers(it)
        }
    }

    private fun ViewGroup.initItemUI(old: BaseStatusItemUIComponent<*>?, component: BaseStatusItemUIComponent<*>?, btnAction: (() -> Unit)? = null) {
        val isShow = old?.isShow?.value
        val msg = old?.msg?.value
        old?.unBind()
        old?.container?.let {
            container?.removeView(it)
        }
        component?.createComponent(this)
        component?.container?.findViewById<View?>(BaseStatusItemUIComponent.VIEW_ID_BTN_RETRY)?.setOnClickListener {
            (btnAction ?: onRetryAction)?.invoke()
        }
        (context as? LifecycleOwner)?.let {
            component?.bind(it)
            component?.isShow?.bind(it) { component.container?.visibility = it.turnVisibleOrGone() }
        }
        component?.isShow?.value = isShow
        component?.msg?.value = msg
    }

    override fun start() {
        status.value = STATUS_LOADING
    }

    override fun onLoading(msg: CharSequence?) {
        status.value = STATUS_LOADING
        this.msg.value = msg
    }

    override fun end(status: Int, msg: CharSequence?) {
        this.status.value = status
        this.msg.value = msg
    }

}

