package com.vophamtuananh.base.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import com.vophamtuananh.base.R
import com.vophamtuananh.base.databinding.DialogConfirmBinding

/**
 * Created by vophamtuananh on 1/7/18.
 */
class ConfirmDialog(context: Context) : BaseDialog<DialogConfirmBinding>(context) {

    private var mOnYesListener: OnYesListener? = null
    private var mOnNoListener: OnNoListener? = null
    private var mTitle: String? = null
    private var mDescription: String? = null
    private var mYesButtonText: String? = null
    private var mNoButtonText: String? = null
    private var mConfirmType = ConfirmType.WARNING
    private var mTag: String? = null

    override fun getLayoutId(): Int {
        return R.layout.dialog_confirm
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding?.event = this
    }

    override fun onStart() {
        super.onStart()
        setupTexts()
        setupBackgrouns()
    }

    override fun dismiss() {
        mOnYesListener = null
        mOnNoListener = null
        super.dismiss()
    }

    fun onYesCliked() {
        mOnYesListener?.onChoseYes(mTag)
        dismiss()
    }

    fun onNoCliked() {
        mOnNoListener?.onChoseNo(mTag)
        dismiss()
    }

    private fun setupTexts() {
        mViewDataBinding?.tvDescription?.text = mDescription
        mViewDataBinding?.btnYes?.text = mYesButtonText
        mViewDataBinding?.btnNo?.text = mNoButtonText
        if (!TextUtils.isEmpty(mTitle)) {
            mViewDataBinding?.tvTitle?.text = mTitle
            return
        }
        val title = when (mConfirmType) {
            ConfirmDialog.ConfirmType.ERROR -> context.getString(R.string.error)
            ConfirmDialog.ConfirmType.SUCCESS -> context.getString(R.string.success)
            else -> context.getString(R.string.warning)
        }
        mViewDataBinding?.tvTitle?.text = title
    }

    private fun setupBackgrouns() {
        val backgroundParentId: Int
        val backgroundYestId: Int
        val backgroundNoId: Int
        val iconId: Int
        when (mConfirmType) {
            ConfirmDialog.ConfirmType.ERROR -> {
                backgroundParentId = R.drawable.bg_error
                backgroundYestId = R.drawable.bg_success
                backgroundNoId = R.drawable.bg_error
                iconId = R.drawable.ic_error
            }
            ConfirmDialog.ConfirmType.SUCCESS -> {
                backgroundParentId = R.drawable.bg_success
                backgroundYestId = R.drawable.bg_success
                backgroundNoId = R.drawable.bg_error
                iconId = R.drawable.ic_success
            }
            else -> {
                backgroundParentId = R.drawable.bg_warning
                backgroundYestId = R.drawable.bg_success
                backgroundNoId = R.drawable.bg_warning
                iconId = R.drawable.ic_warning
            }
        }
        mViewDataBinding?.clParent?.setBackgroundResource(backgroundParentId)
        mViewDataBinding?.btnYes?.setBackgroundResource(backgroundYestId)
        mViewDataBinding?.btnNo?.setBackgroundResource(backgroundNoId)
        mViewDataBinding?.ivIcon?.setImageResource(iconId)
    }

    fun show(onYesListener: OnYesListener? = null, onNoListener: OnNoListener? = null,
             title: String? = null, description: String = "",
             confirmType: ConfirmType = ConfirmType.WARNING, yesText: String = context.getString(R.string.yes),
             noText: String = context.getString(R.string.yes), tag: String? = null, cancelable: Boolean = true) {
        mOnYesListener = onYesListener
        mOnNoListener = onNoListener
        mTitle = title
        mDescription = description
        mConfirmType = confirmType
        mYesButtonText = yesText
        mNoButtonText = noText
        mTag = tag
        setCancelable(cancelable)
        super.show()
    }

    interface OnYesListener {
        fun onChoseYes(tag: String?)
    }

    interface OnNoListener {
        fun onChoseNo(tag: String?)
    }

    enum class ConfirmType {
        SUCCESS, ERROR, WARNING
    }
}