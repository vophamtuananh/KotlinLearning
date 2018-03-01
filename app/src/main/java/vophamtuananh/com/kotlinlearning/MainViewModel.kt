package vophamtuananh.com.kotlinlearning

import android.util.Log
import com.vophamtuananh.base.viewmodel.ActivityViewModel
import com.vophamtuananh.base.viewmodel.CommonView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by vophamtuananh on 2/9/18.
 */
class MainViewModel : ActivityViewModel() {

    interface MainView : CommonView {
        fun onLoadedData(data: List<TestModel>)
    }

    override fun resume() {
        super.resume()
        loadData()
    }

    private fun loadData() {
        val view: MainView? = view() as MainView?
        if (view != null) {
            val observable = Observable.create<List<TestModel>> {
                Log.e("Create", Thread.currentThread().name)
                val imageList = arrayOf("http://13.250.58.213/assets/media/d33f2d8163704903bce4dcb109875b3737mmDa_1517477228.jpg",
                        "http://13.250.58.213/assets/media/pink-unicorn-faceumPhGD_1517476546.jpg",
                        "http://13.250.58.213/assets/media/depositphotos_64605493-stock-illustration-cute-kawaii-food-characters-cupcakeyKU8m9_1517476884.jpg")
                val dataList = ArrayList<TestModel>()
                (0..2).mapTo(dataList) { TestModel(it.toString(), imageList[it]) }
                it.onNext(dataList)
            }
            addDisposable(observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view.onLoadedData(it)
                    }, {

                    }))
        }
    }
}