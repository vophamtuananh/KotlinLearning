package com.vophamtuananh.base.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.vophamtuananh.base.R
import com.vophamtuananh.base.dialog.ConfirmDialog
import com.vophamtuananh.base.dialog.InformDialog
import com.vophamtuananh.base.dialog.LoadingDialog
import com.vophamtuananh.base.utils.DeviceUtil
import com.vophamtuananh.base.utils.DeviceUtil.Companion.CAMERA_REQUEST_CODE
import com.vophamtuananh.base.utils.DeviceUtil.Companion.GALLERY_REQUEST_CODE
import com.vophamtuananh.base.utils.DeviceUtil.Companion.PERMISSION_CALL_PHONE_REQUEST_CODE
import com.vophamtuananh.base.utils.DeviceUtil.Companion.PERMISSION_CAMERA_REQUEST_CODE
import com.vophamtuananh.base.utils.DeviceUtil.Companion.PERMISSION_LOCATION_REQUEST_CODE
import com.vophamtuananh.base.utils.DeviceUtil.Companion.PERMISSION_READ_EXTERNAL_REQUEST_CODE
import com.vophamtuananh.base.utils.DeviceUtil.Companion.PERMISSION_WRITE_STORAGE_REQUEST_CODE
import com.vophamtuananh.base.utils.FileUtil
import com.vophamtuananh.base.viewmodel.ActivityViewModel
import com.vophamtuananh.base.viewmodel.CommonView

/**
 * Created by vophamtuananh on 1/7/18.
 */
abstract class BaseActivity<B : ViewDataBinding, VM : ActivityViewModel> : AppCompatActivity(), CommonView {

    protected var mViewDataBinding: B? = null

    protected var mViewModel: VM? = null

    private var mCapturedPath: String? = null

    private var mCurrentPhoneNumber: String? = null

    private var mLoadingDialog: LoadingDialog? = null

    private var mInformDialog: InformDialog? = null

    private var mConfirmDialog: ConfirmDialog? = null

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    open protected fun getViewModelClass(): Class<VM>? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        if (getViewModelClass() != null)
            mViewModel = ViewModelProviders.of(this).get(getViewModelClass()!!)
        mViewModel?.onAttached(this)
    }

    override fun onPause() {
        super.onPause()
        mLoadingDialog?.dismiss()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mViewModel?.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        mViewModel?.onRestoreInstanceState(savedInstanceState)
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onDestroy() {
        mViewDataBinding?.unbind()
        super.onDestroy()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v != null && v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return try {
            super.dispatchTouchEvent(event)
        } catch (e: Exception) {
            true
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                onRejectedCameraPermission()
            }
        } else if (requestCode == PERMISSION_READ_EXTERNAL_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                onRejectedReadExternalPermission()
            }
        } else if (requestCode == PERMISSION_CALL_PHONE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCurrentPhoneNumber?.let { callToPhoneNumber(it) }
            } else {
                onRejectedPhoneCallPermission()
            }
        } else if (requestCode == PERMISSION_WRITE_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onAgreedWriteExternal()
            } else {
                onRejectedWriteExternalPermission()
            }
        } else if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onAgreedLocationPermission()
            } else {
                onRejectedLocationPermission()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                if (mCapturedPath != null)
                    onCapturedImage(mCapturedPath!!)
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                if (data != null) {
                    val selectedImage = data.data
                    onChoseImage(selectedImage)
                }
            }
        } else if (requestCode == GALLERY_REQUEST_CODE) {
            onChoseNoImage()
        }
    }

    override fun showLoading() {
        showLoadingDialog()
    }

    override fun hideLoading() {
        hideLoadingDialog()
    }

    override fun showError(throwable: Throwable) {
        mInformDialog = getInformDialog()
        if (!mInformDialog!!.isShowing) {
            mInformDialog?.show(informType = InformDialog.InformType.WARNING, description = getThrowableMessage(throwable))
        }
    }

    open protected fun getThrowableMessage(e: Throwable): String {
        return getString(R.string.unknown_error)
    }

    fun openCamera(fileName: String? = null) {
        val tempFile = FileUtil.getOutputMediaFile(applicationContext, fileName)
        tempFile?.let {
            mCapturedPath = tempFile.absolutePath
            DeviceUtil.openCamera(this, tempFile)
        }
    }

    fun openGallery() {
        DeviceUtil.openGallery(this)
    }

    fun callToPhoneNumber(phoneNumber: String) {
        val telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simState = telMgr.simState
        if (simState == TelephonyManager.SIM_STATE_ABSENT) {
            return
        }
        mCurrentPhoneNumber = phoneNumber
        DeviceUtil.callToPhoneNumber(this, mCurrentPhoneNumber!!)
    }

    open protected fun onCapturedImage(path: String) {}

    open protected fun onChoseImage(uri: Uri) {}

    open protected fun onChoseNoImage() {}

    open protected fun onRejectedCameraPermission() {}

    open protected fun onRejectedReadExternalPermission() {}

    open protected fun onRejectedPhoneCallPermission() {}

    open protected fun onAgreedWriteExternal() {}

    open protected fun onRejectedWriteExternalPermission() {}

    open protected fun onAgreedLocationPermission() {}

    open protected fun onRejectedLocationPermission() {}

    fun showLoadingDialog(onLoadingDialogListener: LoadingDialog.OnLoadingDialogListener? = null) {
        if (mLoadingDialog == null)
            mLoadingDialog = LoadingDialog(this)

        if (mLoadingDialog!!.isShowing)
            return

        mLoadingDialog?.showWithListener(onLoadingDialogListener)
    }

    fun hideLoadingDialog() {
        mLoadingDialog?.dismiss()
    }

    fun getInformDialog(): InformDialog {
        if (mInformDialog == null)
            mInformDialog = InformDialog(this)
        return mInformDialog!!
    }

    fun getConfirmDialog(): ConfirmDialog {
        if (mConfirmDialog == null)
            mConfirmDialog = ConfirmDialog(this)
        return mConfirmDialog!!
    }
}