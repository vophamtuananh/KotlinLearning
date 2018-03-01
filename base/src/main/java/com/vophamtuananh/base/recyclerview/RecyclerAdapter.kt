package com.vophamtuananh.base.recyclerview

import android.databinding.ViewDataBinding
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.View
import android.view.ViewGroup
import com.vophamtuananh.base.utils.ListUtil
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

/**
 * Created by vophamtuananh on 1/12/18.
 */
abstract class RecyclerAdapter<VH : RecyclerAdapter.BaseHolder<*, T>, T>(private var mOnItemClickListener: OnItemClickListener<T>? = null,
                                                                         private var mComparator: ItemComparator<T>? = null) : RecyclerView.Adapter<VH>() {

    private val itemList = ArrayList<T>()

    private var mCalculateDiffDisposable: Disposable? = null

    private var mAdapterDataObserver: RecyclerView.AdapterDataObserver? = null

    protected abstract fun getViewHolder(parent: ViewGroup, viewType: Int): VH?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH? {
        val viewHolder = getViewHolder(parent, viewType)
        if (viewHolder != null && mOnItemClickListener != null) {
            viewHolder.mViewDataBinding.root.setOnClickListener { view ->
                val pos = viewHolder.adapterPosition
                if (pos != NO_POSITION) {
                    mOnItemClickListener!!.onItemClick(view, pos, itemList[pos])
                }
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindData(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun registerDataObserver(adapterDataObserver: RecyclerView.AdapterDataObserver) {
        if (mAdapterDataObserver != null)
            unregisterAdapterDataObserver(mAdapterDataObserver)
        mAdapterDataObserver = adapterDataObserver
        registerAdapterDataObserver(mAdapterDataObserver)
    }

    private fun unRegisterDataObserver() {
        if (mAdapterDataObserver != null)
            unregisterAdapterDataObserver(mAdapterDataObserver)
        mAdapterDataObserver = null
    }

    fun update(items: List<T>) {
        if (ListUtil.isNotEmpty(itemList) && mComparator != null) {
            updateDiffItemsOnly(items)
        } else {
            updateAllItems(items)
        }
    }

    fun appenItems(items: List<T>?) {
        if (itemList.isEmpty()) {
            updateAllItems(items!!)
        } else {
            if (items != null && !items.isEmpty()) {
                val positionStart = itemList.size
                itemList.addAll(items)
                notifyItemRangeInserted(positionStart, items.size)
            }
        }
    }

    private fun updateAllItems(items: List<T>) {
        updateItemsInModel(items)
        notifyDataSetChanged()
    }

    private fun updateDiffItemsOnly(items: List<T>) {
        if (mCalculateDiffDisposable != null && !mCalculateDiffDisposable!!.isDisposed)
            mCalculateDiffDisposable!!.dispose()
        mCalculateDiffDisposable = Single.fromCallable { calculateDiff(items) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { updateItemsInModel(items) }
                .subscribe { result -> updateAdapterWithDiffResult(result) }
    }

    private fun calculateDiff(newItems: List<T>): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(DiffUtilCallback(itemList, newItems, mComparator!!))
    }

    private fun updateItemsInModel(items: List<T>) {
        itemList.clear()
        itemList.addAll(items)
    }

    private fun updateAdapterWithDiffResult(result: DiffUtil.DiffResult) {
        result.dispatchUpdatesTo(this)
    }

    protected fun getData(): List<T> {
        return itemList
    }

    open fun release() {
        mOnItemClickListener = null
        unRegisterDataObserver()
    }

    open class BaseHolder<out V : ViewDataBinding, in T>(val mViewDataBinding: V) : RecyclerView.ViewHolder(mViewDataBinding.root) {

        open fun bindData(data: T) {}
    }

    interface OnItemClickListener<in T> {
        fun onItemClick(v: View, position: Int, data: T)
    }
}