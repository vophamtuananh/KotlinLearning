package com.vophamtuananh.base.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import com.vophamtuananh.base.R
import com.vophamtuananh.base.databinding.DialogInformBinding

/**
 * Created by vophamtuananh on 1/7/18.
 */
class InformDialog(context: Context) : BaseDialog<DialogInformBinding>(context) {

    private var mOnConfirmListener: OnConfirmListener? = null
    private var mTitle: String? = null
    private var mDescription: String? = null
    private var mButtonText: String? = null
    private var mInformType = InformType.ERROR
    private var mTag: String? = null
    
    override fun getLayoutId(): Int {
        return R.layout.dialog_inform
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        mViewDataBinding!!.event = this
    }

    override fun onStart() {
        super.onStart()
        setupTexts()
        setupBackgrouns()
    }

    private fun setupTexts() {
        mViewDataBinding!!.tvDescription.text = mDescription
        mViewDataBinding!!.btnOk.text = mButtonText
        if (!TextUtils.isEmpty(mTitle)) {
            mViewDataBinding!!.tvTitle.text = mTitle
            return
        }
        val title = when (mInformType) {
            InformDialog.InformType.SUCCESS -> context.getString(R.string.success)
            InformDialog.InformType.WARNING -> context.getString(R.string.warning)
            else -> context.getString(R.string.error)
        }
        mViewDataBinding!!.tvTitle.text = title
    }

    override fun dismiss() {
        mOnConfirmListener = null
        super.dismiss()
    }

    fun onConfirmClicked() {
        if (mOnConfirmListener != null)
            mOnConfirmListener!!.onConfirmed(mTag)
        dismiss()
    }

    private fun setupBackgrouns() {
        val backgroundId: Int
        val iconId: Int
        when (mInformType) {
            InformDialog.InformType.SUCCESS -> {
                backgroundId = R.drawable.bg_success
                iconId = R.drawable.ic_success
            }
            InformDialog.InformType.WARNING -> {
                backgroundId = R.drawable.bg_warning
                iconId = R.drawable.ic_warning
            }
            else -> {
                backgroundId = R.drawable.bg_error
                iconId = R.drawable.ic_error
            }
        }
        mViewDataBinding!!.llParent.setBackgroundResource(backgroundId)
        mViewDataBinding!!.btnOk.setBackgroundResource(backgroundId)
        mViewDataBinding!!.ivIcon.setImageResource(iconId)
    }

    fun show(onConfirmListener: OnConfirmListener? = null, title: String? = null,
             description: String = "", informType: InformType = InformType.WARNING,
             buttonText: String = context.getString(R.string.yes), tag: String? = null,
             cancelable: Boolean = true) {
        mOnConfirmListener = onConfirmListener
        mTitle = title
        mDescription = description
        mInformType = informType
        mButtonText = buttonText
        mTag = tag
        setCancelable(cancelable)
        super.show()
    }

    interface OnConfirmListener {
        fun onConfirmed(tag: String?)
    }

    enum class InformType {
        SUCCESS, ERROR, WARNING
    }
}