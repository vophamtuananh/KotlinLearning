package com.vophamtuananh.base.widgets

import android.animation.AnimatorInflater
import android.content.Context
import android.content.res.TypedArray
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils
import android.util.AttributeSet
import com.vophamtuananh.base.R

/**
 * Created by vophamtuananh on 12/24/17.
 */
class AnimButton : AppCompatButton {

    private var mDrawableWidth = 0

    private var mDrawableHeight = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimButton, defStyleAttr, 0)

        mDrawableWidth = a.getDimensionPixelSize(R.styleable.AnimButton_ab_drawable_width, -1)
        mDrawableHeight = a.getDimensionPixelSize(R.styleable.AnimButton_ab_drawable_height, -1)

        if (mDrawableWidth > 0 || mDrawableHeight > 0) {
            initCompoundDrawableSize()
        }

        init(context)

        a.recycle()
    }

    private fun init(context: Context) {
        this.stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.common_button_anim)
    }

    private fun initCompoundDrawableSize() {
        val drawables = compoundDrawables
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
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3])
    }
}