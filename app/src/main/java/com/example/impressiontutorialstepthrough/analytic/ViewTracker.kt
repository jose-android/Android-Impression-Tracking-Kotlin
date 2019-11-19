package com.example.impressiontutorialstepthrough.analytic

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.impressiontutorialstepthrough.model.DataTracking

class ViewTracker {
    var recyclerView: RecyclerView? = null
    var listener: Listener? = null
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var firstTrackFlag: Boolean = false
    private var viewsViewed: ArrayList<Int> = arrayListOf()
    private var trackingData: ArrayList<TrackingData> = arrayListOf()
    private var minimumVisibleHeightThreshold: Double = 100.0

    fun startTracking() {
        recyclerView?.viewTreeObserver?.addOnGlobalLayoutListener {
            if (!firstTrackFlag) {
                startTime = System.currentTimeMillis()

                val firstVisibleItemPosition = findFirstVisibleItemPosition()
                val lastVisibleItemPosition = findLastVisibleItemPosition()

                analyzeAndAddViewData(firstVisibleItemPosition, lastVisibleItemPosition)

                firstTrackFlag = true
            }
        }

        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                /**
                 * User is scrolling, calculate and store the tracking
                 * data of the views that were being viewed
                 * before the scroll.
                 */
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    endTime = System.currentTimeMillis()

                    for (trackedViewsCount in viewsViewed.indices) {
                        trackingData.add(prepareTrackingData(viewsViewed[trackedViewsCount].toString(),(endTime - startTime) / 1000))
                    }
                    /**
                     * We clear the list of current item positions.
                     * If we don't do this, the items will be tracked
                     * every time the new items are added.
                     */
                    viewsViewed.clear()
                }

                /**
                 *  Scrolling has ended, start the tracking
                 *  process by assigning a start time
                 *  and maintaining a list of views being viewed.
                 */
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    startTime = System.currentTimeMillis()
                    val firstVisibleItemPosition = findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = findLastVisibleItemPosition()

                    analyzeAndAddViewData(firstVisibleItemPosition,lastVisibleItemPosition)
                }
            }
        })
    }
    fun stopTracking() {
        endTime = System.currentTimeMillis()
        val firstVisibleItemPosition = findFirstVisibleItemPosition()
        val lastVisibleItemPosition = findLastVisibleItemPosition()

        analyzeAndAddViewData(firstVisibleItemPosition, lastVisibleItemPosition)

        for (trackedViewsCount in viewsViewed.indices) {
            trackingData.add(prepareTrackingData(viewsViewed[trackedViewsCount].toString(),(endTime - startTime) / 1000))
            viewsViewed.clear()
        }
    }

    private fun analyzeAndAddViewData(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int) {
        val adapter = recyclerView?.adapter
        val list = if (adapter is TrackerAdapter) adapter.data else emptyList()
        for (viewPosition in firstVisibleItemPosition..lastVisibleItemPosition) {
            // Log.i("View being considered", viewPosition.toString())
            val itemView = recyclerView?.layoutManager?.findViewByPosition(viewPosition)
            itemView?.let {
                if (getVisibleHeightPercentage(it) >= minimumVisibleHeightThreshold) {
                    if (list.isNotEmpty() && !list[viewPosition].tracked) {
                        viewsViewed.add(viewPosition)
                        list[viewPosition].tracked = true
                    }
                }
            }
        }

        listener?.viewsViewed(viewsViewed)
    }
    private fun getVisibleHeightPercentage(view: View): Double {
        val itemRect = Rect()
        view.getLocalVisibleRect(itemRect)
        val visibleHeight = itemRect.height()
        val height = view.measuredHeight

        // Log.i("Visible Height", visibleHeight.toString())
        // Log.i("Measured Height", height.toString())
        val viewVisibleHeightPercentage = visibleHeight / height * 100

        // Log.i("Percentage visible", viewVisibleHeightPercentage.toString())

        // Log.i("___", "___")

        return viewVisibleHeightPercentage.toDouble()
    }

    private fun prepareTrackingData(viewId: String, viewDuration: Long): TrackingData {
        val trackingData = TrackingData()

        trackingData.viewId = viewId
        trackingData.viewDuration = viewDuration

        return trackingData
    }

    private fun findFirstVisibleItemPosition(): Int {
        return when(val lm = recyclerView?.layoutManager) {
            is LinearLayoutManager -> lm.findFirstVisibleItemPosition()
            is GridLayoutManager -> lm.findFirstVisibleItemPosition()
            else -> 0
        }
    }

    private fun findLastVisibleItemPosition(): Int {
        return when(val lm = recyclerView?.layoutManager) {
            is LinearLayoutManager -> lm.findLastVisibleItemPosition()
            is GridLayoutManager -> lm.findLastVisibleItemPosition()
            else -> 0
        }
    }

    interface Listener {
        fun viewsViewed(viewsViewed: ArrayList<Int>)
    }
}