package com.tsubasa.core.ui.callback

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

/**
 * 状态回调
 * Created by tsubasa on 2017/11/4.
 */
const val STATUS_SUCCESS = 0
const val STATUS_ERROR = 1
const val STATUS_EMPTY = 2
const val STATUS_LOADING = 3


interface StatusCallback {

    val status: LiveData<Int>

    fun start()

    fun onLoading(msg: CharSequence? = null)

    fun end(status: Int = STATUS_SUCCESS, msg: CharSequence? = null)

}

class OnStatusCallback : StatusCallback {

    override val status: LiveData<Int> = MutableLiveData()

    private var delegates: MutableList<StatusCallback?> = mutableListOf()
    private var onStart: (() -> Unit)? = null
    private var onLoading: ((msg: CharSequence?) -> Unit)? = null
    private var onEnd: ((status: Int, msg: CharSequence?) -> Unit)? = null

    fun addDelegate(vararg delegate:StatusCallback?) {
        delegate.filterNot { it == null }.forEach {
            delegates.add(it)
        }
    }

    fun onStart(block: (() -> Unit)) {
        this.onStart = block
    }
    fun onLoading(block: ((msg: CharSequence?) -> Unit)) {
        this.onLoading = block
    }
    fun onEnd(block: ((status: Int, msg: CharSequence?) -> Unit)) {
        this.onEnd = block
    }

    override fun start() {
        (status as MutableLiveData).value = STATUS_LOADING
        onStart?.invoke()
        delegates.forEach { it?.start() }
    }

    override fun onLoading(msg: CharSequence?) {
        (status as MutableLiveData).value = STATUS_LOADING
        onLoading?.invoke(msg)
        delegates.forEach { it?.onLoading(msg) }
    }

    override fun end(status: Int, msg: CharSequence?) {
        (this.status as MutableLiveData).value = status
        onEnd?.invoke(status, msg)
        delegates.forEach { it?.end(status, msg) }
    }

}

fun createStatusCallback(init: OnStatusCallback.() -> Unit): OnStatusCallback {
    val onProgress = OnStatusCallback()
    onProgress.init()
    return onProgress
}