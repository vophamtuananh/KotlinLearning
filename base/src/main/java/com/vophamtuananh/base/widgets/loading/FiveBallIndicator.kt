package com.vophamtuananh.base.widgets.loading

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.util.ArrayList

/**
 * Created by vophamtuananh on 1/3/18.
 */
class FiveBallIndicator : Indicator(COLOR) {

    companion object {
        private val COLOR = Color.parseColor("#DDFFFFFF")
        private val BALL_COUNT = 5
        private val SPACING = 10

        private val SCALE = 1.0f
    }

    private val scaleFloats = floatArrayOf(SCALE, SCALE, SCALE, SCALE, SCALE, SCALE)

    private var radius = 0f

    override fun draw(canvas: Canvas, paint: Paint) {
        if (radius == 0f)
            radius = ((getWidth() - (BALL_COUNT - 1) * SPACING) / (BALL_COUNT * 2)).toFloat()
        var x = radius
        val y = (getHeight() / 2).toFloat()
        for (i in 0 until BALL_COUNT) {
            canvas.save()
            canvas.translate(x, y)
            x += radius * 2 + SPACING
            canvas.scale(scaleFloats[i], scaleFloats[i])
            canvas.drawCircle(0f, 0f, radius, paint)
            canvas.restore()
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val delays = intArrayOf(80, 160, 240, 320, 400, 480)
        for (i in 0 until BALL_COUNT) {

            val scaleAnim = ValueAnimator.ofFloat(1.0f, 0.3f, 1.0f)

            scaleAnim.duration = 1000
            scaleAnim.repeatCount = -1
            scaleAnim.startDelay = delays[i].toLong()

            addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener {
                valueAnimator ->
                scaleFloats[i] = valueAnimator.animatedValue as Float
                postInvalidate()
            })
            animators.add(scaleAnim)
        }
        return animators
    }
}