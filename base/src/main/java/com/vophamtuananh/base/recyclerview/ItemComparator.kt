package com.vophamtuananh.base.recyclerview

/**
 * Created by vophamtuananh on 1/12/18.
 */
interface ItemComparator<in T> {

    fun areItemsTheSame(item1: T, item2: T): Boolean

    fun areContentsTheSame(item1: T, item2: T): Boolean
}