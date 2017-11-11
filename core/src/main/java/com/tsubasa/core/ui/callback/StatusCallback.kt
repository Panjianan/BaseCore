package com.tsubasa.core.ui.callback

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

/**
 * 状态回调
 * Created by tsubasa on 2017/11/4.
 */
enum class Status {
    STATUS_SUCCESS,
    STATUS_SUCCESS_NO_MORE,
    STATUS_ERROR,
    STATUS_EMPTY,
    STATUS_LOADING
}


interface StatusCallback<T> {

    val status: LiveData<T>

    fun statusChange(status: T, msg: CharSequence? = null)

}

class OnStatusCallback : StatusCallback<Status> {

    private val mStatus: MutableLiveData<Status> = MutableLiveData()
    override val status: LiveData<Status> = mStatus

    private var delegates: MutableList<StatusCallback<Status>> = mutableListOf()
    fun addDelegate(vararg delegate: StatusCallback<Status>?) {
        delegate.filterNot { it == null }.forEach {
            delegates.add(it!!)
        }
    }

    private var onStatusChange: ((status: Status, msg: CharSequence?) -> Unit)? = null
    fun onStatusChange(block: ((status: Status, msg: CharSequence?) -> Unit)) {
        this.onStatusChange = block
    }

    override fun statusChange(status: Status, msg: CharSequence?) {
        mStatus.value = status
        onStatusChange?.invoke(status, msg)
        delegates.forEach { it.statusChange(status, msg) }
    }
}

fun createStatusCallback(init: OnStatusCallback.() -> Unit): OnStatusCallback {
    val onProgress = OnStatusCallback()
    onProgress.init()
    return onProgress
}