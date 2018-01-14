package com.vophamtuananh.base.recyclerview

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by vophamtuananh on 1/14/18.
 */
class VerticalLinearItemDecoration(private val mLeftSpace: Int = 0,
                                   private val mTopSpace: Int = 0,
                                   private val mRightSpace: Int = 0,
                                   private val mBottomSpace: Int = 0) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {

        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition

        outRect.bottom = 0
        outRect.left = mLeftSpace
        outRect.top = mTopSpace
        outRect.right = mRightSpace

        if (itemPosition == parent.adapter.itemCount - 1) {
            outRect.bottom = mBottomSpace
        }

    }
}