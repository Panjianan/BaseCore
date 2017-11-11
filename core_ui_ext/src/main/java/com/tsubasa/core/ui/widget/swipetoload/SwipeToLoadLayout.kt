package com.tsubasa.core.ui.widget.swipetoload

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko.custom.ankoView

/***
 * <br> Project BaseCore
 * <br> Package com.tsubasa.core.ui.widget.swipetoload
 * <br> Description 下拉刷新的控件
 * <br> Version 1.0
 * <br> Author Administrator
 * <br> Creation 2017/11/10 17:53
 * <br> Mender Administrator
 * <br> Modification 2017/11/10 17:53
 * <br> Copyright Copyright © 2012 - 2017 ZhongWangXinTong.All Rights Reserved.
 */
class SwipeToLoadLayout(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : SmartRefreshLayout(context, attributeSet, defStyle) {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    init {
        setDisableContentWhenLoading(true)
        setDisableContentWhenRefresh(true)
    }

    inline fun <T: View> T.lparams(
            c: Context?,
            attrs: AttributeSet?,
            init: SmartRefreshLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = SmartRefreshLayout.LayoutParams(c!!, attrs!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T: View> T.lparams(
            c: Context?,
            attrs: AttributeSet?
    ): T {
        val layoutParams = SmartRefreshLayout.LayoutParams(c!!, attrs!!)
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T: View> T.lparams(
            width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            init: SmartRefreshLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = SmartRefreshLayout.LayoutParams(width, height)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T: View> T.lparams(
            width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT
    ): T {
        val layoutParams = SmartRefreshLayout.LayoutParams(width, height)
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T: View> T.lparams(
            source: ViewGroup.LayoutParams?,
            init: SmartRefreshLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = SmartRefreshLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T: View> T.lparams(
            source: ViewGroup.LayoutParams?
    ): T {
        val layoutParams = SmartRefreshLayout.LayoutParams(source!!)
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T: View> T.lparams(
            source: ViewGroup.MarginLayoutParams?,
            init: SmartRefreshLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = SmartRefreshLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T: View> T.lparams(
            source: ViewGroup.MarginLayoutParams?
    ): T {
        val layoutParams = SmartRefreshLayout.LayoutParams(source!!)
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T: View> T.lparams(
            source: SmartRefreshLayout.LayoutParams?,
            init: SmartRefreshLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = SmartRefreshLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T: View> T.lparams(
            source: SmartRefreshLayout.LayoutParams?
    ): T {
        val layoutParams = SmartRefreshLayout.LayoutParams(source!!)
        this@lparams.layoutParams = layoutParams
        return this
    }
}

fun ViewManager.swipeToLoadLayout(): SwipeToLoadLayout = swipeToLoadLayout {}
inline fun ViewManager.swipeToLoadLayout(init: (@AnkoViewDslMarker SwipeToLoadLayout).() -> Unit): SwipeToLoadLayout {
    return ankoView({ ctx: Context -> SwipeToLoadLayout(ctx) }, theme = 0) { init() }
}

fun Context.swipeToLoadLayout(): SwipeToLoadLayout = swipeToLoadLayout {}
inline fun Context.swipeToLoadLayout(init: (@AnkoViewDslMarker SwipeToLoadLayout).() -> Unit): SwipeToLoadLayout {
    return ankoView({ ctx: Context -> SwipeToLoadLayout(ctx) }, theme = 0) { init() }
}