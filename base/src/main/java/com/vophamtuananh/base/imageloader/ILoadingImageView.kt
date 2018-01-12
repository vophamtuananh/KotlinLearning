package com.vophamtuananh.base.imageloader

import android.graphics.drawable.Drawable

/**
 * Created by vophamtuananh on 1/12/18.
 */
interface ILoadingImageView {

    fun setResourceId(resourceId: Int)

    fun setDrawable(drawable: Drawable)

    fun showLoading()

    fun hideLoading()

}