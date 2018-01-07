package com.vophamtuananh.base.fragment

import java.lang.ref.WeakReference

/**
 * Created by vophamtuananh on 1/7/18.
 */
class WaitThread(fragment: BaseFragment<*, *>) : Thread() {

    private var fragmentWeak: WeakReference<BaseFragment<*, *>>? = null
    private var isStopped: Boolean = false
    private val mObject = Object()

    init {
        fragmentWeak = WeakReference(fragment)
    }

    override fun run() {
        val fragment = fragmentWeak!!.get()
        if (fragment != null) {
            while (!fragment.isViewCreated() && !isStopped) {
                try {
                    synchronized(mObject) {
                        mObject.wait()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }

            if (isStopped)
                return

            val finalFragment = fragmentWeak!!.get()
            if (finalFragment != null) {
                finalFragment.activity!!.runOnUiThread {
                    if (finalFragment.isInitialized())
                        finalFragment.initialized()
                    else
                        finalFragment.handleAfterVisible()
                }
            }
        }
    }

    fun continueProcessing() {
        synchronized(mObject) {
            mObject.notifyAll()
        }
    }

    fun stopProcessing() {
        isStopped = true
        synchronized(mObject) {
            mObject.notifyAll()
        }
    }
}

