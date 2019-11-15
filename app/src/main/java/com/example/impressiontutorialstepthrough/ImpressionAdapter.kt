package com.example.impressiontutorialstepthrough

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Created by User on 1/18/16.
 */
class ImpressionAdapter(activity: Activity, private val mDataSet: List<String>) : RecyclerView.Adapter<ProductViewHolder>() {
    private var mVisibilityTracker: VisibilityTracker
    private val mViewPositionMap = WeakHashMap<View, Int>()

    init {
        mVisibilityTracker = VisibilityTracker(activity)

        mVisibilityTracker.setVisibilityTrackerListener(object : VisibilityTracker.VisibilityTrackerListener {
            override fun onVisibilityChanged(visibleViews: List<View>, invisibleViews: List<View>) {
                handleVisibleViews(visibleViews)
            }
        })
    }

    private fun handleVisibleViews(visibleViews: List<View>) {
        Log.d(ImpressionAdapter::class.java.getSimpleName(), "Currently visible views \n")
        for (v in visibleViews) {
            val viewPosition = mViewPositionMap[v]
            val viewTitle = mDataSet[viewPosition!!]
            Log.d(ImpressionAdapter::class.java.getSimpleName(), viewTitle)
        }

        Log.d(ImpressionAdapter::class.java.getSimpleName(), "------------------------------")

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ProductViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.product_item_layout, viewGroup, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(productViewHolder: ProductViewHolder, position: Int) {
        val title = mDataSet[position]

        productViewHolder.itemView.setBackgroundResource(if (position % 2 == 0) android.R.color.white else android.R.color.darker_gray)
        productViewHolder.mTitleTextView.text = title

        mViewPositionMap[productViewHolder.itemView] = position
        mVisibilityTracker.addView(productViewHolder.itemView, 0)
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }
}
