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
    }

    override fun dismiss() {
        mOnYesListener = null
        mOnNoListener = null
        super.dismiss()
    }

    private fun setupTexts() {
        mViewDataBinding?.tvDescription?.text = if (!TextUtils.isEmpty(mDescription)) mDescription else ""
        mViewDataBinding?.btnYes?.text = if (!TextUtils.isEmpty(mYesButtonText)) mYesButtonText else context.getString(R.string.yes)
        mViewDataBinding?.btnNo?.text = if (!TextUtils.isEmpty(mNoButtonText)) mNoButtonText else context.getString(R.string.cancel)
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

    fun showWithYesListener(onYesListener: OnYesListener) {
        mOnYesListener = onYesListener
        super.show()
    }

    fun showWithNoListener(onNoListener: OnNoListener) {
        mOnNoListener = onNoListener
        super.show()
    }

    fun showWithListener(onYesListener: OnYesListener, onNoListener: OnNoListener) {
        mOnYesListener = onYesListener
        mOnNoListener = onNoListener
        super.show()
    }

    fun setTitle(title: String): ConfirmDialog {
        mTitle = title
        return this
    }

    fun setDescription(description: String): ConfirmDialog {
        mDescription = description
        return this
    }

    fun setConfirmType(confirmType: ConfirmType): ConfirmDialog {
        mConfirmType = confirmType
        return this
    }

    fun setYesButtonText(yesText: String): ConfirmDialog {
        mYesButtonText = yesText
        return this
    }

    fun setNoButtonText(noText: String): ConfirmDialog {
        mNoButtonText = noText
        return this
    }

    fun setInformType(confirmType: ConfirmType): ConfirmDialog {
        mConfirmType = confirmType
        return this
    }

    fun setTag(tag: String): ConfirmDialog {
        mTag = tag
        return this
    }

    fun setCancelWhenTapOutSide(cancelable: Boolean): ConfirmDialog {
        setCancelable(cancelable)
        return this
    }

    fun onYesCliked() {
        mOnYesListener?.onChoseYes(mTag)
        dismiss()
    }

    fun onNoCliked() {
        mOnNoListener?.onChoseNo(mTag)
        dismiss()
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