package com.tsubasa.core.util.lifecycle

import android.arch.lifecycle.*

/**
 * LiveData 自动绑定的kotlin拓展 再也不同手动指定重载了hhh
 */
fun <T> LiveData<T>.bind(lifecycleOwner: LifecycleOwner, block: (T?) -> Unit) {
    this.observe(lifecycleOwner, android.arch.lifecycle.Observer<T> {
        block(it)
    })
}

class CommonLifecycleObserver : GenericLifecycleObserver {

    private var onAny: (() -> Unit)? = null
    private var onCreate: (() -> Unit)? = null
    private var onStart: (() -> Unit)? = null
    private var onResume: (() -> Unit)? = null
    private var onPause: (() -> Unit)? = null
    private var onStop: (() -> Unit)? = null
    private var onDestory: (() -> Unit)? = null

    override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event?) {
        when (event) {
            Lifecycle.Event.ON_ANY -> {
            }
            Lifecycle.Event.ON_CREATE -> {
            }
            Lifecycle.Event.ON_START -> {
            }
            Lifecycle.Event.ON_RESUME -> {
            }
            Lifecycle.Event.ON_PAUSE -> {
            }
            Lifecycle.Event.ON_STOP -> {
            }
            Lifecycle.Event.ON_DESTROY -> {
            }
        }
    }

}

fun LifecycleOwner.addObserver(block: (CommonLifecycleObserver.() -> Unit)? = null) {
    val commonLifecycleObserver = CommonLifecycleObserver()
    block?.invoke(commonLifecycleObserver)
    lifecycle.addObserver(commonLifecycleObserver)
}