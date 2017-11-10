package com.tsubasa.core.ui.component.swipetoload

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.widget.FrameLayout
import com.tsubasa.core.ui.callback.StatusCallback
import com.tsubasa.core.ui.component.BaseComponent
import com.tsubasa.core.ui.widget.swipetoload.swipeToLoadLayout
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko._FrameLayout
import org.jetbrains.anko.frameLayout

/***
 * <br> Project BaseCore
 * <br> Package com.tsubasa.core.ui.component.swipetoload
 * <br> Description 下拉刷新的组件
 * <br> Version 1.0
 * <br> Author Administrator
 * <br> Creation 2017/11/10 17:33
 * <br> Mender Administrator
 * <br> Modification 2017/11/10 17:33
 * <br> Copyright Copyright © 2012 - 2017 ZhongWangXinTong.All Rights Reserved.
 */
class SwipeToLoadComponent : BaseComponent<FrameLayout>(), StatusCallback {

    override val status: MutableLiveData<Int> = MutableLiveData()

    /**
     * 内容布局组件
     */
    open var contentUI: BaseComponent<*>? = null

    override fun AnkoContext<Any>.createContainer(): FrameLayout = frameLayout()

    override fun createContent(parent: FrameLayout) {
        (parent as _FrameLayout).apply {
            swipeToLoadLayout {
                contentUI?.createComponent(this)
                (context as? LifecycleOwner)?.let {
                    bind(it)
                }
            }
        }
    }

    override fun bind(owner: LifecycleOwner) {
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoading(msg: CharSequence?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun end(status: Int, msg: CharSequence?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}