package com.tsubasa.core.ui.widget.swipetoload

import android.content.Context
import android.util.AttributeSet
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
}

fun ViewManager.loadingIndicatorView(): SwipeToLoadLayout = swipeToLoadLayout {}
inline fun ViewManager.swipeToLoadLayout(init: (@AnkoViewDslMarker SwipeToLoadLayout).() -> Unit): SwipeToLoadLayout {
    return ankoView({ ctx: Context -> SwipeToLoadLayout(ctx) }, theme = 0) { init() }
}