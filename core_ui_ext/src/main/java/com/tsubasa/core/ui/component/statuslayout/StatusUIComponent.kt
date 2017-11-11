package com.tsubasa.core.ui.component.statuslayout

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.tsubasa.core.ui.callback.Status
import com.tsubasa.core.ui.callback.StatusData
import com.tsubasa.core.ui.component.BaseComponent
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
open class StatusUIComponent<ContentUI : BaseComponent<*>> : BaseComponent<_FrameLayout>() {

    /**
     * 当前的布局状态
     */
    val status: MutableLiveData<StatusData> = MutableLiveData()

    /**
     * 初始化的动作
     */
    open var onInit: (() -> Unit)? = null

    /**
     * 重试的动作, 这个优先级比onInit高
     */
    open var onEmptyAction: (() -> Unit)? = null

    /**
     * 重试的动作, 这个优先级比onInit高
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
            (it?.status == Status.STATUS_LOADING).let { isLoading ->
                if (isLoading.and(loadingUI?.container == null)) {
                    container?.initItemUI(null, loadingUI)
                }
                loadingUI?.isShow?.value = isLoading
                loadingUI?.msg?.value = it?.msg
                loadingUI?.container?.visibility = isLoading.turnVisibleOrGone()
            }

            (it?.status == Status.STATUS_EMPTY).let { isEmpty ->
                if (isEmpty.and(emptyUI?.container == null)) {
                    container?.initItemUI(null, emptyUI)
                }
                emptyUI?.isShow?.value = isEmpty
                emptyUI?.msg?.value = it?.msg
                emptyUI?.container?.visibility = isEmpty.turnVisibleOrGone()
            }

            (it?.status == Status.STATUS_ERROR).let { isError ->
                if (isError.and(errorUI?.container == null)) {
                    container?.initItemUI(null, errorUI)
                }
                errorUI?.isShow?.value = isError
                errorUI?.msg?.value = it?.msg
                errorUI?.container?.visibility = isError.turnVisibleOrGone()
            }

            contentUI?.container?.visibility = (status.value?.status == Status.STATUS_SUCCESS).or(status.value?.status == Status.STATUS_SUCCESS_NO_MORE).turnVisibleOrGone()
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
            (btnAction ?: onInit)?.invoke()
        }
        component?.isShow?.value = isShow
        component?.msg?.value = msg
    }

}