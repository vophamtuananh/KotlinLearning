package com.vophamtuananh.base.activity

import android.databinding.ViewDataBinding
import android.os.Bundle
import com.vophamtuananh.base.viewmodel.ActivityViewModel

/**
 * Created by vophamtuananh on 1/7/18.
 */
abstract class BaseInjectingActivity<B : ViewDataBinding, VM : ActivityViewModel, Component> : BaseActivity<B, VM>() {

    protected var mComponent: Component? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mComponent = createComponent()
        mComponent?.let { onInject(it) }
    }

    protected abstract fun createComponent(): Component?

    protected abstract fun onInject(component: Component)

}