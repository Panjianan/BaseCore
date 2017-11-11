package com.tsubasa.core.util.lifecycle

import android.arch.lifecycle.*

/**
 * LiveData 自动绑定的kotlin拓展 再也不同手动指定重载了hhh
 */
fun <T> LiveData<T>.bind(lifecycleOwner: LifecycleOwner?, block: (T?) -> Unit) {
    lifecycleOwner?.let {
        this.observe(lifecycleOwner, android.arch.lifecycle.Observer<T> {
            block(it)
        })
    }
}

open class CommonLifecycleObserver : GenericLifecycleObserver {

    private var onAny: ((source: LifecycleOwner?) -> Unit)? = null
    open fun onAny(block: (source: LifecycleOwner?) -> Unit) {
        onAny = block
    }

    private var onCreate: ((source: LifecycleOwner?) -> Unit)? = null
    open fun onCreate(block: (source: LifecycleOwner?) -> Unit) {
        onCreate = block
    }

    private var onStart: ((source: LifecycleOwner?) -> Unit)? = null
    open fun onStart(block: (source: LifecycleOwner?) -> Unit) {
        onStart = block
    }

    private var onResume: ((source: LifecycleOwner?) -> Unit)? = null
    open fun onResume(block: (source: LifecycleOwner?) -> Unit) {
        onResume = block
    }

    private var onPause: ((source: LifecycleOwner?) -> Unit)? = null
    open fun onPause(block: (source: LifecycleOwner?) -> Unit) {
        onPause = block
    }

    private var onStop: ((source: LifecycleOwner?) -> Unit)? = null
    open fun onStop(block: (source: LifecycleOwner?) -> Unit) {
        onStop = block
    }

    private var onDestroy: ((source: LifecycleOwner?) -> Unit)? = null
    open fun onDestroy(block: (source: LifecycleOwner?) -> Unit) {
        onDestroy = block
    }

    override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event?) {
        when (event) {
            Lifecycle.Event.ON_ANY -> onAny?.invoke(source)
            Lifecycle.Event.ON_CREATE -> onCreate?.invoke(source)
            Lifecycle.Event.ON_START -> onStart?.invoke(source)
            Lifecycle.Event.ON_RESUME -> onResume?.invoke(source)
            Lifecycle.Event.ON_PAUSE -> onPause?.invoke(source)
            Lifecycle.Event.ON_STOP -> onStop?.invoke(source)
            Lifecycle.Event.ON_DESTROY -> onDestroy?.invoke(source)
        }
    }

}

fun LifecycleOwner.addObserver(block: (CommonLifecycleObserver.() -> Unit)? = null) {
    val commonLifecycleObserver = CommonLifecycleObserver()
    block?.invoke(commonLifecycleObserver)
    lifecycle.addObserver(commonLifecycleObserver)
}