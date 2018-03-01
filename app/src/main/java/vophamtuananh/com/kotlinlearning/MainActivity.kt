package vophamtuananh.com.kotlinlearning

import android.os.Bundle
import com.vophamtuananh.base.activity.BaseActivity
import com.vophamtuananh.base.imageloader.ImageLoader
import vophamtuananh.com.kotlinlearning.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainViewModel.MainView {

    private lateinit var mainAdapter: MainAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getViewModelClass(): Class<MainViewModel>? {
        return MainViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding?.viewModel = mViewModel
        mainAdapter = MainAdapter(ImageLoader(this))
        mViewDataBinding?.rvTest?.adapter = mainAdapter
    }

    override fun onLoadedData(data: List<TestModel>) {
        mainAdapter.update(data)
    }
}
