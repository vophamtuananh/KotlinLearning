package vophamtuananh.com.kotlinlearning

import android.os.Bundle
import com.vophamtuananh.base.activity.BaseActivity
import com.vophamtuananh.base.viewmodel.ActivityViewModel
import com.vophamtuananh.base.viewmodel.CommonView
import vophamtuananh.com.kotlinlearning.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, ActivityViewModel<CommonView>>() {

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding!!.tvHello.setOnClickListener {
            showError(Throwable("I Don't Know"))
        }
    }
}
