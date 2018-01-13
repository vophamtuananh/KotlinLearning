package com.vophamtuananh.base.recyclerview

import android.support.v7.util.DiffUtil

/**
 * Created by vophamtuananh on 1/12/18.
 */
class DiffUtilCallback<in T>(private val mOldItems: List<T>,
                             private val mNewItems: List<T>,
                             private val mComparator: ItemComparator<T>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = mOldItems[oldItemPosition]
        val newItem = mNewItems[newItemPosition]
        return oldItem != null && newItem != null && mComparator.areItemsTheSame(oldItem,
                newItem)
    }

    override fun getOldListSize(): Int {
        return mOldItems.size
    }

    override fun getNewListSize(): Int {
        return mNewItems.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = mOldItems[oldItemPosition]
        val newItem = mNewItems[newItemPosition]
        return oldItem != null && newItem != null && mComparator.areContentsTheSame(mOldItems[oldItemPosition],
                mNewItems[newItemPosition])
    }
}