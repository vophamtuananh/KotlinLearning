package com.vophamtuananh.base.viewmodel

import android.arch.lifecycle.LifecycleOwner

/**
 * Created by vophamtuananh on 1/7/18.
 */
interface CommonView : LifecycleOwner {

    fun showLoading()

    fun showError(throwable: Throwable)

    fun hideLoading()
}