package vophamtuananh.com.kotlinlearning

import com.vophamtuananh.base.viewmodel.ActivityViewModel
import com.vophamtuananh.base.viewmodel.CommonView

/**
 * Created by vophamtuananh on 2/9/18.
 */
class MainViewModel : ActivityViewModel() {

    interface MainView : CommonView {
        fun onTest()
    }

    public fun test() {
        val view = view() as MainView
        view.onTest()
    }
}