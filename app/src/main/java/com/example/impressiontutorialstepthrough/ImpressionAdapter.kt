package com.example.impressiontutorialstepthrough

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.impressiontutorialstepthrough.analytic.TrackerAdapter
import com.example.impressiontutorialstepthrough.model.DataModel
import com.example.impressiontutorialstepthrough.model.DataTracking
import java.util.*

/**
 * Created by User on 1/18/16.
 */
class ImpressionAdapter(activity: Activity, private val mDataSet: List<DataModel>) : RecyclerView.Adapter<ProductViewHolder>(), TrackerAdapter {
    private var mVisibilityTracker: VisibilityTracker = VisibilityTracker(activity)
    private val mViewPositionMap = WeakHashMap<View, Int>()

    init {
        mVisibilityTracker.setVisibilityTrackerListener(object : VisibilityTracker.VisibilityTrackerListener {
            override fun onVisibilityChanged(visibleViews: List<View>, invisibleViews: List<View>) {
                handleVisibleViews(visibleViews)
            }
        })
    }

    private fun handleVisibleViews(visibleViews: List<View>) {
        Log.d(ImpressionAdapter::class.java.simpleName, "Currently visible views \n")
        for (v in visibleViews) {
            val viewPosition = mViewPositionMap[v]
            val viewTitle = mDataSet[viewPosition!!]
            Log.d(ImpressionAdapter::class.java.simpleName, viewTitle.nombre ?: "")
        }

        Log.d(ImpressionAdapter::class.java.simpleName, "------------------------------")

    }

    override var data: List<DataTracking> =  mDataSet

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ProductViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.product_item_layout, viewGroup, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(productViewHolder: ProductViewHolder, position: Int) {
        val title = mDataSet[position]

        productViewHolder.itemView.setBackgroundResource(if (position % 2 == 0) android.R.color.white else android.R.color.darker_gray)
        productViewHolder.mTitleTextView.text = title.nombre

        mViewPositionMap[productViewHolder.itemView] = position
        mVisibilityTracker.addView(productViewHolder.itemView, 100)
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }
}
