package com.vophamtuananh.base.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.vophamtuananh.base.R
import com.vophamtuananh.base.activity.BaseActivity
import com.vophamtuananh.base.viewmodel.CommonView
import com.vophamtuananh.base.viewmodel.FragmentViewModel

/**
 * Created by vophamtuananh on 1/7/18.
 */
abstract class BaseFragment<B : ViewDataBinding, VM : FragmentViewModel> : Fragment(), CommonView {

    protected var mViewDataBinding: B? = null

    protected var mViewModel: VM? = null

    private var mIsInLeft: Boolean = false
    private var mIsOutLeft: Boolean = false
    private var mIsCurrentScreen: Boolean = false
    private var mIsPush: Boolean = false

    private var mInitialized = true
    private var mViewCreated = false
    private var mViewDestroyed = false

    private var mWaitThread: WaitThread? = null

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    open protected fun getViewModelClass(): Class<VM>? {
        return null
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (getViewModelClass() != null)
            mViewModel = ViewModelProviders.of(this).get(getViewModelClass()!!)
            mViewModel?.onAttach(this)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
        val animation: Animation
        if (mIsCurrentScreen) {
            val popExit = getPopExitAnimId()
            val pushEnter = getPushEnterAnimId()
            val pushExit = getPushExitAnimId()
            val popEnter = getPopEnterAnimId()
            animation = if (mIsPush)
                AnimationUtils.loadAnimation(context, if (enter) pushEnter else pushExit)
            else
                AnimationUtils.loadAnimation(context, if (enter) popEnter else popExit)
        } else {
            if (enter) {
                val left = getLeftInAnimId()
                val right = getRightInAnimId()
                if (mIsInLeft) {
                    if (left == 0) {
                        animation = AlphaAnimation(1f, 1f)
                        animation.setDuration(resources.getInteger(R.integer.animation_time_full).toLong())
                    } else {
                        animation = AnimationUtils.loadAnimation(context, left)
                    }
                } else {
                    if (right == 0) {
                        animation = AlphaAnimation(1f, 1f)
                        animation.setDuration(resources.getInteger(R.integer.animation_time_full).toLong())
                    } else {
                        animation = AnimationUtils.loadAnimation(context, right)
                    }
                }
            } else {
                val left = getLeftOutAnimId()
                val right = getRightOutAnimId()
                animation = AnimationUtils.loadAnimation(context, if (mIsOutLeft) left else right)
            }
        }

        if (enter) {
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    if (mViewDestroyed)
                        return
                    mWaitThread = WaitThread(this@BaseFragment)
                    mWaitThread?.start()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        return animation
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return mViewDataBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onVisible()
        mViewCreated = true
        mViewDestroyed = false
        mWaitThread?.continueProcessing()
    }

    override fun onDestroyView() {
        mWaitThread?.stopProcessing()
        mViewDestroyed = true
        mViewCreated = false
        onInVisible()
        mViewDataBinding?.unbind()
        super.onDestroyView()
    }

    override fun showLoading() {
        val activity = activity
        if (activity != null && activity is BaseActivity<*, *>) {
            activity.showLoading()
        }
    }

    override fun hideLoading() {
        val activity = activity
        if (activity != null && activity is BaseActivity<*, *>) {
            activity.hideLoading()
        }
    }

    override fun showError(throwable: Throwable) {
        val activity = activity
        if (activity != null && activity is BaseActivity<*, *>) {
            activity.showError(throwable)
        }
    }

    fun getTagName(): String {
        return javaClass.simpleName
    }

    fun isInitialized(): Boolean {
        return mInitialized
    }

    open fun initialized() {
        mInitialized = false
        mViewModel?.onInitialized()
    }

    fun isViewCreated(): Boolean {
        return mViewCreated
    }

    fun setInLeft(inLeft: Boolean) {
        mIsInLeft = inLeft
    }

    fun setOutLeft(outLeft: Boolean) {
        mIsOutLeft = outLeft
    }

    fun setCurrentScreen(currentScreen: Boolean) {
        mIsCurrentScreen = currentScreen
    }

    fun setPush(push: Boolean) {
        mIsPush = push
    }

    open fun isShouldSave(): Boolean {
        return true
    }

    open protected fun onVisible() {}

    open fun handleAfterVisible() {}

    open protected fun onInVisible() {}

    open protected fun onCapturedImage(path: String) {}

    open protected fun onChoseImage(uri: Uri) {}

    open protected fun getPushExitAnimId(): Int {
        return R.anim.push_exit
    }

    open protected fun getPopEnterAnimId(): Int {
        return R.anim.pop_enter
    }

    open protected fun getPopExitAnimId(): Int {
        return R.anim.pop_exit
    }

    open protected fun getPushEnterAnimId(): Int {
        return R.anim.push_enter
    }

    open protected fun getLeftInAnimId(): Int {
        return R.anim.slide_in_left
    }

    open protected fun getRightInAnimId(): Int {
        return R.anim.slide_in_right
    }

    open protected fun getLeftOutAnimId(): Int {
        return R.anim.slide_out_left
    }

    open protected fun getRightOutAnimId(): Int {
        return R.anim.slide_out_right
    }
}