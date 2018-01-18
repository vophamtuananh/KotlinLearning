package com.vophamtuananh.base.fragment

import android.support.v4.app.FragmentManager
import java.util.*

/**
 * Created by vophamtuananh on 1/7/18.
 */
open class BaseFragmentHelper<in T : BaseFragment<*, *>>(private var mOnChangedFragmentListener: OnChangedFragmentListener? = null,
                                                         fragmentProvider: FragmentProvider<T>,
                                                         shouldShowPosition: Int) {

    private var mPageList: ArrayList<Stack<T>>? = null
    private var mPageIndex: Int = 0
    private var mLayoutId: Int = 0
    private val mFragmentManager: FragmentManager

    private val mBuildFragments: Array<T>

    init {
        mLayoutId = fragmentProvider.getContentLayoutId()
        mFragmentManager = fragmentProvider.fragmentManager()
        mBuildFragments = fragmentProvider.getFragments()
        initFragments(mBuildFragments, shouldShowPosition)
    }

    private fun initFragments(fragments: Array<T>, shouldShowPosition: Int) {
        mPageList = ArrayList(fragments.size)

        for (fragment in fragments) {
            val stack = Stack<T>()
            stack.push(fragment)
            mPageList!!.add(stack)
        }

        val fragment = mPageList!![mPageIndex].peek()
        if (fragment.isAdded || fragment.isDetached || fragment.isHidden) {
            showFragment(mPageIndex)
        } else {
            mOnChangedFragmentListener?.onChangedFragment(fragment)
            val transaction = mFragmentManager.beginTransaction()
            transaction.add(mLayoutId, fragment)
            transaction.commitAllowingStateLoss()
        }
        showFragment(shouldShowPosition)
    }

    fun pushFragment(fragment: T) {
        val currentStack = mPageList!![mPageIndex]
        if (currentStack.peek().getTagName() == fragment.getTagName())
            return

        val hideFragment = currentStack.peek()
        currentStack.push(fragment)
        mOnChangedFragmentListener?.onChangedFragment(fragment)

        fragment.setCurrentScreen(true)
        fragment.setPush(true)
        hideFragment.setCurrentScreen(true)
        hideFragment.setPush(true)

        val transaction = mFragmentManager.beginTransaction()

        if (hideFragment.isShouldSave())
            transaction.hide(hideFragment)
        else
            transaction.detach(hideFragment)

        transaction.add(mLayoutId, fragment)
        transaction.commitAllowingStateLoss()

    }

    fun popFragment(): Boolean {
        return popFragment(1)
    }

    fun popFragmentToRoot(): Boolean {
        val level = mPageList!![mPageIndex].size - 1
        return popFragment(level)
    }

    private fun popFragment(l: Int): Boolean {
        var level = l
        if (level <= 0) return false
        if (mPageList!![mPageIndex].size <= level) return false
        val transaction = mFragmentManager.beginTransaction()

        while (level >= 1) {
            val fragment = mPageList!![mPageIndex].pop()
            fragment.setCurrentScreen(true)
            fragment.setPush(false)
            transaction.remove(fragment)
            level--
        }
        val showFragment = mPageList!![mPageIndex].peek()

        mOnChangedFragmentListener?.onChangedFragment(showFragment)

        showFragment.setCurrentScreen(true)
        showFragment.setPush(false)

        if (showFragment.isHidden)
            transaction.show(showFragment)
        else if (showFragment.isDetached)
            transaction.attach(showFragment)

        transaction.commitAllowingStateLoss()
        return true
    }


    fun showFragment(index: Int) {
        if (index == mPageIndex) return
        val showFragment = mPageList!![mPageIndex].peek()
        val hideFragment = mPageList!![mPageIndex].peek()
        val transaction = mFragmentManager.beginTransaction()

        if (mPageIndex > index) {
            showFragment.setInLeft(true)
            hideFragment.setOutLeft(false)
        } else {
            showFragment.setInLeft(false)
            hideFragment.setOutLeft(true)
        }
        showFragment.setCurrentScreen(false)
        hideFragment.setCurrentScreen(false)
        mPageIndex = index

        if (showFragment.isAdded) {
            if (showFragment.isDetached)
                transaction.attach(showFragment)
            else if (showFragment.isHidden)
                transaction.show(showFragment)
        } else {
            transaction.add(mLayoutId, showFragment)
        }

        if (hideFragment.isShouldSave())
            transaction.hide(hideFragment)
        else
            transaction.detach(hideFragment)

        transaction.commitAllowingStateLoss()
        mOnChangedFragmentListener?.onChangedFragment(showFragment)
    }

    fun release() {
        mOnChangedFragmentListener = null
    }

    fun getCurrentFragment(): BaseFragment<*, *> {
        return mPageList!![mPageIndex].peek()
    }

    interface OnChangedFragmentListener {
        fun onChangedFragment(fragment: BaseFragment<*, *>)
    }
}