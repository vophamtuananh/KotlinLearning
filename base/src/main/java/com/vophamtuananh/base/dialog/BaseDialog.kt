package com.vophamtuananh.base.dialog

import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import com.vophamtuananh.base.R

/**
 * Created by vophamtuananh on 1/7/18.
 */
abstract class BaseDialog<T : ViewDataBinding>(context: Context) : Dialog(context) {

    protected var mViewDataBinding: T? = null

    protected abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutId(), null, false)
        setContentView(mViewDataBinding!!.root)
        window!!.setWindowAnimations(R.style.DialogTheme)
    }

    override fun dismiss() {
        super.dismiss()
        setOnCancelListener(null)
    }

}