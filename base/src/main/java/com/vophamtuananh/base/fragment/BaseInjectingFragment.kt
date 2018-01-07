package com.vophamtuananh.base.fragment

import android.content.Context
import android.databinding.ViewDataBinding
import com.vophamtuananh.base.viewmodel.CommonView
import com.vophamtuananh.base.viewmodel.FragmentViewModel

/**
 * Created by vophamtuananh on 1/7/18.
 */
abstract class BaseInjectingFragment<B : ViewDataBinding, VM : FragmentViewModel<CommonView>, Component> : BaseFragment<B, VM>() {

    private var mComponent: Component? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mComponent = createComponent()
        if (mComponent != null)
            onInject(mComponent!!)
    }


    protected abstract fun createComponent(): Component

    protected abstract fun onInject(component: Component)

}