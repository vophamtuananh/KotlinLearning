package com.vophamtuananh.base.widgets

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import com.vophamtuananh.base.R

/**
 * Created by vophamtuananh on 12/24/17.
 */
class WithIconTextView : AppCompatTextView {

    private var mDrawableWidth = 0
    private var mDrawableHeight = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.WithIconTextView, defStyleAttr, 0)

        val symbol = a.getString(R.styleable.WithIconTextView_witv_symbol)
        val symbolColor = a.getColor(R.styleable.WithIconTextView_witv_symbol_color, ContextCompat.getColor(context, R.color.colorBlack))

        mDrawableWidth = a.getDimensionPixelSize(R.styleable.WithIconTextView_witv_drawable_width, -1)
        mDrawableHeight = a.getDimensionPixelSize(R.styleable.WithIconTextView_witv_drawable_height, -1)

        a.recycle()

        if (mDrawableWidth > 0 || mDrawableHeight > 0) {
            initCompoundDrawableSize()
        }

        if (!TextUtils.isEmpty(symbol)) {
            val builder = SpannableStringBuilder(text.toString() + " " + symbol)

            builder.setSpan(ForegroundColorSpan(symbolColor), text.length, builder.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            text = builder
        }
    }

    private fun initCompoundDrawableSize() {
        val drawables = compoundDrawablesRelative
        for (drawable in drawables) {
            if (drawable == null) {
                continue
            }

            val realBounds = drawable.bounds
            val scaleFactor = realBounds.height() / realBounds.width().toFloat()

            var drawableWidth = realBounds.width().toFloat()
            var drawableHeight = realBounds.height().toFloat()

            if (mDrawableWidth > 0) {
                if (drawableWidth > mDrawableWidth) {
                    drawableWidth = mDrawableWidth.toFloat()
                    drawableHeight = drawableWidth * scaleFactor
                }
            }
            if (mDrawableHeight > 0) {
                if (drawableHeight > mDrawableHeight) {
                    drawableHeight = mDrawableHeight.toFloat()
                    drawableWidth = drawableHeight / scaleFactor
                }
            }

            realBounds.right = realBounds.left + Math.round(drawableWidth)
            realBounds.bottom = realBounds.top + Math.round(drawableHeight)

            drawable.bounds = realBounds
        }
        setCompoundDrawablesRelative(drawables[0], drawables[1], drawables[2], drawables[3])
    }
}