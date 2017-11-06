package com.tsubasa.core.ui.lifecycle

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData

/**
 * Created by tsubasa on 2017/11/5.
 */
/**
 * LiveData 自动绑定的kotlin拓展 再也不同手动指定重载了hhh
 */
fun <T> LiveData<T>.bind(lifecycleOwner: LifecycleOwner, block: (T?) -> Unit) {
    this.observe(lifecycleOwner, android.arch.lifecycle.Observer<T> {
        block(it)
    })
}