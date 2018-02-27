package com.vophamtuananh.base.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import com.vophamtuananh.base.R
import com.vophamtuananh.base.utils.DisplayUtil

/**
 * Created by vophamtuananh on 1/3/18.
 */
class LoadingView : ProgressBar {

    companion object {

        private val DEFAULT_BASE_ON_LINEAR = true
        private val DEFAULT_SHOULD_SQUARE = false
        private val DEFAULT_PERCENT = 0

    }

    private var mMeasureBaseOnLinear: Boolean = false
    private var mShouldSquare: Boolean = false
    private var mPercent: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, 0)

        mMeasureBaseOnLinear = a.getBoolean(R.styleable.LoadingView_lv_measure_linear, DEFAULT_BASE_ON_LINEAR)
        mShouldSquare = a.getBoolean(R.styleable.LoadingView_lv_should_square, DEFAULT_SHOULD_SQUARE)
        mPercent = a.getInt(R.styleable.LoadingView_lv_percent, DEFAULT_PERCENT)

        a.recycle()
    }

    @Synchronized override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mPercent == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val screenSize = if (mMeasureBaseOnLinear)
            DisplayUtil.getDeviceWidth(context)
        else
            DisplayUtil.getDeviceHeight(context)
        val size = screenSize * mPercent / 100
        if (mMeasureBaseOnLinear) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY),
                    if (mShouldSquare) View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY) else heightMeasureSpec)
        } else {
            super.onMeasure(if (mShouldSquare)
                View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
            else
                widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY))
        }
    }
}