package com.tsubasa.core.util.lifecycle

import android.arch.lifecycle.LiveData

/**
 * A LiveData class that has {@code null} value.
 * Created by tsubasa on 2017/11/12.
 */
class NullLiveData<T> : LiveData<T>() {
    init {
        value = null
    }
}