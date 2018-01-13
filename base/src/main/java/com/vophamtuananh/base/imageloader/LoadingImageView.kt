package com.vophamtuananh.base.imageloader

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

/**
 * Created by vophamtuananh on 1/12/18.
 */
class LoadingImageView : AppCompatImageView, ILoadingImageView {

    companion object {
        private const val FADE_IN_TIME = 400
    }

    private var transparentColor: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        transparentColor = ContextCompat.getColor(context, android.R.color.transparent)
    }

    override fun setResourceId(resourceId: Int) {
        setImageResource(resourceId)
    }

    override fun setDrawable(drawable: Drawable) {
        val transitionDrawable = TransitionDrawable(arrayOf(ColorDrawable(transparentColor), drawable))
        setImageDrawable(transitionDrawable)
        transitionDrawable.startTransition(FADE_IN_TIME)
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }

}