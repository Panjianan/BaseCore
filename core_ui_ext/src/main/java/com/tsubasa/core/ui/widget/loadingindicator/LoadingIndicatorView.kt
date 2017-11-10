package com.tsubasa.core.ui.widget.loadingindicator

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewManager
import android.view.animation.AnimationUtils
import com.tsubasa.core.R
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.dip

const val TAG = "LoadingIndicatorView"

/**
 * 加载动画的控件
 * Created by tsubasa on 2017/11/6.
 */
open class LoadingIndicatorView(context: Context,
                                attributes: AttributeSet? = null,
                                defStyleAttr: Int = 0,
                                defaultStyleRes: Int = R.style.LoadingIndicatorView) : View(context, attributes, defStyleAttr, defaultStyleRes) {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributes: AttributeSet? = null) : this(context, attributes, 0)

    constructor(context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0) : this(context, attributes, defStyleAttr, R.style.LoadingIndicatorView)


    companion object {
        const val MIN_SHOW_TIME = 16.toLong()
        const val MIN_DELAY = 16.toLong()
    }

    private var mStartTime: Long = -1
    private var mPostedHide = false
    private var mPostedShow = false
    private var mDismissed = false
    private var mMinWidth = 0
    private var mMaxWidth = 0
    private var mMinHeight = 0
    private var mMaxHeight = 0
    open var indicator: Indicator? = null
        set(value) {
            if ((field != value)) {
                if (field != null) {
                    field?.callback = null
                    unscheduleDrawable(field)
                }

                field = value
                if (value != null) {
                    value.color = indicatorColor
                    value.callback = this
                }
                postInvalidate()
            }
        }
    open var indicatorColor: Int = 0
        get() = indicator?.color ?: field
        set(value) {
            field = value
            indicator?.color = value
        }
    private var mShouldStartAnimationDrawable = false

    private val mDelayedHide = {
        mPostedHide = false
        mStartTime = -1
        visibility = View.GONE
    }
    private val mDelayedShow = {
        mPostedShow = true
        if (mDismissed.not()) {
            mStartTime = System.currentTimeMillis()
            visibility = View.VISIBLE
        }
    }

    init {
        context.obtainStyledAttributes(attributes, R.styleable.LoadingIndicatorView, defStyleAttr, defaultStyleRes).apply {
            mMinWidth = getDimensionPixelSize(R.styleable.LoadingIndicatorView_minWidth, dip(24))
            mMaxWidth = getDimensionPixelSize(R.styleable.LoadingIndicatorView_maxWidth, dip(48))
            mMinHeight = getDimensionPixelSize(R.styleable.LoadingIndicatorView_minHeight, dip(24))
            mMaxHeight = getDimensionPixelSize(R.styleable.LoadingIndicatorView_maxHeight, dip(48))
            val indicatorName = getString(R.styleable.LoadingIndicatorView_indicatorName)
            indicatorColor = getColor(R.styleable.LoadingIndicatorView_indicatorColor, indicatorColor)
            setIndicator(indicatorName)
            recycle()
        }
    }

    open fun setIndicator(name: String?) {
        if (name == null || name.isEmpty()) {
            return
        }
        val className = StringBuilder().apply {
            if (name.contains(".").not()) {
                append("${javaClass.`package`.name}.indicators.")
            }
            append(name)
        }.toString()
        try {
            indicator = Class.forName(className).newInstance() as Indicator
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Didn't find your class , check the name again !")
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    fun smoothToShow() {
        startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
        visibility = View.VISIBLE
    }

    fun smoothToHide() {
        startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
        visibility = View.GONE
    }

    fun hide() {
        mDismissed = true
        removeCallbacks(mDelayedShow)
        mPostedShow = false
        val diff = System.currentTimeMillis() - mStartTime
        if ((diff >= MIN_SHOW_TIME).or(mStartTime == (-1).toLong())) {
            visibility = View.GONE
        } else {
            if (mPostedHide.not()) {
                postDelayed(mDelayedHide, MIN_SHOW_TIME.minus(diff))
                mPostedHide = true
            }
        }
    }

    fun show() {
        mStartTime = (-1).toLong()
        mDismissed = false
        removeCallbacks(mDelayedHide)
        mPostedHide = false
        if (mPostedShow.not()) {
            postDelayed(mDelayedShow, MIN_DELAY)
            mPostedShow = true
        }
    }

    override fun verifyDrawable(who: Drawable?): Boolean = (who == indicator).or(super.verifyDrawable(who))

    internal fun startAnimation() {
        if (visibility != VISIBLE) {
            return
        }
        if (indicator is Animatable) {
            mShouldStartAnimationDrawable = true
        }
        postInvalidate()
    }

    internal fun stopAnimation() {
        if (indicator is Animatable) {
            indicator?.stop()
            mShouldStartAnimationDrawable = false
        }
        postInvalidate()
    }

    override fun setVisibility(visibility: Int) {
        if (this.visibility != visibility) {
            super.setVisibility(visibility)
            if ((visibility == GONE).or(visibility == INVISIBLE)) {
                stopAnimation()
            } else {
                startAnimation()
            }
        }
    }

    override fun onVisibilityChanged(changedView: View?, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if ((visibility == GONE).or(visibility == INVISIBLE)) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }

    override fun invalidateDrawable(drawable: Drawable?) {
        if (verifyDrawable(drawable).and(drawable != null)) {
            drawable?.bounds?.let {
                val scrollX = scrollX.plus(paddingLeft)
                val scrollY = scrollY.plus(paddingTop)
                invalidate(it.left.plus(scrollX), it.top.plus(scrollY),
                        it.right.plus(scrollX), it.bottom.plus(scrollY))
            }
        } else {
            super.invalidateDrawable(drawable)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        updateDrawableBounds(w, h)
    }

    private fun updateDrawableBounds(width: Int, height: Int) {
        val w = width.minus(paddingRight.plus(paddingLeft))
        val h = height.minus(paddingTop.plus(paddingBottom))

        var top = 0
        var left = 0
        var right = w
        var bottom = h

        indicator?.let {
            val intrinsicAspect = it.intrinsicWidth.toFloat().div(it.intrinsicHeight)
            val boundAspect = w.toFloat().div(h)
            if (intrinsicAspect != boundAspect) {
                if (boundAspect > intrinsicAspect) {
                    val finalWidth = h.times(intrinsicAspect).toInt()
                    left = w.minus(finalWidth).div(2)
                    right = left.plus(finalWidth)
                } else {
                    val finalHeight = w.times(1.div(intrinsicAspect)).toInt()
                    top = h.minus(finalHeight).div(2)
                    bottom = top.plus(finalHeight)
                }
            }
            it.setBounds(left, top, right, bottom)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawTrack(canvas)
    }

    internal fun drawTrack(canvas: Canvas?) {
        indicator?.let {
            canvas?.let { canvas ->
                val saveCount = canvas.save()
                canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
                it.draw(canvas)
                canvas.restoreToCount(saveCount)

            }
        }
        if (mShouldStartAnimationDrawable.and(indicator is Animatable)) {
            indicator?.start()
            mShouldStartAnimationDrawable = false
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var dw = 0
        var dh = 0
        indicator?.let {
            dw = Math.max(mMinWidth, Math.min(mMaxWidth, it.intrinsicWidth))
            dh = Math.max(mMinHeight, Math.min(mMaxHeight, it.intrinsicHeight))
        }
        updateDrawableState()
        dw = dw.plus(paddingLeft).plus(paddingRight)
        dh = dh.plus(paddingTop).plus(paddingBottom)
        val measureWidth = resolveSizeAndState(dw, widthMeasureSpec, 0)
        val measureHeight = resolveSizeAndState(dh, heightMeasureSpec, 0)
        setMeasuredDimension(measureWidth, measureHeight)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        updateDrawableState()
    }

    private fun updateDrawableState() {
        if (indicator?.isStateful == true) {
            indicator?.state = drawableState
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)
        indicator?.setHotspot(x, y)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
        removeCallbacks()
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        super.onDetachedFromWindow()
        removeCallbacks()
    }

    private fun removeCallbacks() {
        removeCallbacks(mDelayedHide)
        removeCallbacks(mDelayedShow)
    }
}

fun ViewManager.loadingIndicatorView(): LoadingIndicatorView = loadingIndicatorView {}
inline fun ViewManager.loadingIndicatorView(init: (@AnkoViewDslMarker LoadingIndicatorView).() -> Unit): LoadingIndicatorView {
    return ankoView({ ctx: Context -> LoadingIndicatorView(ctx) }, theme = 0) { init() }
}