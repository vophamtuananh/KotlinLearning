package com.vophamtuananh.base.activity

import android.databinding.ViewDataBinding
import android.os.Bundle
import com.vophamtuananh.base.viewmodel.ActivityViewModel
import com.vophamtuananh.base.viewmodel.CommonView

/**
 * Created by vophamtuananh on 1/7/18.
 */
abstract class BaseInjectingActivity<B : ViewDataBinding, VM : ActivityViewModel, Component> : BaseActivity<B, VM>() {

    private var mComponent : Component? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mComponent = createComponent()
        if (mComponent != null)
            onInject(mComponent!!)
    }

    protected abstract fun createComponent(): Component

    protected abstract fun onInject(component: Component)

}