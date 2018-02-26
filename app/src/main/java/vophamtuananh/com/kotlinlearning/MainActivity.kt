package vophamtuananh.com.kotlinlearning

import android.os.Bundle
import com.vophamtuananh.base.activity.BaseActivity
import vophamtuananh.com.kotlinlearning.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainViewModel.MainView {
    override fun onTest() {
        showError(Throwable("I Don't Know"))
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getViewModelClass(): Class<MainViewModel>? {
        return MainViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding?.viewModel = mViewModel
    }
}
