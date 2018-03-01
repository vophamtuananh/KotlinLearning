package com.vophamtuananh.base.widgets

import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import com.vophamtuananh.base.R

/**
 * Created by vophamtuananh on 3/1/18.
 */
class WithIconButton : AppCompatButton {

    private var mDrawableWidth: Int = 0

    private var mDrawableHeight: Int = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.WithIconButton, defStyleAttr, 0)

        mDrawableWidth = a.getDimensionPixelSize(R.styleable.WithIconButton_wibt_drawable_width, -1)
        mDrawableHeight = a.getDimensionPixelSize(R.styleable.WithIconButton_wibt_drawable_height, -1)

        if (mDrawableWidth > 0 || mDrawableHeight > 0) {
            initCompoundDrawableSize()
        }

        a.recycle()
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
                // save scale factor of image
                if (drawableWidth > mDrawableWidth) {
                    drawableWidth = mDrawableWidth.toFloat()
                    drawableHeight = drawableWidth * scaleFactor
                }
            }
            if (mDrawableHeight > 0) {
                // save scale factor of image

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