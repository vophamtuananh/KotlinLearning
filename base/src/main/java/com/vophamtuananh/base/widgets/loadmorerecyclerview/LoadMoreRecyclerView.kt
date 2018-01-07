package com.vophamtuananh.base.widgets.loadmorerecyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

/**
 * Created by vophamtuananh on 1/7/18.
 */
class LoadMoreRecyclerView : RecyclerView {

    companion object {
        private val NUMBER_TO_LOAD_MORE_DEFAULT = 1
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {

    }


}