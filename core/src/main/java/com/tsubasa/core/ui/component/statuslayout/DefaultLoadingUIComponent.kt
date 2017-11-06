package com.tsubasa.core.ui.component.statuslayout

import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.tsubasa.core.R
import com.tsubasa.core.ui.widget.loadingindicator.LoadingIndicatorView
import com.tsubasa.core.ui.widget.loadingindicator.indicators.LineSpinLoaderIndicator
import com.tsubasa.core.ui.widget.loadingindicator.loadingIndicatorView
import org.jetbrains.anko.*

/**
 * 默认布局
 * Created by tsubasa on 2017/11/6.
 */
class DefaultLoadingUIComponent : BaseStatusItemUIComponent<LinearLayout>() {

    override fun AnkoContext<Any>.createContainer(): LinearLayout = verticalLayout()

    override fun createContent(parent: LinearLayout) {
        (parent as? _LinearLayout)?.apply {
            weightSum = 100.toFloat()
            id = VIEW_ID_BTN_RETRY
            gravity = Gravity.CENTER_HORIZONTAL

            space().lparams(matchParent, 0, 20.toFloat())

            loadingIndicatorView {
                id = VIEW_ID_ICON
                indicator = LineSpinLoaderIndicator()
            }.lparams(matchParent, 0, 10.toFloat())

            space().lparams(matchParent, 0, 10.toFloat())

            textView {
                id = VIEW_ID_TV_MSG
                textSize = 16.toFloat()
                textColor = ContextCompat.getColor(context, R.color.default_status_layout_hint_color)
            }.lparams(wrapContent, wrapContent)
        }
    }

    override fun showLayout(isShow: Boolean) {
        container?.findViewById<LoadingIndicatorView?>(VIEW_ID_ICON)?.let {
            if (isShow) {
                it.startAnimation()
            } else {
                it.stopAnimation()
            }
        }
    }

    override fun showMsg(msg: CharSequence?) {
        container?.findViewById<TextView?>(VIEW_ID_TV_MSG)?.text = msg
    }

}