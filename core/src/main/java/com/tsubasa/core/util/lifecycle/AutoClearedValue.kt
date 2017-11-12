package com.tsubasa.core.util.lifecycle

import android.arch.lifecycle.LifecycleOwner

/**
 * 根据生命周期自动清除引用的
 * Created by tsubasa on 2017/11/12.
 */
class AutoClearedValue<T>(lifecycleOwner: LifecycleOwner, val value: T) {

    var valueHolder: T? = value
        private set

    fun get(): T? = valueHolder

    init {
        lifecycleOwner.addObserver {
            onDestroy {
                this@AutoClearedValue.valueHolder = null
                it?.lifecycle?.removeObserver(this)
            }
        }
    }

}

fun <T> LifecycleOwner.createAutoClearedValue(value: T): AutoClearedValue<T> {
    return AutoClearedValue(this, value)
}