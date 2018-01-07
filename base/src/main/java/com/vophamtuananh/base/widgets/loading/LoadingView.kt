package com.vophamtuananh.base.widgets.loading

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.vophamtuananh.base.R
import com.vophamtuananh.base.utils.DisplayUtil

/**
 * Created by vophamtuananh on 1/3/18.
 */
class LoadingView : View {

    companion object {
        private val TAG = "LoadingView"

        private val DEFAULT_BASE_ON_LINEAR = true
        private val DEFAULT_SHOULD_SQUARE = false
        private val DEFAULT_PERCENT = 0

        private val DEFAULT_COLOR = Color.parseColor("#DDFFFFFF")
        private val DEFAULT_INDICATOR = FiveBallIndicator()
    }

    private var mMesureBaseOnLinear: Boolean = false
    private var mShouldSquare: Boolean = false
    private var mPercent: Int = 0

    private var mStartTime: Long = -1

    private var mPostedHide = false

    private var mPostedShow = false

    private var mDismissed = false

    private val mDelayedHide = Runnable {
        mPostedHide = false
        mStartTime = -1
        visibility = View.GONE
    }

    private val mDelayedShow = Runnable {
        mPostedShow = false
        if (!mDismissed) {
            mStartTime = System.currentTimeMillis()
            visibility = View.VISIBLE
        }
    }

    private var mIndicator: Indicator? = null
    private var mIndicatorColor: Int = 0

    private var mShouldStartAnimationDrawable = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, 0)

        val indicatorName = a.getString(R.styleable.LoadingView_indicatorName)
        mIndicatorColor = a.getColor(R.styleable.LoadingView_indicatorColor, DEFAULT_COLOR)
        setIndicator(indicatorName)
        if (mIndicator == null) {
            setIndicator(DEFAULT_INDICATOR)
        }
        mMesureBaseOnLinear = a.getBoolean(R.styleable.LoadingView_lv_measure_linear, DEFAULT_BASE_ON_LINEAR)
        mShouldSquare = a.getBoolean(R.styleable.LoadingView_lv_should_square, DEFAULT_SHOULD_SQUARE)
        mPercent = a.getInt(R.styleable.LoadingView_lv_percent, DEFAULT_PERCENT)

        a.recycle()
    }

    private fun setIndicator(d: Indicator) {
        if (mIndicator != d) {
            if (mIndicator != null) {
                mIndicator!!.callback = null
                unscheduleDrawable(mIndicator)
            }

            mIndicator = d
            setIndicatorColor(mIndicatorColor)
            d.callback = this
            postInvalidate()
        }
    }

    private fun setIndicatorColor(color: Int) {
        this.mIndicatorColor = color
        mIndicator!!.setColor(color)
    }

    private fun setIndicator(indicatorName: String) {
        if (TextUtils.isEmpty(indicatorName)) {
            return
        }
        val drawableClassName = StringBuilder()
        drawableClassName.append(indicatorName)
        try {
            val drawableClass = Class.forName(drawableClassName.toString())
            val indicator = drawableClass.newInstance() as Indicator
            setIndicator(indicator)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Didn't find your class , check the name again !")
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    override fun verifyDrawable(drawable: Drawable): Boolean {
        return drawable === mIndicator || super.verifyDrawable(drawable)
    }

    private fun startAnimation() {
        if (visibility != View.VISIBLE) {
            return
        }

        mShouldStartAnimationDrawable = true
        postInvalidate()
    }

    private fun stopAnimation() {
        mIndicator!!.stop()
        mShouldStartAnimationDrawable = false
        postInvalidate()
    }

    override fun setVisibility(v: Int) {
        if (visibility != v) {
            super.setVisibility(v)
            if (v == View.GONE || v == View.INVISIBLE) {
                stopAnimation()
            } else {
                startAnimation()
            }
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }

    override fun invalidateDrawable(drawable: Drawable) {
        if (verifyDrawable(drawable)) {
            val dirty = drawable.bounds
            val scrollX = scrollX + paddingLeft
            val scrollY = scrollY + paddingTop

            invalidate(dirty.left + scrollX, dirty.top + scrollY,
                    dirty.right + scrollX, dirty.bottom + scrollY)
        } else {
            super.invalidateDrawable(drawable)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldWidth: Int, oldHeight: Int) {
        updateDrawableBounds(w, h)
    }

    private fun updateDrawableBounds(ww: Int, hh: Int) {
        var w = ww
        var h = hh
        w -= paddingRight + paddingLeft
        h -= paddingTop + paddingBottom

        var right = w
        var bottom = h
        var top = 0
        var left = 0

        if (mIndicator != null) {
            val intrinsicWidth = mIndicator!!.intrinsicWidth
            val intrinsicHeight = mIndicator!!.intrinsicHeight
            val intrinsicAspect = intrinsicWidth.toFloat() / intrinsicHeight
            val boundAspect = w.toFloat() / h
            if (intrinsicAspect != boundAspect) {
                if (boundAspect > intrinsicAspect) {
                    val width = (h * intrinsicAspect).toInt()
                    left = (w - width) / 2
                    right = left + width
                } else {
                    val height = (w * (1 / intrinsicAspect)).toInt()
                    top = (h - height) / 2
                    bottom = top + height
                }
            }
            mIndicator!!.setBounds(left, top, right, bottom)
        }
    }

    @Synchronized override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTrack(canvas)
    }

    private fun drawTrack(canvas: Canvas) {
        val d = mIndicator
        if (d != null) {
            val saveCount = canvas.save()

            canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

            d.draw(canvas)
            canvas.restoreToCount(saveCount)

            if (mShouldStartAnimationDrawable) {
                (d as Animatable).start()
                mShouldStartAnimationDrawable = false
            }
        }
    }

    @Synchronized override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mPercent == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val screenSize = if (mMesureBaseOnLinear)
            DisplayUtil.getDeviceWidth(context)
        else
            DisplayUtil.getDeviceHeight(context)
        val size = screenSize * mPercent / 100
        if (mMesureBaseOnLinear) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY),
                    if (mShouldSquare) View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY) else heightMeasureSpec)
        } else {
            super.onMeasure(if (mShouldSquare)
                View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
            else
                widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY))
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        updateDrawableState()
    }

    private fun updateDrawableState() {
        val state = drawableState
        if (mIndicator != null && mIndicator!!.isStateful) {
            mIndicator!!.state = state
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)

        if (mIndicator != null) {
            mIndicator!!.setHotspot(x, y)
        }
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