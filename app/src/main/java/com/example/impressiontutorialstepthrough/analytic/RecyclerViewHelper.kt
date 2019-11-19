package com.example.impressiontutorialstepthrough.analytic


import android.view.View

import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Dawid Drozd aka Gelldur on 02.02.16.
 * Modeled on https://gist.github.com/mipreamble/b6d4b3d65b0b4775a22e
 */
object RecyclerViewHelper {

    fun isScrolledToBottom(recyclerView: RecyclerView): Boolean {
        val layoutManager = recyclerView.layoutManager
        val child = findOneVisibleChild(recyclerView, layoutManager!!.childCount - 1, -1, false, true)
                ?: return false

        if (recyclerView.getChildAdapterPosition(child) + 1 < recyclerView.childCount) {
            return false
        }

        val helper: OrientationHelper
        helper = if (layoutManager.canScrollVertically()) {
            OrientationHelper.createVerticalHelper(layoutManager)
        } else {
            OrientationHelper.createHorizontalHelper(layoutManager)
        }

        val childEnd = helper.getDecoratedEnd(child)
        return childEnd <= helper.endAfterPadding
    }

    /**
     * Returns the adapter position of the first visible view. This position does not include
     * adapter changes that were dispatched after the last layout pass.
     *
     * @return The adapter position of the first visible item or [RecyclerView.NO_POSITION] if
     * there aren't any visible items.
     */
    fun findFirstVisibleItemPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager
        val child = findOneVisibleChild(recyclerView, 0, layoutManager!!.childCount, false, true)
        return if (child == null) RecyclerView.NO_POSITION else recyclerView.getChildAdapterPosition(child)
    }

    /**
     * Returns the adapter position of the first fully visible view. This position does not include
     * adapter changes that were dispatched after the last layout pass.
     *
     * @return The adapter position of the first fully visible item or
     * [RecyclerView.NO_POSITION] if there aren't any visible items.
     */
    fun findFirstCompletelyVisibleItemPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager
        val child = findOneVisibleChild(recyclerView, 0, layoutManager!!.childCount, completelyVisible = true, acceptPartiallyVisible = false)
        return if (child == null) RecyclerView.NO_POSITION else recyclerView.getChildAdapterPosition(child)
    }

    /**
     * Returns the adapter position of the last visible view. This position does not include
     * adapter changes that were dispatched after the last layout pass.
     *
     * @return The adapter position of the last visible view or [RecyclerView.NO_POSITION] if
     * there aren't any visible items
     */
    fun findLastVisibleItemPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager
        val child = findOneVisibleChild(recyclerView, layoutManager!!.childCount - 1, -1, false, true)
        return if (child == null) RecyclerView.NO_POSITION else recyclerView.getChildAdapterPosition(child)
    }

    /**
     * Returns the adapter position of the last fully visible view. This position does not include
     * adapter changes that were dispatched after the last layout pass.
     *
     * @return The adapter position of the last fully visible view or
     * [RecyclerView.NO_POSITION] if there aren't any visible items.
     */
    fun findLastCompletelyVisibleItemPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager
        val child = findOneVisibleChild(recyclerView, layoutManager!!.childCount - 1, -1, true, false)
        return if (child == null) RecyclerView.NO_POSITION else recyclerView.getChildAdapterPosition(child)
    }

    private fun findOneVisibleChild(recyclerView: RecyclerView, fromIndex: Int, toIndex: Int,
                                    completelyVisible: Boolean, acceptPartiallyVisible: Boolean): View? {
        val layoutManager = recyclerView.layoutManager
        val helper: OrientationHelper
        if (layoutManager!!.canScrollVertically()) {
            helper = OrientationHelper.createVerticalHelper(layoutManager)
        } else {
            helper = OrientationHelper.createHorizontalHelper(layoutManager)
        }

        val start = helper.startAfterPadding
        val end = helper.endAfterPadding
        val next = if (toIndex > fromIndex) 1 else -1
        var partiallyVisible: View? = null
        var i = fromIndex
        while (i != toIndex) {
            val child = layoutManager.getChildAt(i)
            val childStart = helper.getDecoratedStart(child)
            val childEnd = helper.getDecoratedEnd(child)
            if (childStart < end && childEnd > start) {
                if (completelyVisible) {
                    if (childStart >= start && childEnd <= end) {
                        return child
                    } else if (acceptPartiallyVisible && partiallyVisible == null) {
                        partiallyVisible = child
                    }
                } else {
                    return child
                }
            }
            i += next
        }
        return partiallyVisible
    }
}