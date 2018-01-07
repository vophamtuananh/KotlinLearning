package com.vophamtuananh.base.widgets.loading

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import java.util.ArrayList

/**
 * Created by vophamtuananh on 12/24/17.
 */
abstract class Indicator(color: Int) : Drawable(), Animatable {
    companion object {
        private val ZERO_BOUNDS_RECT = Rect()
    }

    private var mUpdateListeners = HashMap<ValueAnimator, ValueAnimator.AnimatorUpdateListener>()

    private var mAnimators: ArrayList<ValueAnimator>? = null
    private var mAlpha = 255
    private var drawBounds = ZERO_BOUNDS_RECT

    private var mHasAnimators: Boolean = false

    private val mPaint = Paint()

    init {
        mPaint.color = color
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
    }

    fun setColor(color: Int) {
        mPaint.color = color
    }

    override fun setAlpha(alpha: Int) {
        mAlpha = alpha
    }

    override fun getAlpha(): Int {
        return mAlpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun draw(canvas: Canvas) {
        draw(canvas, mPaint)
    }

    abstract fun draw(canvas: Canvas, paint: Paint)

    abstract fun onCreateAnimators(): ArrayList<ValueAnimator>

    override fun start() {
        ensureAnimators()

        if (mAnimators == null) {
            return
        }

        if (isStarted(mAnimators!!)) {
            return
        }
        startAnimators(mAnimators!!)
        invalidateSelf()
    }

    private fun startAnimators(animators: ArrayList<ValueAnimator>) {
        for (i in 0 until animators.size) {
            val animator = animators[i]
            val updateListener = mUpdateListeners[animator]
            if (updateListener != null) {
                animator.addUpdateListener(updateListener)
            }

            animator.start()
        }
    }

    private fun stopAnimators(animators: ArrayList<ValueAnimator>) {
        for (animator in animators) {
            if (animator.isStarted) {
                animator.removeAllUpdateListeners()
                animator.end()
            }
        }
    }

    private fun ensureAnimators() {
        if (!mHasAnimators) {
            mAnimators = onCreateAnimators()
            mHasAnimators = true
        }
    }

    override fun stop() {
        if (mAnimators == null)
            return
        stopAnimators(mAnimators!!)
    }

    private fun isStarted(animators: ArrayList<ValueAnimator>): Boolean {
        return animators.any { it.isStarted }
    }

    override fun isRunning(): Boolean {
        if (mAnimators == null)
            return false
        return mAnimators!!.any { it.isRunning }
    }

    fun addUpdateListener(animator: ValueAnimator, updateListener: ValueAnimator.AnimatorUpdateListener) {
        mUpdateListeners.put(animator, updateListener)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        setDrawBounds(bounds)
    }

    private fun setDrawBounds(drawBounds: Rect) {
        setDrawBounds(drawBounds.left, drawBounds.top, drawBounds.right, drawBounds.bottom)
    }

    private fun setDrawBounds(left: Int, top: Int, right: Int, bottom: Int) {
        this.drawBounds = Rect(left, top, right, bottom)
    }

    fun postInvalidate() {
        invalidateSelf()
    }

    fun getWidth(): Int {
        return drawBounds.width()
    }

    fun getHeight(): Int {
        return drawBounds.height()
    }

}