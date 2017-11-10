package com.tsubasa.core.ui.widget.loadingindicator.indicators

import android.animation.ValueAnimator
import android.graphics.*

import com.tsubasa.core.ui.widget.loadingindicator.Indicator

import java.util.ArrayList


/**
 * 仿ios菊花
 * Created by Administrator on 2017/4/19.
 */
class LineSpinLoaderIndicator : Indicator() {

    private val alphas = IntArray(POINTER_COUNT)

    private var radius: Float = 0.toFloat()
    private var roundPx: Float = 0.toFloat()
    private var eachLength: Float = 0.toFloat()
    private var centerX: Int = 0
    private var centerY: Int = 0
    private var rectF: RectF? = null

    init {
        for (i in alphas.indices) {
            alphas[i] = ALPHA
        }
        color = Color.parseColor("#999999")
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        val baseLength = Math.min(getWidth(), getHeight())
        radius = baseLength.toFloat().div(20)
        roundPx = radius.div(2)
        eachLength = getWidth().toFloat().div(4)
        centerX = (getBounds().left + getBounds().right).div(2)
        centerY = (getBounds().top + getBounds().bottom).div(2)
        rectF = RectF(centerX.minus(radius.div(2)), 0f, centerX.plus(radius.div(2)), eachLength)
    }

    override fun draw(canvas: Canvas?, paint: Paint) {
        for (i in 0 until POINTER_COUNT) {
            canvas!!.save()
            canvas.rotate(EACH_DEGREE.times(i), centerX.toFloat(), centerY.toFloat())
            paint.alpha = alphas[i]
            canvas.drawRoundRect(rectF!!, roundPx, roundPx, paint)
            canvas.restore()
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val delays = LongArray(POINTER_COUNT)
        for (i in delays.indices) {
            delays[i] = ANIMATION_DURATION / POINTER_COUNT * i
        }
        for (i in 0 until POINTER_COUNT) {
            val alphaAnim = ValueAnimator.ofInt(255, 77, 255)
            alphaAnim.duration = ANIMATION_DURATION
            alphaAnim.repeatCount = -1
            alphaAnim.startDelay = delays[i]
            addUpdateListener(alphaAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                alphas[i] = animation.animatedValue as Int
                postInvalidate()
            })
            animators.add(alphaAnim)
        }
        return animators
    }

    companion object {

        private val ALPHA = 255

        private val POINTER_COUNT = 12

        private val EACH_DEGREE = (360 / POINTER_COUNT).toFloat()

        private val ANIMATION_DURATION: Long = 1000
    }

}