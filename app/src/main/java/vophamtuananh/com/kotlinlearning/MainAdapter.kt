package vophamtuananh.com.kotlinlearning

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vophamtuananh.base.imageloader.ImageLoader
import com.vophamtuananh.base.recyclerview.RecyclerAdapter
import vophamtuananh.com.kotlinlearning.databinding.ItemTestBinding

/**
 * Created by vophamtuananh on 3/1/18.
 */
class MainAdapter(private val mImageLoader: ImageLoader) : RecyclerAdapter<MainAdapter.MainHolder, TestModel>() {

    override fun getViewHolder(parent: ViewGroup, viewType: Int): MainHolder? {
        val itemTestBinding = ItemTestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(itemTestBinding, mImageLoader)
    }

    class MainHolder(itemTestBinding: ItemTestBinding, private val mImageLoader: ImageLoader) :
            RecyclerAdapter.BaseHolder<ItemTestBinding, TestModel>(itemTestBinding) {

        override fun bindData(data: TestModel) {
            mViewDataBinding.data = data
            mImageLoader.load(data.image).errorHolderId(0).into(mViewDataBinding.ivTest)
        }

    }
}