package com.vophamtuananh.base.widgets

import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.View
import com.vophamtuananh.base.R
import com.vophamtuananh.base.utils.DisplayUtil

/**
 * Created by vophamtuananh on 12/24/17.
 */
class PercentLinearLayout : LinearLayoutCompat {

    companion object {
        private val DEFAULT_BASE_ON_LINEAR = true
        private val DEFAULT_SHOULD_SQUARE = false
        private val DEFAULT_PERCENT = 0
    }

    private var mMeasureBaseOnLinear: Boolean = DEFAULT_BASE_ON_LINEAR
    private var mShouldSquare: Boolean = DEFAULT_SHOULD_SQUARE
    private var mPercent: Int = DEFAULT_PERCENT

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.PercentLinearLayout, defStyleAttr, 0)

        mMeasureBaseOnLinear = a.getBoolean(R.styleable.PercentLinearLayout_pll_measure_linear, DEFAULT_BASE_ON_LINEAR)
        mShouldSquare = a.getBoolean(R.styleable.PercentLinearLayout_pll_should_square, DEFAULT_SHOULD_SQUARE)
        mPercent = a.getInt(R.styleable.PercentLinearLayout_pll_percent, DEFAULT_PERCENT)

        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
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