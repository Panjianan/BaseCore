package com.tsubasa.core.ui.component.statuslayout

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.view.ViewGroup
import com.tsubasa.core.ui.component.BaseComponent
import com.tsubasa.core.util.lifecycle.bind

/**
 * 状态布局的具体页面基类
 * Created by tsubasa on 2017/11/6.
 */
abstract class BaseStatusItemUIComponent<T : ViewGroup> : BaseComponent<T>() {

    companion object {
        const val VIEW_ID_TV_MSG = 0x112
        const val VIEW_ID_ICON = 0x113
        const val VIEW_ID_BTN_RETRY = 0x114
    }

    val isShow: MutableLiveData<Boolean> = MutableLiveData()
    val msg: MutableLiveData<CharSequence> = MutableLiveData()

    override fun bindData(lifecycleOwner: LifecycleOwner) {
        this.owner = lifecycleOwner
        isShow.bind(lifecycleOwner) {
            if (container != null) {
                showLayout(it ?: false)
            }
        }
        msg.bind(lifecycleOwner) {
            if (container != null) {
                showMsg(it)
            }
        }
    }

    open fun unBind() {
        owner?.let {
            isShow.removeObservers(it)
            msg.removeObservers(it)
        }
    }

    abstract protected fun showLayout(isShow: Boolean)

    abstract protected fun showMsg(msg: CharSequence? = null)

}
