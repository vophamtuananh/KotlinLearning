package com.vophamtuananh.base.widgets

import android.animation.AnimatorInflater
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils
import android.util.AttributeSet
import com.vophamtuananh.base.R

/**
 * Created by vophamtuananh on 12/24/17.
 */
class AnimButton : AppCompatButton {

    companion object {
        private val DEFAULT_TYPE_FONT = "fonts/AbhayaLibre-Medium.ttf"
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimButton, defStyleAttr, 0)

        var typeFont: String = a.getString(R.styleable.AnimButton_ab_font)
        if (TextUtils.isEmpty(typeFont))
            typeFont = DEFAULT_TYPE_FONT

        a.recycle()

        val t: Typeface?
        t = try {
            Typeface.createFromAsset(getContext().assets, typeFont)
        } catch (e: Exception) {
            null
        }

        if (t != null)
            typeface = t

        init(context)
    }

    private fun init(context: Context) {
        this.stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.common_button_anim)
    }
}