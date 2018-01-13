package com.vophamtuananh.base.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.vophamtuananh.base.R
import com.vophamtuananh.base.databinding.DialogLoadingBinding

/**
 * Created by vophamtuananh on 1/7/18.
 */
class LoadingDialog(context: Context) : BaseDialog<DialogLoadingBinding>(context) {

    private var mOnLoadingDialogListener : OnLoadingDialogListener? = null

    override fun getLayoutId(): Int {
        return R.layout.dialog_loading
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
    }

    override fun onStart() {
        super.onStart()
        mViewDataBinding!!.lvLoading.visibility = View.VISIBLE
    }

    override fun onStop() {
        mViewDataBinding!!.lvLoading.visibility = View.INVISIBLE
        super.onStop()
    }

    override fun dismiss() {
        if (mOnLoadingDialogListener != null)
            mOnLoadingDialogListener!!.onDismissed()
        super.dismiss()
    }

    fun showWithListener(onLoadingDialogListener: OnLoadingDialogListener? = null) {
        mOnLoadingDialogListener = onLoadingDialogListener
        show()
    }

    interface OnLoadingDialogListener {
        fun onDismissed()
    }
}