package com.tsubasa.core.ui.ext

import android.content.Context
import android.view.View
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.UI

/**
 * View的相关扩展
 * Created by tsubasa on 2017/11/5.
 */
fun Boolean?.turnVisibleOrGone(default: Int = View.GONE): Int {
    if (this == null) {
        return default
    }
    return if (this) {
        View.VISIBLE
    } else {
        View.GONE
    }
}