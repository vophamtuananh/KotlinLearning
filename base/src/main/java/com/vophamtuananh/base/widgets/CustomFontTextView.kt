package com.vophamtuananh.base.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.support.v7.widget.AppCompatTextView
import android.text.TextUtils
import android.util.AttributeSet
import com.vophamtuananh.base.R

/**
 * Created by vophamtuananh on 12/24/17.
 */
class CustomFontTextView : AppCompatTextView {

    companion object {
        private val DEFAULT_TYPE_FONT = "fonts/AbhayaLibre-Medium.ttf"
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView, defStyleAttr, 0)

        var typeFont = a.getString(R.styleable.CustomFontTextView_cftv_font)
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
    }
}