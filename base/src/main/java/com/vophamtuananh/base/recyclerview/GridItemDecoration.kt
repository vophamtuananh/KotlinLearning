package com.vophamtuananh.base.recyclerview

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by vophamtuananh on 1/14/18.
 */
class GridItemDecoration(private val mSpacing: Int = 0,
                         private val mSpanCount: Int = 0) : RecyclerView.ItemDecoration() {

    private var mNeedLeftSpacing = false

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val frameWidth = ((parent.width - mSpacing.toFloat() * (mSpanCount - 1)) / mSpanCount).toInt()
        val padding = parent.width / mSpanCount - frameWidth
        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        if (itemPosition < mSpanCount) {
            outRect.top = 0
        } else {
            outRect.top = 2 * mSpacing
        }
        if (itemPosition % mSpanCount == 0) {
            outRect.left = 0
            outRect.right = padding
            mNeedLeftSpacing = true
        } else if ((itemPosition + 1) % mSpanCount == 0) {
            mNeedLeftSpacing = false
            outRect.right = 0
            outRect.left = padding
        } else if (mNeedLeftSpacing) {
            mNeedLeftSpacing = false
            outRect.left = mSpacing - padding
            if ((itemPosition + 2) % mSpanCount == 0) {
                outRect.right = mSpacing - padding
            } else {
                outRect.right = mSpacing / 2
            }
        } else if ((itemPosition + 2) % mSpanCount == 0) {
            mNeedLeftSpacing = false
            outRect.left = mSpacing / 2
            outRect.right = mSpacing - padding
        } else {
            mNeedLeftSpacing = false
            outRect.left = mSpacing / 2
            outRect.right = mSpacing / 2
        }
        outRect.bottom = 0
    }
}