package com.tsubasa.core.ui.widget.loadingindicator

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import java.util.ArrayList
import java.util.HashMap

/**
 * 加载动画的基类
 * Created by tsubasa on 2017/11/6.
 */
abstract class Indicator : Drawable(), Animatable {

    companion object {
        val ZERO_BOUNDS_RECT = Rect()
    }


    private val mUpdateListeners = HashMap<ValueAnimator, ValueAnimator.AnimatorUpdateListener>()
    private var mAnimators: ArrayList<ValueAnimator>? = null
    private var alpha: Int = 255
    open var drawBounds = ZERO_BOUNDS_RECT
        set(value) {
            field = Rect(value.left, value.top, value.right, value.bottom)
        }
    private var mHasAnimators: Boolean = false
    private val mPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    var color: Int
        get() = mPaint.color
        set(value) {
            mPaint.color = value
        }

    override fun setAlpha(alpha: Int) {
        this.alpha = alpha
    }

    override fun getAlpha(): Int = alpha

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun draw(canvas: Canvas?) {
        draw(canvas, mPaint)
    }

    abstract fun draw(canvas: Canvas?, paint: Paint)

    abstract fun onCreateAnimators(): ArrayList<ValueAnimator>

    override fun start() {
        ensureAnimators()
        if ((mAnimators == null).or(isStarted())) {
            return
        }
        startAnimators()
        invalidateSelf()
    }

    private fun startAnimators() {
        mAnimators?.forEach { animator ->
            mUpdateListeners[animator]?.let { animator.addUpdateListener(it) }
            animator.start()
        }
    }

    private fun stopAnimators() {
        mAnimators?.filter { it.isStarted }?.forEach {
            it.removeAllUpdateListeners()
            it.end()
        }
    }

    private fun ensureAnimators() {
        if (mHasAnimators.not()) {
            mAnimators = onCreateAnimators()
            mHasAnimators = true
        }
    }

    override fun stop() {
        stopAnimators()
    }

    private fun isStarted(): Boolean = mAnimators?.any { it.isStarted } ?: false

    override fun isRunning(): Boolean = mAnimators?.any { it.isRunning } ?: false

    fun addUpdateListener(animator: ValueAnimator, updateListener: ValueAnimator.AnimatorUpdateListener) {
        mUpdateListeners.put(animator, updateListener)
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        bounds?.let { drawBounds = it }
    }

    open fun setDrawBounds(left: Int, top: Int, right: Int, bottom: Int) {
        drawBounds = Rect(left, top, right, bottom)
    }

    open fun postInvalidate() {
        invalidateSelf()
    }

    open fun getWidth(): Int = drawBounds.width()

    open fun getHeight(): Int = drawBounds.height()

    open fun centerX(): Int = drawBounds.centerX()

    open fun centerY(): Int = drawBounds.centerY()

    open fun exactCenterX(): Float = drawBounds.exactCenterX()

    open fun exactCenterY(): Float = drawBounds.exactCenterY()
}